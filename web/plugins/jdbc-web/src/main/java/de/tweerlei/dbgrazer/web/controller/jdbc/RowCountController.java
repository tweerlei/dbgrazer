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
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.RowCountService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;

/**
 * Compare row counts
 * 
 * @author Robert Wruck
 */
@Controller
public class RowCountController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String connection2;
		private String catalog2;
		private String schema2;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			}
		
		/**
		 * Get the catalog
		 * @return the catalog
		 */
		public String getCatalog()
			{
			return catalog;
			}
		
		/**
		 * Set the catalog
		 * @param catalog the catalog to set
		 */
		public void setCatalog(String catalog)
			{
			this.catalog = catalog;
			}
		
		/**
		 * Get the schema
		 * @return the schema
		 */
		public String getSchema()
			{
			return schema;
			}
		
		/**
		 * Set the schema
		 * @param schema the schema to set
		 */
		public void setSchema(String schema)
			{
			this.schema = schema;
			}
		
		/**
		 * Get the object
		 * @return the object
		 */
		public String getObject()
			{
			return object;
			}
		
		/**
		 * Set the object
		 * @param object the object to set
		 */
		public void setObject(String object)
			{
			this.object = object;
			}
		
		/**
		 * Get the connection2
		 * @return the connection2
		 */
		public String getConnection2()
			{
			return connection2;
			}
		
		/**
		 * Set the connection2
		 * @param connection2 the connection2 to set
		 */
		public void setConnection2(String connection2)
			{
			this.connection2 = connection2;
			}
		
		/**
		 * Get the catalog2
		 * @return the catalog2
		 */
		public String getCatalog2()
			{
			return catalog2;
			}
		
		/**
		 * Set the catalog2
		 * @param catalog2 the catalog2 to set
		 */
		public void setCatalog2(String catalog2)
			{
			this.catalog2 = catalog2;
			}
		
		/**
		 * Get the schema2
		 * @return the schema2
		 */
		public String getSchema2()
			{
			return schema2;
			}
		
		/**
		 * Set the schema2
		 * @param schema2 the schema2 to set
		 */
		public void setSchema2(String schema2)
			{
			this.schema2 = schema2;
			}
		}
	private final MetadataService metadataService;
	private final LinkService linkService;
	private final RowCountService rowCountService;
	private final TaskProgressService taskProgressService;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param linkService LinkService
	 * @param rowCountService RowCountService
	 * @param taskProgressService TaskProgressService
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public RowCountController(MetadataService metadataService, LinkService linkService,
			TaskProgressService taskProgressService, RowCountService rowCountService, UserSettingsManager userSettingsManager,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.linkService = linkService;
		this.rowCountService = rowCountService;
		this.taskProgressService = taskProgressService;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject()
		{
		final FormBackingObject ret = new FormBackingObject();
		
		return (ret);
		}
	
	/**
	 * Show the schema selection dialog
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbcount2.html", method = RequestMethod.GET)
	public Map<String, Object> showObjectCountDialog(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if ((fbo.getConnection2() != null) && (fbo.getCatalog2() != null) && (fbo.getSchema2() != null))
			{
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
			connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
			}
		else
			{
			fbo.setConnection2(connectionSettings.getParameterHistory().get("connection2"));
			fbo.setCatalog2(connectionSettings.getParameterHistory().get("catalog2"));
			fbo.setSchema2(connectionSettings.getParameterHistory().get("schema2"));
			}
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		model.put("allConnections", all);
		
		if (fbo.getConnection2() != null)
			{
			model.put("catalogs", metadataService.getCatalogs(fbo.getConnection2()));
			if (fbo.getCatalog2() != null)
				model.put("schemas", metadataService.getSchemas(fbo.getConnection2()));
			}
		
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbcount2.html", method = RequestMethod.POST)
	public Map<String, Object> compareObjectCounts(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<QualifiedName> tablesLeft = Collections.singleton(new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject()));
		final Set<QualifiedName> tablesRight = Collections.singleton(new QualifiedName(fbo.getCatalog2(), fbo.getSchema2(), fbo.getObject()));
		
		final SQLDialect dialect = getSQLDialect();
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		try	{
			final Map<QualifiedName, Object> srcCounts = rowCountService.countRows(connectionSettings.getLinkName(), tablesLeft, dialect, c.getSourceRows());
			final Map<QualifiedName, Object> dstCounts = rowCountService.countRows(fbo.getConnection2(), tablesRight, dialect, c.getDestinationRows());
			
			model.put("rowCounts", rowCountService.mergeRowCounts(srcCounts, dstCounts, dialect, false));
			
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
			connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
			}
		finally
			{
			taskProgressService.removeCompareProgressMonitor();
			}
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
