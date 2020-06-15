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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
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
public class TableEditController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String newName;
		private String objectComment;
		private boolean primaryKey;
		private String name;
		private String comment;
		private boolean defaultPresent;
		private String defaultValue;
		private boolean nullable;
		private String typeName;
		private int length;
		private int decimals;
		
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
		 * @return the comment
		 */
		public String getComment()
			{
			return comment;
			}
		
		/**
		 * @param comment the comment to set
		 */
		public void setComment(String comment)
			{
			this.comment = comment;
			}
		
		/**
		 * @return the defaultValue
		 */
		public String getDefaultValue()
			{
			return defaultValue;
			}
		
		/**
		 * @param defaultValue the defaultValue to set
		 */
		public void setDefaultValue(String defaultValue)
			{
			this.defaultValue = defaultValue;
			}
		
		/**
		 * @return the nullable
		 */
		public boolean isNullable()
			{
			return nullable;
			}
		
		/**
		 * @param nullable the nullable to set
		 */
		public void setNullable(boolean nullable)
			{
			this.nullable = nullable;
			}
		
		/**
		 * @return the typeName
		 */
		public String getTypeName()
			{
			return typeName;
			}
		
		/**
		 * @param typeName the typeName to set
		 */
		public void setTypeName(String typeName)
			{
			this.typeName = typeName;
			}
		
		/**
		 * @return the length
		 */
		public int getLength()
			{
			return length;
			}
		
		/**
		 * @param length the length to set
		 */
		public void setLength(int length)
			{
			this.length = length;
			}
		
		/**
		 * @return the decimals
		 */
		public int getDecimals()
			{
			return decimals;
			}
		
		/**
		 * @param decimals the decimals to set
		 */
		public void setDecimals(int decimals)
			{
			this.decimals = decimals;
			}

		/**
		 * @return the defaultPresent
		 */
		public boolean isDefaultPresent()
			{
			return defaultPresent;
			}
		
		/**
		 * @param defaultPresent the defaultPresent to set
		 */
		public void setDefaultPresent(boolean defaultPresent)
			{
			this.defaultPresent = defaultPresent;
			}
		
		/**
		 * @return the objectComment
		 */
		public String getObjectComment()
			{
			return objectComment;
			}
		
		/**
		 * @param objectComment the objectComment to set
		 */
		public void setObjectComment(String objectComment)
			{
			this.objectComment = objectComment;
			}
		
		/**
		 * @return the primaryKey
		 */
		public boolean isPrimaryKey()
			{
			return primaryKey;
			}
		
		/**
		 * @param primaryKey the primaryKey to set
		 */
		public void setPrimaryKey(boolean primaryKey)
			{
			this.primaryKey = primaryKey;
			}
		
		/**
		 * @return the newName
		 */
		public String getNewName()
			{
			return newName;
			}
		
		/**
		 * @param newName the newName to set
		 */
		public void setNewName(String newName)
			{
			this.newName = newName;
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
	public TableEditController(MetadataService metadataService,
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
		return (fbo);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/ddl-create-table.html", method = RequestMethod.GET)
	public Map<String, Object> showCreateDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-create-table.html", method = RequestMethod.POST)
	public Map<String, Object> performCreateQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final ColumnDescription cd = new ColumnDescription(fbo.getName(), fbo.getComment(), 0, fbo.getTypeName(), fbo.getLength(), fbo.getDecimals(), fbo.isNullable(), fbo.isDefaultPresent() ? fbo.getDefaultValue() : null);
		final PrimaryKeyDescription pk = fbo.isPrimaryKey() ? new PrimaryKeyDescription("PK_"+fbo.getObject(), Collections.singleton(fbo.getName())) : null;
		final TableDescription info = new TableDescription(fbo.getCatalog(), fbo.getSchema(), fbo.getObject(), fbo.getObjectComment(), TableDescription.TABLE, pk, Collections.singleton(cd), null, null, null, null);
		
		final Query q = queryGeneratorService.createCreateTableQuery(info, getSQLDialect());
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
	@RequestMapping(value = "/db/*/ajax/ddl-alter-table.html", method = RequestMethod.GET)
	public Map<String, Object> showAlterDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		fbo.setNewName(fbo.getObject());
		fbo.setComment(info.getComment());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-alter-table.html", method = RequestMethod.POST)
	public Map<String, Object> performAlterQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final TableDescription t = new TableDescription(fbo.getCatalog(), fbo.getSchema(), fbo.getNewName(), fbo.getComment(), info.getType(), info.getPrimaryKey(), info.getColumns(), info.getIndices(), info.getReferencedKeys(), info.getReferencingKeys(), info.getPrivileges());
		
		final Query q = queryGeneratorService.createAlterTableQuery(info, getSQLDialect(), t);
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
	@RequestMapping(value = "/db/*/ajax/ddl-drop-table.html", method = RequestMethod.GET)
	public Map<String, Object> showDropDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-drop-table.html", method = RequestMethod.POST)
	public Map<String, Object> performDropQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final Query q = queryGeneratorService.createDropTableQuery(info, getSQLDialect());
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
