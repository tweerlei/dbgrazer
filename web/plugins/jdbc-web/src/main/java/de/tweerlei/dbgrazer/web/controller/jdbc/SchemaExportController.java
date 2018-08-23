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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.SchemaExportService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Browse the DB metadata
 * 
 * @author Robert Wruck
 */
@Controller
public class SchemaExportController
	{
	private final MetadataService metadataService;
	private final SchemaExportService schemaExportService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param schemaExportService SchemaExportService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public SchemaExportController(MetadataService metadataService,
			SchemaExportService schemaExportService,
			ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.schemaExportService = schemaExportService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show catalogs
	 * @param format Format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbexport.html", method = RequestMethod.GET)
	public Map<String, Object> exportSchema(
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<TableDescription> right = readSchema(connectionSettings.getLinkName(), connectionSettings.getCatalog(), connectionSettings.getSchema(), "");
		
		final StringBuilder sb = new StringBuilder();
		if (!StringUtils.empty(connectionSettings.getSchema()))
			sb.append(connectionSettings.getSchema());
		else if (!StringUtils.empty(connectionSettings.getCatalog()))
			sb.append(connectionSettings.getCatalog());
		
		final Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(RowSetConstants.ATTR_TABLE_CATALOG, connectionSettings.getCatalog());
		attributes.put(RowSetConstants.ATTR_TABLE_SCHEMA, connectionSettings.getSchema());
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, schemaExportService.getExportDownloadSource(connectionSettings.getLinkName(), sb.toString(), right, getSQLDialect(), attributes, format));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param format Format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/designexport.html", method = RequestMethod.GET)
	public Map<String, Object> exportDesigner(
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<QualifiedName> missing = new HashSet<QualifiedName>();
		final Set<TableDescription> right = metadataService.getTableInfos(connectionSettings.getLinkName(), connectionSettings.getDesign().getTableNames(), missing, ColumnMode.ALL, null);
		
		final String title = StringUtils.empty(connectionSettings.getDesign().getName()) ? connectionSettings.getLinkName() : connectionSettings.getDesign().getName();
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, schemaExportService.getExportDownloadSource(connectionSettings.getLinkName(), title, right, getSQLDialect(), null, format));
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/designlinks.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignDownloadMenu()
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", schemaExportService.getSupportedExportFormats());
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/schemalinks.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemaDownloadMenu()
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", schemaExportService.getSupportedExportFormats());
		
		return (model);
		}
	
	private Set<TableDescription> readSchema(String conn, String catalog, String schema, String filter)
		{
		final Map<QualifiedName, String> tables = metadataService.getTables(conn, catalog, schema, TableDescription.TABLE, filter);
		
		return (metadataService.getTableInfos(conn, tables.keySet(), null, ColumnMode.ALL, null));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
