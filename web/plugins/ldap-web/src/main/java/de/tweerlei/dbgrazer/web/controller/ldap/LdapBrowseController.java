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
package de.tweerlei.dbgrazer.web.controller.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.constant.ldap.LdapConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class LdapBrowseController
	{
	private final QueryPerformerService runner;
	private final DataFormatterFactory factory;
	private final ResultTransformerService resultTransformer;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param runner QueryPerformerService
	 * @param factory DataFormatterFactory
	 * @param resultTransformer ResultTransformerService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public LdapBrowseController(QueryPerformerService runner, DataFormatterFactory factory, ResultTransformerService resultTransformer,
			ConnectionSettings connectionSettings)
		{
		this.runner = runner;
		this.factory = factory;
		this.resultTransformer = resultTransformer;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ldap.html", method = RequestMethod.GET)
	public Map<String, Object> showBrowser()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		try	{
			final Result dirResult = runner.performCustomQuery(connectionSettings.getLinkName(), LdapConstants.QUERYTYPE_LIST, "SELECT rdn, rdn.name", null, null, "dirs", false, null);
			final RowSet dirs = dirResult.getFirstRowSet();
			resultTransformer.translateRowSet(dirs, fmt);
			
			final Map<String, TabItem<RowSet>> tabs = new HashMap<String, TabItem<RowSet>>(1);
			tabs.put(LdapMessageKeys.ENTRIES_TAB, new TabItem<RowSet>(dirs, dirs.getRows().size()));
			
			final Result fileResult = runner.performCustomQuery(connectionSettings.getLinkName(), LdapConstants.QUERYTYPE_LOOKUP, "", null, null, "files", false, null);
			final RowSet files = fileResult.getFirstRowSet();
			resultTransformer.translateRowSet(files, fmt);
			
			final Map<String, TabItem<RowSet>> tabs2 = new HashMap<String, TabItem<RowSet>>(1);
			tabs2.put(LdapMessageKeys.ENTRY_TAB, new TabItem<RowSet>(files, files.getRows().size()));
			
			model.put("tabs", tabs);
			model.put("tabs2", tabs2);
			model.put("extensionJS", LdapMessageKeys.EXTENSION_JS);
			
			final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
			tableColumns.add(files.getColumns());
			model.put("tableColumns", tableColumns);
			}
		catch (PerformQueryException e)
			{
			throw e.getCause();
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/entry.html", method = RequestMethod.GET)
	public Map<String, Object> showDirectory(@RequestParam("path") String path)
		{
		return (showDirectoryInternal(path));
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/entry.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxDirectory(@RequestParam("path") String path)
		{
		return (showDirectoryInternal(path));
		}
	
	private Map<String, Object> showDirectoryInternal(String path)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("path", path);
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		try	{
			final Result fileResult = runner.performCustomQuery(connectionSettings.getLinkName(), LdapConstants.QUERYTYPE_LOOKUP, "FROM " + path, null, null, "files", false, null);
			final RowSet files = fileResult.getFirstRowSet();
			resultTransformer.translateRowSet(files, fmt);
			
			final Map<String, TabItem<RowSet>> tabs = new HashMap<String, TabItem<RowSet>>(1);
			tabs.put(LdapMessageKeys.ENTRY_TAB, new TabItem<RowSet>(files, files.getRows().size()));
			
			model.put("tabs", tabs);
			model.put("extensionJS", LdapMessageKeys.EXTENSION_JS);
			
			final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
			tableColumns.add(files.getColumns());
			model.put("tableColumns", tableColumns);
			}
		catch (PerformQueryException e)
			{
			throw e.getCause();
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param path Path
	 * @param label Label
	 * @param left Left
	 * @param target Target element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/ldaptree.html", method = RequestMethod.GET)
	public Map<String, Object> showSubdirs(
			@RequestParam("path") String path,
			@RequestParam("label") String label,
			@RequestParam("left") String left,
			@RequestParam("target") String target
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("path", path);
		model.put("label", label);
		model.put("left", StringUtils.empty(left) ? "" : (left + "-"));
		model.put("targetElement", target);
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		try	{
			final Result dirResult = runner.performCustomQuery(connectionSettings.getLinkName(), LdapConstants.QUERYTYPE_LIST, "SELECT rdn, rdn.name\nFROM " + path, null, null, "dirs", false, null);
			final RowSet dirs = dirResult.getFirstRowSet();
			resultTransformer.translateRowSet(dirs, fmt);
			
			model.put("rs", dirs);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			model.put("exception", e);
			}
		
		return (model);
		}
	}
