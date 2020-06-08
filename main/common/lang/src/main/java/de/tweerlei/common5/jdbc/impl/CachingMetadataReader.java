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
package de.tweerlei.common5.jdbc.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ProcedureDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Proxy implementation that caches results
 * 
 * @author Robert Wruck
 */
public class CachingMetadataReader implements MetadataReader
	{
	private final MetadataReader target;
	private String defaultCatalog;
	private String defaultSchema;
	private List<String> catalogs;
	private List<String> schemas;
	private final Map<String, Map<String, String>> tables;
	private final Map<String, Map<String, String>> procedures;
	private final Map<String, Map<String, Integer>> types;
	private final Map<String, TableDescription> tableDescs;
	private final Map<String, ProcedureDescription> procedureDescs;
	
	/**
	 * Constructor
	 * @param target MetadataReader to cache
	 */
	public CachingMetadataReader(MetadataReader target)
		{
		this.target = target;
		tables = new ConcurrentHashMap<String, Map<String, String>>();
		procedures = new ConcurrentHashMap<String, Map<String, String>>();
		types = new ConcurrentHashMap<String, Map<String, Integer>>();
		tableDescs = new ConcurrentHashMap<String, TableDescription>();
		procedureDescs = new ConcurrentHashMap<String, ProcedureDescription>();
		}
	
	/**
	 * Flush any cached data
	 */
	public void flush()
		{
		defaultCatalog = null;
		defaultSchema = null;
		catalogs = null;
		schemas = null;
		tables.clear();
		procedures.clear();
		types.clear();
		tableDescs.clear();
		procedureDescs.clear();
		}
	
	public String getDefaultCatalogName() throws SQLException
		{
		if (defaultCatalog == null)
			defaultCatalog = target.getDefaultCatalogName();
		return (defaultCatalog);
		}
	
	public String getDefaultSchemaName() throws SQLException
		{
		if (defaultSchema == null)
			defaultSchema = target.getDefaultSchemaName();
		return (defaultSchema);
		}
	
	public List<String> getCatalogNames() throws SQLException
		{
		List<String> ret = catalogs;
		if (ret == null)
			{
			ret = target.getCatalogNames();
			catalogs = ret;
			}
		return (new ArrayList<String>(ret));
		}
	
	public List<String> getSchemaNames() throws SQLException
		{
		List<String> ret = schemas;
		if (ret == null)
			{
			ret = target.getSchemaNames();
			schemas = ret;
			}
		return (new ArrayList<String>(ret));
		}
	
	public Map<String, String> getTables(String catalog, String schema) throws SQLException
		{
		final String key = catalog + "." + schema;
		Map<String, String> ret = tables.get(key);
		if (ret == null)
			{
			ret = target.getTables(catalog, schema);
			tables.put(key, ret);
			}
		return (new LinkedHashMap<String, String>(ret));
		}
	
	public Map<String, String> getProcedures(String catalog, String schema) throws SQLException
		{
		final String key = catalog + "." + schema;
		Map<String, String> ret = procedures.get(key);
		if (ret == null)
			{
			ret = target.getProcedures(catalog, schema);
			procedures.put(key, ret);
			}
		return (new LinkedHashMap<String, String>(ret));
		}
	
	public Map<String, Integer> getDatatypes(String catalog, String schema) throws SQLException
		{
		final String key = catalog + "." + schema;
		Map<String, Integer> ret = types.get(key);
		if (ret == null)
			{
			ret = target.getDatatypes(catalog, schema);
			types.put(key, ret);
			}
		return (new LinkedHashMap<String, Integer>(ret));
		}
	
	public TableDescription getTableDescription(String catalog, String schema, String table) throws SQLException
		{
		final String key = catalog + "." + schema + "." + table;
		TableDescription ret = tableDescs.get(key);
		if (ret == null)
			{
			ret = target.getTableDescription(catalog, schema, table);
			tableDescs.put(key, ret);
			}
		return (ret);
		}
	
	public ProcedureDescription getProcedureDescription(String catalog, String schema, String proc) throws SQLException
		{
		final String key = catalog + "." + schema + "." + proc;
		ProcedureDescription ret = procedureDescs.get(key);
		if (ret == null)
			{
			ret = target.getProcedureDescription(catalog, schema, proc);
			procedureDescs.put(key, ret);
			}
		return (ret);
		}
	}
