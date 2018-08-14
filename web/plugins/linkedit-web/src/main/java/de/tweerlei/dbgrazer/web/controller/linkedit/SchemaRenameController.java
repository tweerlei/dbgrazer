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
package de.tweerlei.dbgrazer.web.controller.linkedit;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class SchemaRenameController
	{
	private final QueryService queryService;
	private final FrontendNotificationService notificationService;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param notificationService FrontendNotificationService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public SchemaRenameController(QueryService queryService,
			FrontendNotificationService notificationService,
			UserSettings userSettings)
		{
		this.queryService = queryService;
		this.notificationService = notificationService;
		this.userSettings = userSettings;
		}
	
	/**
	 * Show the rename form
	 * @param q Schema name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/schema.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemaForm(
			@RequestParam("q") String q
			)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SchemaDef schema = SchemaDef.valueOf(q);
		model.put("schemaName", schema.getName());
		model.put("schemaVersion", schema.getVersion());
		
		return (model);
		}
	
	/**
	 * Show the rename form
	 * @param fromName Schema name
	 * @param fromVersion Schema version
	 * @param toName Schema name
	 * @param toVersion Schema version
	 * @return View
	 */
	@RequestMapping(value = "/rename-schema.html", method = RequestMethod.POST)
	public String renameSchema(
			@RequestParam("fromName") String fromName,
			@RequestParam("fromVersion") String fromVersion,
			@RequestParam("toName") String toName,
			@RequestParam("toVersion") String toVersion
			)
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final SchemaDef oldName = new SchemaDef(fromName, fromVersion);
		final SchemaDef newName = new SchemaDef(toName, toVersion);
		
		try	{
			queryService.renameSchema(userSettings.getPrincipal().getLogin(), oldName, newName);
			}
		catch (BindException e)
			{
			// FIXME: Add error message
			notificationService.logError("validation_subqueryWithTooManyParameters", e.getMessage());
			}
		
		return ("redirect:links.html");
		}
	}
