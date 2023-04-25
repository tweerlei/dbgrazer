/*
 * Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.ObjectComparators;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.common5.util.ObjectUtils;
import de.tweerlei.dbgrazer.extension.jdbc.ConfigKeys;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.OrderBy;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.model.CompareHandler;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.DiffCompareHandler;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.ResultDiffService;
import de.tweerlei.dbgrazer.web.service.jdbc.ResultCompareService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLObjectDDLWriter;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
@Service
public class ResultCompareServiceImpl implements ResultCompareService
	{
	private static class FilteredCompareHandler implements CompareHandler
		{
		private final CompareHandler other;
		private final CompareFlags flags;
		
		public FilteredCompareHandler(CompareHandler other, CompareFlags flags)
			{
			this.other = other;
			this.flags = flags;
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			if (flags.useInsert)
				other.rowAdded(tableName, columns, values, pk);
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			if (flags.useUpdate)
				return (other.rowChanged(tableName, columns, oldValues, newValues, pk));
			else
				return (!oldValues.getValues().equals(newValues.getValues()));
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			if (flags.useDelete)
				other.rowRemoved(tableName, columns, values, pk);
			}
		
		@Override
		public void flush()
			{
			other.flush();
			}
		}
	
	private static abstract class LoggingCompareHandler implements CompareHandler
		{
		private QualifiedName lastTableName;
		private List<ColumnDef> lastColumns;
		private Set<Integer> lastPK;
		private ResultRow firstRow;
		private ResultRow lastRow;
		
		protected LoggingCompareHandler()
			{
			}
		
		protected void logRow(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			lastTableName = tableName;
			lastColumns = columns;
			lastPK = pk;
			if (firstRow == null)
				firstRow = values;
			lastRow = values;
			}
		
		protected QualifiedName getLastTableName()
			{
			return lastTableName;
			}
		
		protected List<ColumnDef> getLastColumns()
			{
			return lastColumns;
			}
		
		protected Set<Integer> getLastPK()
			{
			return lastPK;
			}
		
		protected String getLogMessage()
			{
			if (firstRow == null)
				return (null);

			final StringBuilder sb = new StringBuilder();
			
			sb.append("First difference at PK (");
			boolean first = true;
			for (Integer i : lastPK)
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(String.valueOf(firstRow.getValues().get(i)));
				}
			sb.append(")\nLast difference at PK (");
			first = true;
			for (Integer i : lastPK)
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(String.valueOf(lastRow.getValues().get(i)));
				}
			sb.append(")");
			
			return (sb.toString());
			}
		}
	
	private static class InsertCompareHandler extends LoggingCompareHandler
		{
		private final SQLWriter sqlWriter;
		
		public InsertCompareHandler(SQLWriter sqlWriter)
			{
			this.sqlWriter = sqlWriter;
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			logRow(tableName, columns, values, pk);
			sqlWriter.writeInsert(tableName, columns, values);
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			if (sqlWriter.writeUpdate(tableName, columns, oldValues, newValues, pk))
				{
				logRow(tableName, columns, newValues, pk);
				return (true);
				}
			return (false);
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			logRow(tableName, columns, values, pk);
			sqlWriter.writeDelete(tableName, columns, values, pk);
			}
		
		@Override
		public void flush()
			{
			final String log = getLogMessage();
			if (log != null)
				sqlWriter.writeComment(log);
			}
		}
	
	private static class MergeCompareHandler extends LoggingCompareHandler
		{
		private final SQLWriter sqlWriter;
		private final int blockSize;
		private final List<ResultRow> rowsToMerge;
		
		public MergeCompareHandler(SQLWriter sqlWriter, int blockSize)
			{
			this.sqlWriter = sqlWriter;
			this.blockSize = blockSize;
			this.rowsToMerge = new LinkedList<ResultRow>();
			}
		
		private void addRow(ResultRow values)
			{
			rowsToMerge.add(values);
			
			if (rowsToMerge.size() >= blockSize)
				flushRows();
			}
		
		private void flushRows()
			{
			if (!rowsToMerge.isEmpty())
				{
				sqlWriter.writeMerge(getLastTableName(), getLastColumns(), rowsToMerge, getLastPK());
				rowsToMerge.clear();
				}
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			logRow(tableName, columns, values, pk);
			addRow(values);
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			if (sqlWriter.checkUpdate(oldValues, newValues, pk))
				{
				logRow(tableName, columns, newValues, pk);
				addRow(newValues);
				return (true);
				}
			return (false);
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			logRow(tableName, columns, values, pk);
			sqlWriter.writeDelete(tableName, columns, values, pk);
			}
		
		@Override
		public void flush()
			{
			flushRows();
			
			final String log = getLogMessage();
			if (log != null)
				sqlWriter.writeComment(log);
			}
		}
	
	private static class DirectCompareHandler extends LoggingCompareHandler
		{
		private final RowHandler insert;
		private final RowHandler update;
		private final RowHandler delete;
		
		public DirectCompareHandler(RowHandler insert, RowHandler update, RowHandler delete)
			{
			this.insert = insert;
			this.update = update;
			this.delete = delete;
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			if ((insert != null) && insert.handleRow(values))
				logRow(tableName, columns, values, pk);
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			if (oldValues.getValues().equals(newValues.getValues()))
				return (false);
			
			if ((update != null) && update.handleRow(newValues))
				{
				logRow(tableName, columns, newValues, pk);
				return (true);
				}
			return (false);
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			if ((delete != null) && delete.handleRow(values))
				logRow(tableName, columns, values, pk);
			}
		
		@Override
		public void flush()
			{
			}
		}
	
	private static class DDLCompareHandler implements CompareHandler
		{
		private static final int SRC_CATALOG = 0;
		private static final int SRC_SCHEMA = 1;
		private static final int SRC_NAME = 2;
		private static final int SRC_TYPE = 3;
		private static final int SRC_LINE = 4;
		
		private final StatementHandler handler;
		private final SQLObjectDDLWriter writer;
		
		public DDLCompareHandler(StatementHandler handler, SQLObjectDDLWriter writer)
			{
			this.handler = handler;
			this.writer = writer;
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			handler.statement(writer.createObject(
					(String) values.getValues().get(SRC_CATALOG),
					(String) values.getValues().get(SRC_SCHEMA),
					(String) values.getValues().get(SRC_NAME),
					(String) values.getValues().get(SRC_TYPE),
					(String) values.getValues().get(SRC_LINE)
					));
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			final String oldLine = (String) oldValues.getValues().get(SRC_LINE);
			final String newLine = (String) newValues.getValues().get(SRC_LINE);
			
			if (ObjectUtils.equals(oldLine, newLine))
				return (false);
			
			final String catalog = (String) newValues.getValues().get(SRC_CATALOG);
			final String schema = (String) newValues.getValues().get(SRC_SCHEMA);
			final String name = (String) newValues.getValues().get(SRC_NAME);
			final String type = (String) newValues.getValues().get(SRC_TYPE);
			
			if (writer.canReplaceObject(catalog, schema, name, type))
				handler.statement(writer.replaceObject(catalog, schema, name, type, newLine));
			else
				{
				handler.statement(writer.dropObject(catalog, schema, name, type));
				handler.statement(writer.createObject(catalog, schema, name, type, newLine));
				}
			
			return (true);
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			handler.statement(writer.dropObject(
					(String) values.getValues().get(SRC_CATALOG),
					(String) values.getValues().get(SRC_SCHEMA),
					(String) values.getValues().get(SRC_NAME),
					(String) values.getValues().get(SRC_TYPE)
					));
			}
		
		@Override
		public void flush()
			{
			}
		}
	
	private static class PrivilegeCompareHandler implements CompareHandler
		{
		private static final int SRC_CATALOG = 0;
		private static final int SRC_SCHEMA = 1;
		private static final int SRC_NAME = 2;
		private static final int SRC_TYPE = 3;
		private static final int SRC_GRANTEE = 4;
		private static final int SRC_PRIVILEGE = 5;
		private static final int SRC_IS_GRANTABLE = 6;
		
		private final StatementHandler handler;
		private final SQLObjectDDLWriter writer;
		
		public PrivilegeCompareHandler(StatementHandler handler, SQLObjectDDLWriter writer)
			{
			this.handler = handler;
			this.writer = writer;
			}
		
		@Override
		public void rowAdded(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			handler.statement(writer.grantObjectPrivilege(
					(String) values.getValues().get(SRC_CATALOG),
					(String) values.getValues().get(SRC_SCHEMA),
					(String) values.getValues().get(SRC_NAME),
					(String) values.getValues().get(SRC_TYPE),
					(String) values.getValues().get(SRC_GRANTEE),
					(String) values.getValues().get(SRC_PRIVILEGE),
					((String) values.getValues().get(SRC_IS_GRANTABLE)).equals("YES")
					));
			}
		
		@Override
		public boolean rowChanged(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
			{
			final String oldLine = (String) oldValues.getValues().get(SRC_IS_GRANTABLE);
			final String newLine = (String) newValues.getValues().get(SRC_IS_GRANTABLE);
			
			if (ObjectUtils.equals(oldLine, newLine))
				return (false);
			
			rowRemoved(tableName, columns, oldValues, pk);
			rowAdded(tableName, columns, newValues, pk);
			
			return (true);
			}
		
		@Override
		public void rowRemoved(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
			{
			handler.statement(writer.revokeObjectPrivilege(
					(String) values.getValues().get(SRC_CATALOG),
					(String) values.getValues().get(SRC_SCHEMA),
					(String) values.getValues().get(SRC_NAME),
					(String) values.getValues().get(SRC_TYPE),
					(String) values.getValues().get(SRC_GRANTEE),
					(String) values.getValues().get(SRC_PRIVILEGE)
					));
			}
		
		@Override
		public void flush()
			{
			}
		}
	
	private static interface ResultRowFetcher
		{
		public ResultRow fetch();
		
		public List<ColumnDef> getColumns();
		}
	
	private static class SimpleResultRowFetcher implements ResultRowFetcher
		{
		private final RowIterator iter;
		
		public SimpleResultRowFetcher(RowIterator iter)
			{
			this.iter = iter;
			}
		
		@Override
		public ResultRow fetch()
			{
			if (!iter.hasNext())
				return (null);
			
			return (iter.next());
			}
		
		@Override
		public List<ColumnDef> getColumns()
			{
			return (iter.getColumns());
			}
		}
	
	private static class AggregateResultRowFetcher implements ResultRowFetcher
		{
		private final RowIterator iter;
		private final Set<Integer> pk;
		private ResultRow lastRow;
		
		public AggregateResultRowFetcher(RowIterator iter, Set<Integer> pk)
			{
			this.iter = iter;
			this.pk = pk;
			}
		
		@Override
		public ResultRow fetch()
			{
			if (lastRow == null)
				{
				if (!iter.hasNext())
					return (null);
				
				lastRow = iter.next();
				}
			
			final boolean[] isPK = new boolean[lastRow.getValues().size()];
			final Object[] tmp = new Object[lastRow.getValues().size()];
			
			for (int i = 0; i < isPK.length; i++)
				{
				final Object value = lastRow.getValues().get(i);
				if (pk.contains(i))
					{
					tmp[i] = value;
					isPK[i] = true;
					}
				else
					{
					final StringBuilder sb = new StringBuilder();
					if (value != null)
						sb.append(value);
					tmp[i] = sb;
					isPK[i] = false;
					}
				}
			
			for (;;)
				{
				if (!iter.hasNext())
					{
					lastRow = null;
					break;
					}
				
				lastRow = iter.next();
				
				boolean match = true;
				for (int i = 0; i < isPK.length; i++)
					{
					if (isPK[i] && !ObjectUtils.equals(tmp[i], lastRow.getValues().get(i)))
						{
						match = false;
						break;
						}
					}
				
				if (!match)
					break;
				
				for (int i = 0; i < isPK.length; i++)
					{
					final Object value = lastRow.getValues().get(i);
					if (!isPK[i] && (value != null))
						{
						final StringBuilder sb = (StringBuilder) tmp[i];
						sb.append(value);
						}
					}
				}
			
			final ResultRow row = new DefaultResultRow(isPK.length);
			
			for (int i = 0; i < isPK.length; i++)
				{
				if (isPK[i])
					row.getValues().add(tmp[i]);
				else
					row.getValues().add(tmp[i].toString());
				}
			
			return (row);
			}
		
		@Override
		public List<ColumnDef> getColumns()
			{
			return (iter.getColumns());
			}
		}
	
	private final ConfigAccessor configService;
	private final DataFormatterFactory dataFormatterFactory;
	private final ResultDiffService resultDiffService;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param resultDiffService ResultDiffService
	 */
	@Autowired
	public ResultCompareServiceImpl(ConfigAccessor configService, DataFormatterFactory dataFormatterFactory, ResultDiffService resultDiffService)
		{
		this.configService = configService;
		this.dataFormatterFactory = dataFormatterFactory;
		this.resultDiffService = resultDiffService;
		}
	
	@Override
	public void compareResults(RowSet l, RowSet r, StatementHandler h, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect, CompareFlags flags)
		{
		final SQLWriter sqlWriter = dataFormatterFactory.getSQLWriter(h, dialect, true);
		final CompareHandler ch;
		if (flags.useMerge && dialect.supportsMerge())
			ch = new MergeCompareHandler(sqlWriter, configService.get(ConfigKeys.MERGE_ROWS));
		else
			ch = new InsertCompareHandler(sqlWriter);
		
		resultDiffService.compareResults(l, r, new FilteredCompareHandler(ch, flags), monitor, tableDesc.getName(), tableDesc.getPKColumns());
		}
	
	@Override
	public RowSet compareResults(RowSet l, RowSet r, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect)
		{
		final DiffCompareHandler ch = new DiffCompareHandler(l.getRows().isEmpty() ? r : l);
		
		resultDiffService.compareResults(l, r, ch, monitor, tableDesc.getName(), tableDesc.getPKColumns());
		
		return (ch.getRowSet());
		}
	
	@Override
	public void compareResultsByPK(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect, OrderBy order, CompareFlags flags)
		{
		final SQLWriter sqlWriter = dataFormatterFactory.getSQLWriter(h, dialect, true);
		final CompareHandler ch;
		if (flags.useMerge && dialect.supportsMerge() && (order == OrderBy.PK))
			ch = new MergeCompareHandler(sqlWriter, configService.get(ConfigKeys.MERGE_ROWS));
		else
			ch = new InsertCompareHandler(sqlWriter);
		
		compareResults(new SimpleResultRowFetcher(l), new SimpleResultRowFetcher(r), new FilteredCompareHandler(ch, flags), monitor, tableDesc.getName(), tableDesc.getPKColumns(), order == OrderBy.PK);
		}
	
	@Override
	public void compareResultsByPK(RowIterator l, RowIterator r, RowHandler insert, RowHandler update, RowHandler delete, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect)
		{
		compareResults(new SimpleResultRowFetcher(l), new SimpleResultRowFetcher(r), new DirectCompareHandler(insert, update, delete), monitor, tableDesc.getName(), tableDesc.getPKColumns(), true);
		}
	
	@Override
	public void compareDDLSource(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, SQLDialect dialect)
		{
		final SQLObjectDDLWriter w = dialect.getObjectDDLWriter();
		final TableDescription tableDesc = w.getFindObjectSourceTableDescription();
		final Set<Integer> pk = tableDesc.getPKColumns();
		
		compareResults(new AggregateResultRowFetcher(l, pk), new AggregateResultRowFetcher(r, pk), new DDLCompareHandler(h, w), monitor, null, pk, true);
		}
	
	@Override
	public void compareDDLPrivileges(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, SQLDialect dialect)
		{
		final SQLObjectDDLWriter w = dialect.getObjectDDLWriter();
		final TableDescription tableDesc = w.getFindObjectPrivilegesTableDescription();
		final Set<Integer> pk = tableDesc.getPKColumns();
		
		compareResults(new SimpleResultRowFetcher(l), new SimpleResultRowFetcher(r), new PrivilegeCompareHandler(h, w), monitor, null, pk, true);
		}
	
	private void compareResults(ResultRowFetcher l, ResultRowFetcher r, CompareHandler h, CompareProgressMonitor monitor, QualifiedName tableName, Set<Integer> pk, boolean compareByPK)
		{
		ResultRow ll = l.fetch();
		ResultRow rl = r.fetch();
		
		for (; (ll != null) || (rl != null);)
			{
			if (ll == null)
				{
				// no more src rows
				monitor.getDestinationRows().progress(1);
				h.rowAdded(tableName, r.getColumns(), rl, pk);
				monitor.getInsertedRows().progress(1);
				rl = r.fetch();
				}
			else if (rl == null)
				{
				// no more dst rows
				monitor.getSourceRows().progress(1);
				h.rowRemoved(tableName, l.getColumns(), ll, pk);
				monitor.getDeletedRows().progress(1);
				ll = l.fetch();
				}
			else
				{
				final int d;
				if (compareByPK)
					d = comparePK(ll.getValues(), pk, rl.getValues(), pk);
				else
					d = compareData(ll.getValues(), pk, rl.getValues(), pk);
				
				if (d < 0)
					{
					// src must catch up
					monitor.getSourceRows().progress(1);
					h.rowRemoved(tableName, l.getColumns(), ll, pk);
					monitor.getDeletedRows().progress(1);
					ll = l.fetch();
					}
				else if (d > 0)
					{
					// dst must catch up
					monitor.getDestinationRows().progress(1);
					h.rowAdded(tableName, r.getColumns(), rl, pk);
					monitor.getInsertedRows().progress(1);
					rl = r.fetch();
					}
				else
					{
					// PK match
					monitor.getSourceRows().progress(1);
					monitor.getDestinationRows().progress(1);
					if (compareByPK)
						{
						if (h.rowChanged(tableName, r.getColumns(), ll, rl, pk))
							monitor.getUpdatedRows().progress(1);
						else
							monitor.getMatchedRows().progress(1);
						}
					else
						monitor.getMatchedRows().progress(1);
					ll = l.fetch();
					rl = r.fetch();
					}
				}
			}
		
		h.flush();
		}
	
	private int comparePK(List<Object> row1, Set<Integer> pk1, List<Object> row2, Set<Integer> pk2)
		{
		final Iterator<Integer> i1 = pk1.iterator();
		final Iterator<Integer> i2 = pk2.iterator();
		while (i1.hasNext() && i2.hasNext())
			{
			final int d = compareNullsLast(row1.get(i1.next()), row2.get(i2.next()));
			if (d != 0)
				return (d);
			}
		return (0);
		}
	
	private int compareData(List<Object> row1, Set<Integer> pk1, List<Object> row2, Set<Integer> pk2)
		{
		final int n = row1.size();
		final int m = row2.size();
		
		for (int i = 0, j = 0; i < n && j < m; )
			{
			if (pk1.contains(i))
				{
				i++;
				continue;
				}
			if (pk2.contains(j))
				{
				j++;
				continue;
				}
			
			final int d = compareNullsLast(row1.get(i), row2.get(j));
			if (d != 0)
				return (d);
			
			i++;
			j++;
			}
		
		return (0);
		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int compareNullsLast(Object a, Object b)
		{
		final Comparable ta = (Comparable) a;
		final Comparable tb = (Comparable) b;
		
		return (ObjectComparators.compareNullsLast(ta, tb));
		}
	}
