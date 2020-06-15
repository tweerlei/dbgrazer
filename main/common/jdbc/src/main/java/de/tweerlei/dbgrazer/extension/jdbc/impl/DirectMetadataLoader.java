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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataLoader;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;

/**
 * Non-caching impl.
 * 
 * @author Robert Wruck
 */
@Service("jdbcMetadataLoader")
@Scope("prototype")
public class DirectMetadataLoader implements MetadataLoader
	{
	private final DataAccessService dataAccessService;
	
	private String link;
	
	/**
	 * Constructor
	 * @param dataAccessService DataAccessService
	 */
	@Autowired
	public DirectMetadataLoader(DataAccessService dataAccessService)
		{
		this.dataAccessService = dataAccessService;
		}
	
	@Override
	public void setLink(String link)
		{
		if (this.link != null)
			throw new IllegalStateException("Link already set");
		
		this.link = link;
		}
	
	@Override
	public String getLink()
		{
		return (this.link);
		}
	
	@Override
	public Map<String, String> getDBInfo()
		{
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		
		final JdbcTemplate template = getJdbcTemplate();
		if (template == null)
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final DatabaseMetaData md = con.getMetaData();
				ret.put("databaseVersion", md.getDatabaseMajorVersion() + "." + md.getDatabaseMinorVersion());
				ret.put("databaseName", md.getDatabaseProductName());
				ret.put("databaseVersionString", md.getDatabaseProductVersion());
				
				ret.put("driverVersion", md.getDriverMajorVersion() + "." + md.getDriverMinorVersion());
				ret.put("driverName", md.getDriverName());
				ret.put("driverVersionString", md.getDriverVersion());
				
				ret.put("jdbcVersion", md.getJDBCMajorVersion() + "." + md.getJDBCMinorVersion());
				
				ret.put("catalogTerm", md.getCatalogTerm());
				ret.put("schemaTerm", md.getSchemaTerm());
				ret.put("procedureTerm", md.getProcedureTerm());
				ret.put("catalogSeparator", md.getCatalogSeparator());
				ret.put("identifierQuoteString", md.getIdentifierQuoteString());
				ret.put("searchStringEscape", md.getSearchStringEscape());
				
				return null;
				}
			});
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> getCatalogs()
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				ret.addAll(r.getCatalogNames());
				return (null);
				}
			});
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> getSchemas(String catalog)
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				ret.addAll(r.getSchemaNames(catalog));
				return (null);
				}
			});
		
		return (ret);
		}
	
	@Override
	public SortedMap<QualifiedName, String> getTables(final String catalog, final String schema)
		{
		final SortedMap<QualifiedName, String> ret = new TreeMap<QualifiedName, String>();
		
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				for (Map.Entry<String, String> ent : r.getTables(catalog, schema).entrySet())
					ret.put(new QualifiedName(catalog, schema, ent.getKey()), ent.getValue());
				return (null);
				}
			});
		
		return (ret);
		}
	
	@Override
	public TableDescription getTableInfo(final QualifiedName table)
		{
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (null);
		
		final TableDescription ret = (TableDescription) template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				return (r.getTableDescription(table.getCatalogName(), table.getSchemaName(), table.getObjectName()));
				}
			});
		
		return (ret);
		}
	
	@Override
	public Set<TableDescription> getTableInfos(final Set<QualifiedName> tables, final Set<QualifiedName> missing, final ProgressMonitor p)
		{
		final Set<TableDescription> ret = new HashSet<TableDescription>();
		
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				for (QualifiedName table : tables)
					{
					try	{
						ret.add(r.getTableDescription(table.getCatalogName(), table.getSchemaName(), table.getObjectName()));
						if (p != null)
							p.progress(1);
						}
					catch (SQLException e)
						{
						if (missing != null)
							missing.add(table);
						else
							throw e;
						}
					}
				return (null);
				}
			});
		
		return (ret);
		}
	
	@Override
	public Set<TableDescription> getTableInfoRecursive(final QualifiedName table, final int depth, final boolean all, final boolean toplevel, final ProgressMonitor p)
		{
		final Set<TableDescription> ret = new HashSet<TableDescription>();
		
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (ret);
		
		template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final MetadataReader r = dialect.getMetadataReader(con);
				
				recurse(r, table, depth, toplevel);
				
				return (null);
				}
			
			private void recurse(MetadataReader r, QualifiedName qn, int d, boolean includeReferencing) throws SQLException, DataAccessException
				{
				final TableDescription td = r.getTableDescription(qn.getCatalogName(), qn.getSchemaName(), qn.getObjectName());
				if (td == null)
					return;
				
				if (ret.contains(td))
					return;
				
				ret.add(td);
				if (p != null)
					p.progress(1);
				
				if (d <= 0)
					return;
				
				if (includeReferencing)
					{
					// Don't include additional tables referencing our referenced tables
					for (ForeignKeyDescription fk : td.getReferencingKeys())
						{
						if (all || qn.hasSameSchema(fk.getTableName()))
							recurse(r, fk.getTableName(), d - 1, false);
						}
					}
				for (ForeignKeyDescription fk : td.getReferencedKeys())
					{
					if (all || qn.hasSameSchema(fk.getTableName()))
						recurse(r, fk.getTableName(), d - 1, false);
					}
				}
			});
		
		return (ret);
		}
	
	@Override
	public SQLExecutionPlan analyzeStatement(final String stmt, final List<Object> params)
		{
		final JdbcTemplate template = getJdbcTemplate();
		final SQLDialect dialect = getSQLDialect();
		if ((template == null) || (dialect == null))
			return (null);
		
		final SQLExecutionPlan ret = (SQLExecutionPlan) template.execute(new ConnectionCallback()
			{
			@Override
			public Object doInConnection(Connection con) throws SQLException, DataAccessException
				{
				final SQLStatementAnalyzer a = dialect.getStatementAnalyzer(con);
				return a.analyzeStatement(stmt, params);
				}
			});
		
		return (ret);
		}
	
	private JdbcTemplate getJdbcTemplate()
		{
		return (dataAccessService.getUnlimitedJdbcTemplate(link));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (dataAccessService.getSQLDialect(link));
		}
	}
