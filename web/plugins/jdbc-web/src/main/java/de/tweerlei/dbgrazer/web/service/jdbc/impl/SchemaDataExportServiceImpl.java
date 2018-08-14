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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.common5.collections.Pair;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.OrderBy;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.jdbc.SchemaDataExportService;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Export data from SQLSchema objects
 * 
 * @author Robert Wruck
 */
@Service
public class SchemaDataExportServiceImpl implements SchemaDataExportService
	{
	private static final class RecursiveExporter
		{
		private static enum CacheMode
			{
			PARENT,
			CHILD,
			NONE
			}
		
		private final SQLGeneratorService sqlGeneratorService;
		private final QueryPerformerService runner;
		private final String link;
		private final SQLDialect dialect;
		private final Set<TableDescription> infos;
		private final RowSetHandler handler;
		private final Map<QualifiedName, Set<List<Object>>> rowCache;
		private final List<Pair<TableDescription, RowSet>> resultCache;
		private int count;
		private final Logger logger;
		
		public RecursiveExporter(SQLGeneratorService sqlGeneratorService, QueryPerformerService runner,
				String link, SQLDialect dialect, Set<TableDescription> infos, RowSetHandler handler)
			{
			this.sqlGeneratorService = sqlGeneratorService;
			this.runner = runner;
			this.link = link;
			this.dialect = dialect;
			this.infos = infos;
			this.handler = handler;
			this.rowCache = new HashMap<QualifiedName, Set<List<Object>>>();
			this.resultCache = new LinkedList<Pair<TableDescription, RowSet>>();
			this.logger = Logger.getLogger(getClass().getCanonicalName());
			}
		
		public int export(QualifiedName startTable, String where, TraversalMode mode) throws PerformQueryException
			{
			final TableDescription info = getTableDescription(startTable);
			
			final String stmt = sqlGeneratorService.generateSelect(info, Style.SIMPLE, where, null, dialect);
			
			recurse(info, stmt, Collections.emptyList(), null, mode, CacheMode.NONE);
			
			flushCache();
			
			return (count);
			}
		
		private void recurse(TableDescription info, String stmt, List<Object> params, QualifiedName source, TraversalMode tmode, CacheMode mode) throws PerformQueryException
			{
			final RowSet rs = fetchAndCache(info, stmt, params);
			
			if (rs.getRows().isEmpty())
				return;
			
			if (tmode != TraversalMode.STARTING_ONLY)
				{
				for (ForeignKeyDescription fk : info.getReferencedKeys())
					{
					if ((source == null) || !fk.getTableName().hasSameName(source))
						{
						final TableDescription next = getTableDescription(fk.getTableName());
						if (next != null)
							{
							final Map<Integer, Integer> fkColumns = new HashMap<Integer, Integer>();
							for (Map.Entry<String, String> ent : fk.getColumns().entrySet())
								fkColumns.put(getColumnIndex(next, ent.getValue()), getColumnIndex(info, ent.getKey()));
							
							fetchByCriteria(rs, next, fkColumns, info.getName(), (tmode == TraversalMode.CHILDREN) ? TraversalMode.PARENTS : tmode, (mode == CacheMode.CHILD) ? CacheMode.PARENT : mode);
							}
						}
					}
				}
			
			switch (mode)
				{
				case CHILD:
					resultCache.add(new Pair<TableDescription, RowSet>(info, rs));
					break;
				case PARENT:
//					resultCache.add(0, new Pair<TableDescription, RowSet>(info, rs));
					handler.handleRowSet(info, rs);
					break;
				case NONE:
					handler.handleRowSet(info, rs);
					flushCache();
					break;
				}
			
			if (tmode != TraversalMode.STARTING_ONLY && tmode != TraversalMode.PARENTS)
				{
				for (ForeignKeyDescription fk : info.getReferencingKeys())
					{
					if ((source == null) || !fk.getTableName().hasSameName(source))
						{
						final TableDescription next = getTableDescription(fk.getTableName());
						if (next != null)
							{
							final Map<Integer, Integer> fkColumns = new HashMap<Integer, Integer>();
							for (Map.Entry<String, String> ent : fk.getColumns().entrySet())
								fkColumns.put(getColumnIndex(next, ent.getKey()), getColumnIndex(info, ent.getValue()));
							
							fetchByCriteria(rs, next, fkColumns, info.getName(), (tmode == TraversalMode.SIBLINGS) ? TraversalMode.CHILDREN : tmode, CacheMode.CHILD);
							}
						}
					}
				}
			
			count++;
			}
		
		private void flushCache()
			{
			for (Pair<TableDescription, RowSet> p : resultCache)
				handler.handleRowSet(p.getLeft(), p.getRight());
			resultCache.clear();
			}
		
		private void fetchByCriteria(RowSet rs, TableDescription info, Map<Integer, Integer> columns, QualifiedName source, TraversalMode tmode, CacheMode mode) throws PerformQueryException
			{
			final String stmt = sqlGeneratorService.generateSelectIn(info, Style.SIMPLE, columns.keySet(), rs.getRows().size(), OrderBy.PK, dialect);
			
			final List<Object> params = new ArrayList<Object>(rs.getRows().size() * columns.size());
			for (ResultRow row : rs.getRows())
				{
				for (Integer i : columns.values())
					params.add(row.getValues().get(i));
				}
			
			recurse(info, stmt, params, source, tmode, mode);
			}
		
		private RowSet fetchAndCache(TableDescription info, String stmt, List<Object> params) throws PerformQueryException
			{
			final Result r = runner.performCustomQuery(link, JdbcConstants.QUERYTYPE_MULTIPLE, stmt, null, params, "export", false, null);
			
			final RowSet rs = r.getFirstRowSet();
			
			logger.log(Level.INFO, "Fetched " + rs.getRows().size() + " from " + info.getName());
			
			final Set<Integer> pk = info.getPKColumns();
			if (!pk.isEmpty())
				{
				Set<List<Object>> ids = rowCache.get(info.getName());
				if (ids == null)
					{
					ids = new HashSet<List<Object>>();
					rowCache.put(info.getName(), ids);
					}
				
				for (Iterator<ResultRow> it = rs.getRows().iterator(); it.hasNext(); )
					{
					final ResultRow row = it.next();
					final List<Object> id = new ArrayList<Object>(pk.size());
					for (Integer i : pk)
						id.add(row.getValues().get(i));
					
					if (!ids.add(id))
						it.remove();	// row already in cache, remove from result
					}
				}
			
			return (rs);
			}
		
		private int getColumnIndex(TableDescription t, String c)
			{
			int i = 0;
			for (ColumnDescription cd : t.getColumns())
				{
				if (cd.getName().equals(c))
					return (i);
				i++;
				}
			return (-1);
			}
		
		private TableDescription getTableDescription(QualifiedName qn)
			{
			for (TableDescription info : infos)
				{
				if (info.getName().hasSameName(qn))
					return (info);
				}
			
			return (null);
			}
		}
	
	private final SQLGeneratorService sqlGeneratorService;
	private final QueryPerformerService runner;
	
	/**
	 * Constructor
	 * @param sqlGeneratorService SQLGeneratorService
	 * @param runner QueryPerformerService
	 */
	@Autowired
	public SchemaDataExportServiceImpl(SQLGeneratorService sqlGeneratorService, QueryPerformerService runner)
		{
		this.sqlGeneratorService = sqlGeneratorService;
		this.runner = runner;
		}
	
	@Override
	public int export(String link, SQLDialect dialect, Set<TableDescription> infos, QualifiedName startTable, String where, TraversalMode mode, RowSetHandler handler) throws PerformQueryException
		{
		final RecursiveExporter exp = new RecursiveExporter(sqlGeneratorService, runner, link, dialect, infos, handler);
		
		return (exp.export(startTable, where, mode));
		}
	}
