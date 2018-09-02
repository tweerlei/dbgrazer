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
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.SchemaDataExportService;
import de.tweerlei.dbgrazer.web.service.jdbc.SchemaDataExportService.TraversalMode;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableSet;
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
public class DesignExportController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String table;
		private String where;
		private String format;
		private TraversalMode mode;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.mode = TraversalMode.CHILDREN;
			}
		
		/**
		 * Get the table
		 * @return the table
		 */
		public String getTable()
			{
			return table;
			}
		
		/**
		 * Set the table
		 * @param table the table to set
		 */
		public void setTable(String table)
			{
			this.table = table;
			}
		
		/**
		 * Get the where
		 * @return the where
		 */
		public String getWhere()
			{
			return where;
			}
		
		/**
		 * Set the where
		 * @param where the where to set
		 */
		public void setWhere(String where)
			{
			this.where = where;
			}
		
		/**
		 * Get the format
		 * @return the format
		 */
		public String getFormat()
			{
			return format;
			}
		
		/**
		 * Set the format
		 * @param format the format to set
		 */
		public void setFormat(String format)
			{
			this.format = format;
			}
		
		/**
		 * Get the mode
		 * @return the mode
		 */
		public TraversalMode getMode()
			{
			return mode;
			}
		
		/**
		 * Set the mode
		 * @param mode the mode to set
		 */
		public void setMode(TraversalMode mode)
			{
			this.mode = mode;
			}
		}
	
	private static final class ExportRowSetProducer implements RowSetProducer
		{
		private final SchemaDataExportService schemaExportService;
		private final String connection;
		private final SQLDialect dialect;
		private final Set<TableDescription> infos;
		private final QualifiedName startTable;
		private final String where;
		private final TraversalMode mode;
		
		public ExportRowSetProducer(SchemaDataExportService schemaExportService,
				String connection, SQLDialect dialect, Set<TableDescription> infos, QualifiedName startTable, String where, TraversalMode mode)
			{
			this.schemaExportService = schemaExportService;
			this.connection = connection;
			this.dialect = dialect;
			this.infos = infos;
			this.startTable = startTable;
			this.where = where;
			this.mode = mode;
			}
		
		@Override
		public int produceRowSets(RowSetHandler h)
			{
			try	{
				return (schemaExportService.export(connection, dialect, infos, startTable, where, mode, h));
				}
			catch (PerformQueryException e)
				{
				throw e.getCause();
				}
			}
		}
	
	private final MetadataService metadataService;
	private final SchemaDataExportService dataExportService;
	private final DesignManagerService designManagerService;
	private final DownloadService downloadService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param dataExportService SchemaDataExportService
	 * @param downloadService DownloadService
	 * @param designManagerService DesignManagerService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DesignExportController(MetadataService metadataService,
			SchemaDataExportService dataExportService,
			DownloadService downloadService, DesignManagerService designManagerService,
			ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.dataExportService = dataExportService;
		this.downloadService = downloadService;
		this.designManagerService = designManagerService;
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
	@RequestMapping(value = "/db/*/ajax/design-data.html", method = RequestMethod.GET)
	public Map<String, Object> showExportDialog(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		fbo.setTable(connectionSettings.getParameterHistory().get("table"));
		fbo.setWhere(connectionSettings.getParameterHistory().get("where"));
		fbo.setFormat(connectionSettings.getParameterHistory().get("downloadFormat"));
		if (connectionSettings.getParameterHistory().get("traversalMode") != null)
			fbo.setMode(TraversalMode.valueOf(connectionSettings.getParameterHistory().get("traversalMode")));
		
		final SQLDialect d = getSQLDialect();
		
		final TableSet design = designManagerService.getCurrentDesign();
		final Map<String, String> tables = new TreeMap<String, String>();
		for (QualifiedName qn : design.getTableNames())
			tables.put(d.getQualifiedTableName(qn), qn.getCatalogName() + "/" + qn.getSchemaName() + "/" + qn.getObjectName());
		
		model.put("tables", tables);
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		model.put("modes", TraversalMode.values());
		
		return (model);
		}
	
	/**
	 * Export data from all tables of the current design
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/designdata.html", method = RequestMethod.POST)
	public Map<String, Object> exportDesignData(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SQLDialect d = getSQLDialect();
		
		final TableSet design = designManagerService.getCurrentDesign();
		final String[] parts = fbo.getTable().split("/");
		final QualifiedName startTable = new QualifiedName(parts[0], parts[1], parts[2]);
		final Set<TableDescription> infos = metadataService.getTableInfos(connectionSettings.getLinkName(), design.getTableNames(), null, ColumnMode.ALL, null);
		
		final String title = StringUtils.empty(design.getName()) ? connectionSettings.getLinkName() : design.getName();
		
		final ExportRowSetProducer p = new ExportRowSetProducer(dataExportService, connectionSettings.getLinkName(), d, infos, startTable, fbo.getWhere(), fbo.getMode());
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getMultiStreamDownloadSource(connectionSettings.getLinkName(), p, design.getName(), title, d, fbo.getFormat()));
		
		connectionSettings.getParameterHistory().put("table", fbo.getTable());
		connectionSettings.getParameterHistory().put("where", fbo.getWhere());
		connectionSettings.getParameterHistory().put("downloadFormat", fbo.getFormat());
		connectionSettings.getParameterHistory().put("traversalMode", (fbo.getMode() == null) ? null : fbo.getMode().name());
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
