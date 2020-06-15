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
package de.tweerlei.dbgrazer.web.controller.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.QueryGeneratorService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class ForeignKeyEditController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String name;
		private String catalog2;
		private String schema2;
		private String object2;
		private final Map<Integer, String> fromColumns;
		private final Map<Integer, String> toColumns;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.fromColumns = new TreeMap<Integer, String>();
			this.toColumns = new TreeMap<Integer, String>();
			}
		
		/**
		 * Get the column names
		 * @return column names
		 */
		public Map<String, String> getColumnMappings()
			{
			final Map<String, String> ret = new LinkedHashMap<String, String>(fromColumns.size());
			
			for (int i = 0; ; i++)
				{
				final String f = fromColumns.get(i);
				final String t = toColumns.get(i);
				if (StringUtils.empty(f) || StringUtils.empty(t))
					break;
				ret.put(f, t);
				}
			
			return (ret);
			}
		
		/**
		 * @return the catalog
		 */
		public String getCatalog()
			{
			return catalog;
			}
		
		/**
		 * @param catalog the catalog to set
		 */
		public void setCatalog(String catalog)
			{
			this.catalog = catalog;
			}
		
		/**
		 * @return the schema
		 */
		public String getSchema()
			{
			return schema;
			}
		
		/**
		 * @param schema the schema to set
		 */
		public void setSchema(String schema)
			{
			this.schema = schema;
			}
		
		/**
		 * @return the object
		 */
		public String getObject()
			{
			return object;
			}
		
		/**
		 * @param object the object to set
		 */
		public void setObject(String object)
			{
			this.object = object;
			}
		
		/**
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}
		
		/**
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}
		
		/**
		 * @return the catalog2
		 */
		public String getCatalog2()
			{
			return catalog2;
			}
		
		/**
		 * @param catalog2 the catalog2 to set
		 */
		public void setCatalog2(String catalog2)
			{
			this.catalog2 = catalog2;
			}
		
		/**
		 * @return the schema2
		 */
		public String getSchema2()
			{
			return schema2;
			}
		
		/**
		 * @param schema2 the schema2 to set
		 */
		public void setSchema2(String schema2)
			{
			this.schema2 = schema2;
			}
		
		/**
		 * @return the object2
		 */
		public String getObject2()
			{
			return object2;
			}
		
		/**
		 * @param object2 the object2 to set
		 */
		public void setObject2(String object2)
			{
			this.object2 = object2;
			}
		
		/**
		 * @return the columns
		 */
		public Map<Integer, String> getFromColumns()
			{
			return fromColumns;
			}
		
		/**
		 * @return the columns
		 */
		public Map<Integer, String> getToColumns()
			{
			return toColumns;
			}
		}
	
	private final MetadataService metadataService;
	private final QuerySettingsManager querySettingsManager;
	private final QueryPerformerService runner;
	private final QueryGeneratorService queryGeneratorService;
	private final ConnectionSettings connectionSettings;
	private final FrontendHelperService frontendHelperService;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param querySettingsManager QuerySettingsManager
	 * @param runner QueryPerformerService
	 * @param queryGeneratorService QueryGeneratorService
	 * @param connectionSettings ConnectionSettings
	 * @param frontendHelperService FrontendHelperService
	 */
	@Autowired
	public ForeignKeyEditController(MetadataService metadataService,
			QuerySettingsManager querySettingsManager, QueryPerformerService runner,
			QueryGeneratorService queryGeneratorService, ConnectionSettings connectionSettings,
			FrontendHelperService frontendHelperService)
		{
		this.metadataService = metadataService;
		this.querySettingsManager = querySettingsManager;
		this.runner = runner;
		this.queryGeneratorService = queryGeneratorService;
		this.connectionSettings = connectionSettings;
		this.frontendHelperService = frontendHelperService;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param obj DB object name
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(@RequestParam(value = "q", required = false) String obj)
		{
		final FormBackingObject fbo = new FormBackingObject();
		// Hack to recognize object name in "q" as well as "object" parameter
		fbo.setObject(obj);
		for (int i = 0; i < 10; i++)
			fbo.getFromColumns().put(i, "");
		for (int i = 0; i < 10; i++)
			fbo.getToColumns().put(i, "");
		return (fbo);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/ddl-add-foreignkey.html", method = RequestMethod.GET)
	public Map<String, Object> showInsertDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		model.put("columns", info.getColumns());
		
		model.put("catalogs", metadataService.getCatalogs(connectionSettings.getLinkName()));
		
		model.put("catalog", fbo.getCatalog());
		model.put("schemas", metadataService.getSchemas(connectionSettings.getLinkName(), fbo.getCatalog()));
		model.put("schema", fbo.getSchema());
		model.put("objects", metadataService.getTables(connectionSettings.getLinkName(), fbo.getCatalog(), fbo.getSchema()).keySet());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-add-foreignkey.html", method = RequestMethod.POST)
	public Map<String, Object> performInsertQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final ForeignKeyDescription fd = new ForeignKeyDescription(fbo.getName(), fbo.getCatalog2(), fbo.getSchema2(), fbo.getObject2(), fbo.getColumnMappings());
		
		final Query q = queryGeneratorService.createAddForeignKeyQuery(info, getSQLDialect(), fd);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.<Integer, String>emptyMap());
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), query);
			metadataService.flushCache(connectionSettings.getLinkName());
			model.put("result", frontendHelperService.toJSONString(String.valueOf(r.getFirstRowSet().getFirstValue())));
			model.put("exceptionText", null);
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/ddl-drop-foreignkey.html", method = RequestMethod.GET)
	public Map<String, Object> showDeleteDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final ForeignKeyDescription fd = info.getReferencedKey(fbo.getName());
		
		fbo.setCatalog2(fd.getTableName().getCatalogName());
		fbo.setSchema2(fd.getTableName().getSchemaName());
		fbo.setObject2(fd.getTableName().getObjectName());
		int i = 0;
		for (Map.Entry<String, String> ent : fd.getColumns().entrySet())
			{
			fbo.getFromColumns().put(i, ent.getKey());
			fbo.getToColumns().put(i++, ent.getValue());
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-drop-foreignkey.html", method = RequestMethod.POST)
	public Map<String, Object> performDeleteQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final ForeignKeyDescription fd = info.getReferencedKey(fbo.getName());
		
		final Query q = queryGeneratorService.createDropForeignKeyQuery(info, getSQLDialect(), fd);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.<Integer, String>emptyMap());
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), query);
			metadataService.flushCache(connectionSettings.getLinkName());
			model.put("result", frontendHelperService.toJSONString(String.valueOf(r.getFirstRowSet().getFirstValue())));
			model.put("exceptionText", null);
			}
		catch (PerformQueryException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
