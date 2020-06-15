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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
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
public class IndexEditController
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
		private boolean unique;
		private final Map<Integer, String> columns;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.columns = new TreeMap<Integer, String>();
			}
		
		/**
		 * Get the column names
		 * @return column names
		 */
		public List<String> getColumnNames()
			{
			final List<String> ret = new ArrayList<String>(columns.size());
			
			for (int i = 0; ; i++)
				{
				final String s = columns.get(i);
				if (StringUtils.empty(s))
					break;
				ret.add(s);
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
		 * @return the unique
		 */
		public boolean isUnique()
			{
			return unique;
			}
		
		/**
		 * @param unique the unique to set
		 */
		public void setUnique(boolean unique)
			{
			this.unique = unique;
			}
		
		/**
		 * @return the columns
		 */
		public Map<Integer, String> getColumns()
			{
			return columns;
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
	public IndexEditController(MetadataService metadataService,
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
			fbo.getColumns().put(i, "");
		return (fbo);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/ddl-add-index.html", method = RequestMethod.GET)
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
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-add-index.html", method = RequestMethod.POST)
	public Map<String, Object> performInsertQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final IndexDescription id = new IndexDescription(fbo.getName(), fbo.isUnique(), fbo.getColumnNames());
		
		final Query q = queryGeneratorService.createAddIndexQuery(info, getSQLDialect(), id);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.emptyMap());
		
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
	@RequestMapping(value = "/db/*/ajax/ddl-drop-index.html", method = RequestMethod.GET)
	public Map<String, Object> showDeleteDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final IndexDescription id = info.getIndex(fbo.getName());
		
		fbo.setUnique(id.isUnique());
		int i = 0;
		for (String c : id.getColumns())
			fbo.getColumns().put(i++, c);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-drop-index.html", method = RequestMethod.POST)
	public Map<String, Object> performDeleteQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final IndexDescription id = info.getIndex(fbo.getName());
		
		final Query q = queryGeneratorService.createDropIndexQuery(info, getSQLDialect(), id);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.emptyMap());
		
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
	@RequestMapping(value = "/db/*/ajax/ddl-add-primarykey.html", method = RequestMethod.GET)
	public Map<String, Object> showPKInsertDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		model.put("columns", info.getColumns());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-add-primarykey.html", method = RequestMethod.POST)
	public Map<String, Object> performPKInsertQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final PrimaryKeyDescription pk = new PrimaryKeyDescription(fbo.getName(), fbo.getColumnNames());
		
		final Query q = queryGeneratorService.createAddPrimaryKeyQuery(info, getSQLDialect(), pk);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.emptyMap());
		
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
	@RequestMapping(value = "/db/*/ajax/ddl-drop-primarykey.html", method = RequestMethod.GET)
	public Map<String, Object> showPKDeleteDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final PrimaryKeyDescription pk = info.getPrimaryKey();
		
		fbo.setUnique(pk.isUnique());
		int i = 0;
		for (String c : pk.getColumns())
			fbo.getColumns().put(i++, c);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-drop-primarykey.html", method = RequestMethod.POST)
	public Map<String, Object> performPKDeleteQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final PrimaryKeyDescription pk = info.getPrimaryKey();
		
		final Query q = queryGeneratorService.createDropPrimaryKeyQuery(info, getSQLDialect(), pk);
		final QueryParameters query = querySettingsManager.prepareParameters(q, Collections.emptyMap());
		
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
