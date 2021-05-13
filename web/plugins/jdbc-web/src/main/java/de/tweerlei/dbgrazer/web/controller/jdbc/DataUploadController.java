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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.StatementProducerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UploadService;
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
public class DataUploadController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String fileFormat;
		private String format;
		private String mode;
		private String statementFormat;
		private MultipartFile file;
		
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
		 * Get the fileFormat
		 * @return the fileFormat
		 */
		public String getFileFormat()
			{
			return fileFormat;
			}
		
		/**
		 * Set the fileFormat
		 * @param fileFormat the fileFormat to set
		 */
		public void setFileFormat(String fileFormat)
			{
			this.fileFormat = fileFormat;
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
		public String getMode()
			{
			return mode;
			}
		
		/**
		 * Set the mode
		 * @param mode the mode to set
		 */
		public void setMode(String mode)
			{
			this.mode = mode;
			}
		
		/**
		 * Get the statementFormat
		 * @return the statementFormat
		 */
		public String getStatementFormat()
			{
			return statementFormat;
			}
		
		/**
		 * Set the statementFormat
		 * @param statementFormat the statementFormat to set
		 */
		public void setStatementFormat(String statementFormat)
			{
			this.statementFormat = statementFormat;
			}
		
		/**
		 * Get the file
		 * @return the file
		 */
		public MultipartFile getFile()
			{
			return file;
			}
		
		/**
		 * Set the file
		 * @param file the file to set
		 */
		public void setFile(MultipartFile file)
			{
			this.file = file;
			}
		}
	
	private final MetadataService metadataService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final DownloadService downloadService;
	private final StatementProducerService statementProducerService;
	private final UploadService uploadService;
	private final TaskProgressService taskProgressService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param downloadService DownloadService
	 * @param statementProducerService StatementProducerService
	 * @param uploadService UploadService
	 * @param taskProgressService TaskProgressService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DataUploadController(MetadataService metadataService,
			QueryService queryService, QueryPerformerService runner, TaskProgressService taskProgressService,
			StatementProducerService statementProducerService,
			DownloadService downloadService, UploadService uploadService,
			ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.queryService = queryService;
		this.runner = runner;
		this.downloadService = downloadService;
		this.statementProducerService = statementProducerService;
		this.uploadService = uploadService;
		this.taskProgressService = taskProgressService;
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
	 * Show upload form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbobject-upload.html", method = RequestMethod.GET)
	public Map<String, Object> showUploadForm(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		fbo.setMode(connectionSettings.getParameterHistory().get("mode"));
		fbo.setFileFormat(connectionSettings.getParameterHistory().get("fileFormat"));
		fbo.setFormat(connectionSettings.getParameterHistory().get("downloadFormat"));
		fbo.setStatementFormat(connectionSettings.getParameterHistory().get("statementFormat"));
		
		model.put("formats", downloadService.getSupportedDownloadFormats());
		model.put("uploadFormats", uploadService.getSupportedUploadFormats());
		model.put("statementFormats", statementProducerService.getSupportedFormats());
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbobject-upload.html", method = RequestMethod.POST)
	public Map<String, Object> uploadObject(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		if (!StringUtils.empty(fbo.getMode()) && !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DMLProgressMonitor pr = taskProgressService.createDMLProgressMonitor();
		if (pr == null)
			{
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		
		try	{
			final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
			final TableDescription info = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
			final SQLDialect dialect = getSQLDialect();
			
			final RowProducer p = uploadService.createRowProducer(info, fbo.getFile().getInputStream(), fbo.getFileFormat());
			
			if (StringUtils.empty(fbo.getMode()))
				model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), p, fbo.getFile().getOriginalFilename(), fbo.getObject(), qname, info.getPKColumns(), dialect, fbo.getFormat()));
			else
				{
				final Result r = insertRows(p, info, fbo.getStatementFormat(), fbo.getMode(), pr);
				
				model.put("rs", r.getFirstRowSet());
				}
			
			connectionSettings.getParameterHistory().put("mode", fbo.getMode());
			connectionSettings.getParameterHistory().put("fileFormat", fbo.getFileFormat());
			connectionSettings.getParameterHistory().put("downloadFormat", fbo.getFormat());
			connectionSettings.getParameterHistory().put("statementFormat", fbo.getStatementFormat());
			}
		catch (IOException e)
			{
			model.put("exception", e);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			model.put("exception", e);
			}
		finally
			{
			taskProgressService.removeDMLProgressMonitor();
			}
		
		return (model);
		}
	
	private Result insertRows(RowProducer p, TableDescription info, String format, String mode, DMLProgressMonitor pr) throws PerformQueryException
		{
		final StatementProducer sp = statementProducerService.getStatementProducer(p, info, getSQLDialect(), format);
		if (sp == null)
			throw new PerformQueryException("getStatementProducer", new RuntimeException("not supported"));
		
		final Result r = runner.performCustomQueries(connectionSettings.getLinkName(), sp, mode, pr);
		
		return (r);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
