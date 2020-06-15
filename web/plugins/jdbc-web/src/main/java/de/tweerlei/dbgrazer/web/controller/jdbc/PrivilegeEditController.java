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

import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
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
public class PrivilegeEditController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String privilege;
		private String grantee;
		private boolean grantable;
		
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
		 * @return the privilege
		 */
		public String getPrivilege()
			{
			return privilege;
			}
		
		/**
		 * @param privilege the privilege to set
		 */
		public void setPrivilege(String privilege)
			{
			this.privilege = privilege;
			}
		
		/**
		 * @return the grantee
		 */
		public String getGrantee()
			{
			return grantee;
			}
		
		/**
		 * @param grantee the grantee to set
		 */
		public void setGrantee(String grantee)
			{
			this.grantee = grantee;
			}
		
		/**
		 * @return the grantable
		 */
		public boolean isGrantable()
			{
			return grantable;
			}
		
		/**
		 * @param grantable the grantable to set
		 */
		public void setGrantable(boolean grantable)
			{
			this.grantable = grantable;
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
	public PrivilegeEditController(MetadataService metadataService,
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
	@RequestMapping(value = "/db/*/ajax/ddl-grant.html", method = RequestMethod.GET)
	public Map<String, Object> showInsertDialog(@ModelAttribute("model") FormBackingObject fbo)
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
	@RequestMapping(value = "/db/*/ddl-grant.html", method = RequestMethod.POST)
	public Map<String, Object> performInsertQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		
		final PrivilegeDescription pd = new PrivilegeDescription(fbo.getGrantee(), fbo.getGrantee(), fbo.getPrivilege(), fbo.isGrantable());
		
		final Query q = queryGeneratorService.createGrantQuery(info, getSQLDialect(), pd);
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
	@RequestMapping(value = "/db/*/ajax/ddl-revoke.html", method = RequestMethod.GET)
	public Map<String, Object> showDeleteDialog(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final PrivilegeDescription pd = findPrivilege(info, fbo.getPrivilege(), fbo.getGrantee());
		
		fbo.setGrantable(pd.isGrantable());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ddl-revoke.html", method = RequestMethod.POST)
	public Map<String, Object> performDeleteQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final PrivilegeDescription pd = findPrivilege(info, fbo.getPrivilege(), fbo.getGrantee());
		
		final Query q = queryGeneratorService.createRevokeQuery(info, getSQLDialect(), pd);
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
	
	private PrivilegeDescription findPrivilege(TableDescription t, String privilege, String grantee)
		{
		for (PrivilegeDescription pd : t.getPrivileges())
			{
			if (privilege.equals(pd.getPrivilege()) && grantee.equals(pd.getGrantee()))
				return (pd);
			}
		
		return (null);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
