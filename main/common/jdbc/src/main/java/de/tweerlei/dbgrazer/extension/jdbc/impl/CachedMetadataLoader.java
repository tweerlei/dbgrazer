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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataLoader;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;

/**
 * Caching impl.
 * 
 * @author Robert Wruck
 */
public class CachedMetadataLoader implements MetadataLoader
	{
	private static final TableDescription MISSING = new TableDescription(null, null, null, null, null, null, null, null, null, null, null);
	
	private final MetadataLoader delegate;
	private final Logger logger;
	
	private Map<String, String> dbInfo;
	private SortedSet<String> catalogs;
	private SortedSet<String> schemas;
	private final Map<String, SortedMap<QualifiedName, String>> tableNames;
	private final Map<QualifiedName, TableDescription> tableDescs;
	
	/**
	 * Constructor
	 * @param delegate MetadataLoader
	 */
	@Autowired
	public CachedMetadataLoader(MetadataLoader delegate)
		{
		this.delegate = delegate;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.tableNames = new ConcurrentHashMap<String, SortedMap<QualifiedName, String>>();
		this.tableDescs = new ConcurrentHashMap<QualifiedName, TableDescription>();
		}
	
	@Override
	public void setLink(String link)
		{
		delegate.setLink(link);
		}
	
	@Override
	public String getLink()
		{
		return (delegate.getLink());
		}
	
	private void putTable(QualifiedName key, TableDescription value)
		{
		tableDescs.put(key.normalize(), value);
		}
	
	private TableDescription getTable(QualifiedName key)
		{
		return (tableDescs.get(key.normalize()));
		}
	
	@Override
	public Map<String, String> getDBInfo()
		{
		if (dbInfo == null)
			{
			logger.log(Level.INFO, "Fetching DB info for " + delegate.getLink());
			dbInfo = delegate.getDBInfo();
			}
		return (new LinkedHashMap<String, String>(dbInfo));
		}
	
	@Override
	public SortedSet<String> getCatalogs()
		{
		if (catalogs == null)
			{
			logger.log(Level.INFO, "Fetching catalogs for " + delegate.getLink());
			catalogs = delegate.getCatalogs();
			}
		return (new TreeSet<String>(catalogs));
		}
	
	@Override
	public SortedSet<String> getSchemas()
		{
		if (schemas == null)
			{
			logger.log(Level.INFO, "Fetching schemas for " + delegate.getLink());
			schemas = delegate.getSchemas();
			}
		return (new TreeSet<String>(schemas));
		}
	
	@Override
	public SortedMap<QualifiedName, String> getTables(String catalog, String schema)
		{
		final String key = catalog + "/" + schema;
		SortedMap<QualifiedName, String> ret = tableNames.get(key);
		if (ret == null)
			{
			logger.log(Level.INFO, "Fetching tables " + key + " for " + delegate.getLink());
			ret = delegate.getTables(catalog, schema);
			tableNames.put(key, ret);
			}
		return (new TreeMap<QualifiedName, String>(ret));
		}
	
	@Override
	public TableDescription getTableInfo(QualifiedName table)
		{
		TableDescription ret = getTable(table);
		if ((ret == null) || (ret == MISSING))
			{
			logger.log(Level.INFO, "Fetching table " + table + " for " + delegate.getLink());
			ret = delegate.getTableInfo(table);
			putTable(table, ret);
			}
		return (ret);
		}
	
	@Override
	public Set<TableDescription> getTableInfos(Set<QualifiedName> tables, Set<QualifiedName> missing, ProgressMonitor p)
		{
		final Set<TableDescription> ret = new HashSet<TableDescription>();
		Set<QualifiedName> remaining = null;
		
		for (QualifiedName qn : tables)
			{
			final TableDescription td = getTable(qn);
			if (td == MISSING)
				{
				if (missing != null)
					missing.add(qn);
				}
			else if (td != null)
				{
				ret.add(td);
				if (p != null)
					p.progress(1);
				}
			else
				{
				if (remaining == null)
					remaining = new HashSet<QualifiedName>();
				remaining.add(qn);
				}
			}
		
		if (remaining != null)
			{
			logger.log(Level.INFO, "Fetching " + remaining.size() + " table infos for " + delegate.getLink());
			
			final Set<QualifiedName> tmp2 = new HashSet<QualifiedName>();
			final Set<TableDescription> tmp = delegate.getTableInfos(remaining, tmp2, p);
			for (QualifiedName qn : tmp2)
				putTable(qn, MISSING);
			for (TableDescription td : tmp)
				putTable(td.getName(), td);
			ret.addAll(tmp);
			}
		
		return (ret);
		}
	
	@Override
	public Set<TableDescription> getTableInfoRecursive(QualifiedName table, int depth, boolean all, boolean toplevel, ProgressMonitor p)
		{
		final Set<TableDescription> ret = new HashSet<TableDescription>();
		
		recurse(ret, table, depth, all, toplevel, p);
		
		return (ret);
		}
	
	private void recurse(Set<TableDescription> ret, QualifiedName qn, int d, boolean all, boolean includeReferencing, ProgressMonitor p)
		{
		final TableDescription td = getTable(qn);
		if (td == MISSING)
			return;
		
		if (td == null)
			{
			logger.log(Level.INFO, "Fetching recursive table infos " + qn + " for " + delegate.getLink());
			
			final Set<TableDescription> tmp = delegate.getTableInfoRecursive(qn, d, all, includeReferencing, p);
			for (TableDescription td2 : tmp)
				putTable(td2.getName(), td2);
			ret.addAll(tmp);
			return;
			}
		
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
					recurse(ret, fk.getTableName(), d - 1, all, false, p);
				}
			}
		for (ForeignKeyDescription fk : td.getReferencedKeys())
			{
			if (all || qn.hasSameSchema(fk.getTableName()))
				recurse(ret, fk.getTableName(), d - 1, all, false, p);
			}
		}
	
	@Override
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params)
		{
		return (delegate.analyzeStatement(stmt, params));
		}
	}
