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
package de.tweerlei.dbgrazer.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.file.StringPersister;
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.text.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class CustomQueryController
	{
	private static final String UPLOAD_CHARSET = "UTF-8";
	private static final String QUERY_EXTENSION = "queries";
	
	private final ResultDownloadService resultDownloadService;
	private final DownloadService downloadService;
	private final QuerySettingsManager querySettingsManager;
	private final UserManagerService userManagerService;
	private final TextTransformerService textFormatterService;
	private final StringPersister stringPersister;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 * @param resultDownloadService ResultDownloadService
	 * @param downloadService DownloadService
	 * @param querySettingsManager QuerySettingsManager
	 * @param userManagerService UserManagerService
	 * @param textFormatterService TextFormatterService
	 * @param stringPersister StringPersister
	 */
	@Autowired
	public CustomQueryController(QuerySettingsManager querySettingsManager, TextTransformerService textFormatterService,
			ResultDownloadService resultDownloadService, DownloadService downloadService,
			UserManagerService userManagerService, StringPersister stringPersister,
			UserSettings userSettings, ConnectionSettings connectionSettings
			)
		{
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.resultDownloadService = resultDownloadService;
		this.downloadService = downloadService;
		this.querySettingsManager = querySettingsManager;
		this.userManagerService = userManagerService;
		this.textFormatterService = textFormatterService;
		this.stringPersister = stringPersister;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Show a parameter input form
	 * @param statement Effective query string
	 * @param format Format name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/formattext.html", method = RequestMethod.POST)
	public Map<String, Object> formatQuery(
			@RequestParam("statement") String statement,
			@RequestParam("format") String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.FORMATTING);
		final String result = textFormatterService.format(statement, format, options);
		
		model.put("statement", result);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param statement Effective query string
	 * @param format Format name
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/formatlines.html", method = RequestMethod.POST)
	public Map<String, Object> formatLines(
			@RequestParam("statement") String statement,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final String formatName;
		final boolean formattingActive;
		if (format == null)
			{
			formatName = querySettingsManager.getFormatName(null);
			formattingActive = querySettingsManager.isFormattingActive(null);
			}
		else
			{
			formatName = format;
			formattingActive = (formatting == null) ? false : formatting;
			querySettingsManager.setFormatName(null, formatName);
			querySettingsManager.setFormattingActive(null, formattingActive);
			}
		
		final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
		if (formattingActive)
			options.add(TextTransformerService.Option.FORMATTING);
		
		final String result = textFormatterService.format(statement, formatName, options);
		
		model.put("statement", statement);
		model.put("format", formatName);
		model.put("formatting", formattingActive);
		model.put("formats", textFormatterService.getSupportedTextFormats());
		model.put("result", result);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param statement Effective query string
	 * @param format Format name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/formatstmt.html", method = RequestMethod.POST)
	public Map<String, Object> formatStatement(
			@RequestParam("statement") String statement,
			@RequestParam(value = "format", required = false) String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final String formatName;
		if (format == null)
			formatName = querySettingsManager.getFormatName(null);
		else
			{
			formatName = format;
			querySettingsManager.setFormatName(null, formatName);
			}
		
		final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.FORMATTING);
		final String result = textFormatterService.format(statement, formatName, options);
		
		model.put("statement", statement);
		model.put("format", formatName);
		model.put("formats", textFormatterService.getSupportedTextFormats());
		model.put("result", result);
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dllinks.html", method = RequestMethod.GET)
	public Map<String, Object> showDownloadMenu()
		{
		if (!connectionSettings.isBrowserEnabled() && !connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", resultDownloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Show WS download links
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param where WHERE clause
	 * @param order ORDER BY clause
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/wslinks.html", method = RequestMethod.GET)
	public Map<String, Object> showWSLinks(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("where") String where,
			@RequestParam("order") String order
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("catalog", catalog);
		model.put("schema", schema);
		model.put("object", object);
		model.put("where", where);
		model.put("order", order);
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/tbllinks.html", method = RequestMethod.GET)
	public Map<String, Object> showTableDownloadMenu()
		{
		if (!connectionSettings.isBrowserEnabled() && !connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-history.html", method = RequestMethod.GET)
	public Map<String, Object> showHistoryMenu(
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> history = new LinkedHashMap<String, String>();
		int i = 0;
		for (String ent : connectionSettings.getCustomQueryHistory())
			{
			history.put(ent, String.valueOf(i));
			i++;
			}
		
		model.put("history", history);
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param index History index
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-history.html", method = RequestMethod.POST)
	public String loadHistoryEntry(
			@RequestParam("q") Integer index
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final String query = querySettingsManager.getCustomHistoryEntry(index);
		if ((query != null) && !query.equals(connectionSettings.getCustomQuery().getQuery()))
			{
			connectionSettings.getCustomQuery().setQuery(query);
			connectionSettings.getCustomQuery().modify();
			}
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-load.html", method = RequestMethod.GET)
	public Map<String, Object> showLoadDialog(
			@RequestParam("q") String name
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("designs", userManagerService.listExtensionObjects(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), QUERY_EXTENSION));
		model.put("design", name);
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-save.html", method = RequestMethod.GET)
	public Map<String, Object> showSaveDialog(
			@RequestParam("q") String name
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("designs", userManagerService.listExtensionObjects(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), QUERY_EXTENSION));
		model.put("design", name);
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-load.html", method = RequestMethod.POST)
	public String loadQuery(
			@RequestParam(value = "q", required = false) String name
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name))
			{
			try	{
				final String query = userManagerService.loadExtensionObject(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), QUERY_EXTENSION, name, stringPersister);
				connectionSettings.getCustomQuery().setQuery(query);
				connectionSettings.getCustomQuery().persist(name);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "loadQuery", e);
				}
			}
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-delete.html", method = RequestMethod.POST)
	public String removeQuery(
			@RequestParam(value = "q", required = false) String name
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name))
			{
			try	{
				userManagerService.removeExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), QUERY_EXTENSION, name);
				if (name.equals(connectionSettings.getCustomQuery().getName()))
					connectionSettings.getCustomQuery().modify();
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "removeQuery", e);
				}
			}
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @param statement Effective query string
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-save.html", method = RequestMethod.POST)
	public String saveQuery(
			@RequestParam(value = "q", required = false) String name,
			@RequestParam(value = "statement", required = false) String statement
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name) && !StringUtils.empty(statement))
			{
			try	{
				final String dn = userManagerService.saveExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), QUERY_EXTENSION, name, statement, stringPersister);
				connectionSettings.getCustomQuery().setQuery(statement);
				connectionSettings.getCustomQuery().persist(dn);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "saveQuery", e);
				}
			}
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Display the users
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query-upload.html", method = RequestMethod.GET)
	public Map<String, Object> showUploadDialog()
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param file Uploaded file
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/query-upload.html", method = RequestMethod.POST)
	public String uploadQuery(
			@RequestParam("file") MultipartFile file
			)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		try	{
			final InputStream is = file.getInputStream();
			try	{
				final InputStreamReader isr = new InputStreamReader(is, UPLOAD_CHARSET);
				final StringWriter sw = new StringWriter();
				
				StreamUtils.copy(isr, sw);
				
				connectionSettings.getCustomQuery().setQuery(sw.toString());
				connectionSettings.getCustomQuery().reset();
				}
			finally
				{
				is.close();
				}
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "loadQuery", e);
			}
		
		final PathInfo pi = connectionSettings.getSourceURL();
		if (pi != null)
			return ("redirect:" + pi.getPage());
		
		return ("redirect:index.html");
		}
	}
