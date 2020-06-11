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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.RowCountService;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableSet;
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
public class DesignRowCountController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String connection2;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
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
		}
	
	private final LinkService linkService;
	private final RowCountService rowCountService;
	private final TaskProgressService taskProgressService;
	private final UserSettingsManager userSettingsManager;
	private final DesignManagerService designManagerService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param rowCountService RowCountService
	 * @param taskProgressService TaskProgressService
	 * @param userSettingsManager UserSettingsManager
	 * @param designManagerService DesignManagerService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DesignRowCountController(LinkService linkService, DesignManagerService designManagerService,
			TaskProgressService taskProgressService, RowCountService rowCountService, UserSettingsManager userSettingsManager,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.linkService = linkService;
		this.rowCountService = rowCountService;
		this.taskProgressService = taskProgressService;
		this.userSettingsManager = userSettingsManager;
		this.designManagerService = designManagerService;
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
	@RequestMapping(value = "/db/*/dbdesigner-count.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignCountDialog(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (fbo.getConnection2() != null)
			{
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			}
		else
			{
			fbo.setConnection2(connectionSettings.getParameterHistory().get("connection2"));
			}
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		model.put("allConnections", all);
		
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-count.html", method = RequestMethod.POST)
	public Map<String, Object> compareDesignCounts(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final TableSet design = designManagerService.getCurrentDesign();
		model.put("currentDesign", design);
		
		final Set<QualifiedName> tables = design.getTableNames();
		
		final SQLDialect dialect = getSQLDialect();
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		try	{
			final Map<QualifiedName, Object> srcCounts = rowCountService.countRows(connectionSettings.getLinkName(), tables, dialect, c.getSourceRows());
			final Map<QualifiedName, Object> dstCounts = rowCountService.countRows(fbo.getConnection2(), tables, dialect, c.getDestinationRows());
			
			model.put("rowCounts", rowCountService.mergeRowCounts(srcCounts, dstCounts, dialect, true));
			
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
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
