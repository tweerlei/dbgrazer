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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.jdbc.BrowserSettingsManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.SQLReconstructionService;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableFilterEntry;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class DataExportController
	{
	private static final String VIEW_COUNT = "count";
	private static final String VIEW_STATS = "stats";
	private static final String VIEW_DELETE = "delete";
	private static final String VIEW_TRUNCATE = "truncate";
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String where;
		private String order;
		private String view;
		private String format;
		private boolean allRows;
		
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
		 * @return the where
		 */
		public String getWhere()
			{
			return where;
			}
		
		/**
		 * @param where the where to set
		 */
		public void setWhere(String where)
			{
			this.where = where;
			}
		
		/**
		 * @return the order
		 */
		public String getOrder()
			{
			return order;
			}
		
		/**
		 * @param order the order to set
		 */
		public void setOrder(String order)
			{
			this.order = order;
			}
		
		/**
		 * @return the view
		 */
		public String getView()
			{
			return view;
			}
		
		/**
		 * @param view the view to set
		 */
		public void setView(String view)
			{
			this.view = view;
			}
		
		/**
		 * @return the format
		 */
		public String getFormat()
			{
			return format;
			}
		
		/**
		 * @param format the format to set
		 */
		public void setFormat(String format)
			{
			this.format = format;
			}
		
		/**
		 * @return the allRows
		 */
		public boolean isAllRows()
			{
			return allRows;
			}
		
		/**
		 * @param allRows the allRows to set
		 */
		public void setAllRows(boolean allRows)
			{
			this.allRows = allRows;
			}
		}
	
	private final MetadataService metadataService;
	private final QueryPerformerService runner;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final DownloadService downloadService;
	private final ResultTransformerService resultTransformer;
	private final BrowserSettingsManagerService browserSettingsManager;
	private final SQLReconstructionService reconstructionService;
	private final SQLGeneratorService sqlGenerator;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param runner QueryPerformerService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param downloadService DownloadService
	 * @param resultTransformer ResultTransformerService
	 * @param sqlGenerator SQLGeneratorService
	 * @param reconstructionService SQLReconstructionService
	 * @param browserSettingsManager BrowserSettingsManagerService
	 */
	@Autowired
	public DataExportController(MetadataService metadataService, QueryPerformerService runner,
			ConnectionSettings connectionSettings,
			DataFormatterFactory factory, DownloadService downloadService,
			ResultTransformerService resultTransformer, SQLGeneratorService sqlGenerator,
			SQLReconstructionService reconstructionService, BrowserSettingsManagerService browserSettingsManager
			)
		{
		this.metadataService = metadataService;
		this.runner = runner;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.downloadService = downloadService;
		this.resultTransformer = resultTransformer;
		this.sqlGenerator = sqlGenerator;
		this.reconstructionService = reconstructionService;
		this.browserSettingsManager = browserSettingsManager;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Get the FormBackingObject
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject()
		{
		final FormBackingObject fbo = new FormBackingObject();
		
		return (fbo);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param target Target Element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-simple.html", method = RequestMethod.POST)
	public Map<String, Object> performSimpleQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam(value = "target", required = false) String target)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		try	{
			final SQLDialect dialect = getSQLDialect();
			final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
			final TableDescription desc = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
			final String statement;
			final String type;
			final boolean editable;
			if (VIEW_COUNT.equals(fbo.getView()))
				{
				statement = sqlGenerator.generateSelectCount(desc.getName(), Style.INDENTED, fbo.getWhere(), dialect);
				type = JdbcConstants.QUERYTYPE_MULTIPLE;
				editable = false;
				}
			else if (VIEW_STATS.equals(fbo.getView()))
				{
				statement = sqlGenerator.generateSelectStats(desc, Style.INDENTED, fbo.getWhere(), dialect);
				type = JdbcConstants.QUERYTYPE_MULTIPLE;
				editable = false;
				}
			else if (VIEW_DELETE.equals(fbo.getView()))
				{
				if (!connectionSettings.isWritable())
					throw new AccessDeniedException();
				
				statement = sqlGenerator.generateDelete(desc.getName(), Style.INDENTED, fbo.getWhere(), dialect);
				type = JdbcConstants.QUERYTYPE_DML;
				editable = false;
				}
			else if (VIEW_TRUNCATE.equals(fbo.getView()))
				{
				if (!connectionSettings.isWritable())
					throw new AccessDeniedException();
				
				statement = sqlGenerator.generateTruncate(desc.getName(), Style.INDENTED, dialect);
				type = JdbcConstants.QUERYTYPE_DML;
				editable = false;
				}
			else
				{
				if (connectionSettings.isWritable() && !desc.getPKColumns().isEmpty())
					{
					model.put("pkColumns", new ArrayList<Integer>(desc.getPKColumns()));
					editable = true;
					}
				else
					editable = false;
				
				statement = sqlGenerator.generateSelect(desc, Style.INDENTED, fbo.getWhere(), fbo.getOrder(), dialect);
				type = JdbcConstants.QUERYTYPE_MULTIPLE;
				}
			
			final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), type, statement, null, null, fbo.getObject(), false, null);
			
			final RowSet rs = r.getFirstRowSet();
			model.put("foreignKeys", getSingleForeignKeys(desc));
			
			// translateRowSet translates column names, so generate SQL first
			final String sql = reconstructionService.buildSQL(rs, dialect);
			model.put("sql", sql);
			resultTransformer.translateRowSet(rs, fmt);
			model.put("rs", rs);
			
			final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
			if (editable)
				{
				final List<ColumnDef> tmp = new ArrayList<ColumnDef>(rs.getColumns().size() + 1);
				tmp.add(new ColumnDefImpl("", ColumnType.STRING, null, null, null, null));
				tmp.addAll(rs.getColumns());
				tableColumns.add(tmp);
				}
			else
				tableColumns.add(rs.getColumns());
			model.put("tableColumns", tableColumns);
			
			model.put("targetElement", target);
			
			browserSettingsManager.getTableFilters().put(qname.toString(), new TableFilterEntry(fbo.getWhere(), fbo.getOrder()));
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	private List<ForeignKeyDescription> getSingleForeignKeys(TableDescription desc)
		{
		final int nc = desc.getColumns().size();
		final List<ForeignKeyDescription> ret = new ArrayList<ForeignKeyDescription>(nc);
		for (int i = 0; i < nc; i++)
			ret.add(null);
		
		for (ForeignKeyDescription fk : desc.getReferencedKeys())
			{
			if (fk.getColumns().size() == 1)
				{
				final String column = fk.getColumns().keySet().iterator().next();
				int i = 0;
				for (ColumnDescription c : desc.getColumns())
					{
					if (c.getName().equals(column))
						ret.set(i, fk);
					i++;
					}
				}
			}
		
		return (ret);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-simple-export.html", method = RequestMethod.POST)
	public Map<String, Object> performSimpleCSVQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SQLDialect dialect = getSQLDialect();
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription desc = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final String statement = sqlGenerator.generateSelect(desc, Style.INDENTED, fbo.getWhere(), fbo.getOrder(), dialect);
		
		browserSettingsManager.getTableFilters().put(qname.toString(), new TableFilterEntry(fbo.getWhere(), fbo.getOrder()));
		
		try	{
			if (fbo.isAllRows())
				{
				final Query query = runner.createCustomQuery(JdbcConstants.QUERYTYPE_MULTIPLE, statement, null, fbo.getObject());
				
				model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), new QueryParameters(query), qname, desc.getPKColumns(), dialect, fbo.getFormat()));
				}
			else
				{
				final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_MULTIPLE, statement, null, null, fbo.getObject(), false, null);
				final RowSet rs = r.getFirstRowSet();
				
				model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rs, qname, desc.getPKColumns(), dialect, fbo.getFormat()));
				}
			}
		catch (PerformQueryException e)
			{
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-fkvalue.html", method = RequestMethod.GET)
	public Map<String, Object> chooseID(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("v") String selected,
			@RequestParam("id") String target
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final SQLDialect dialect = getSQLDialect();
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription desc = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final String statement = sqlGenerator.generateSelect(desc, Style.INDENTED, null, desc.getPrimaryKey().getColumns().iterator().next(), dialect);
		
		try	{
			final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_MULTIPLE, statement, null, null, fbo.getObject(), false, null);
			
			final Map<String, String> values = resultTransformer.convertToMap(r.getFirstRowSet(), fmt);
			
			model.put("values", values);
			model.put("title", fbo.getObject());
			model.put("value", selected);
			model.put("target", target);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/export.html", method = RequestMethod.GET)
	public Map<String, Object> performExportQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		metadataService.flushCache(connectionSettings.getLinkName());
		
		final SQLDialect dialect = getSQLDialect();
		final QualifiedName qname = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableDescription desc = metadataService.getTableInfo(connectionSettings.getLinkName(), qname, ColumnMode.ALL);
		final String statement = sqlGenerator.generateSelect(desc, Style.INDENTED, fbo.getWhere(), fbo.getOrder(), dialect);
		
		final Query query = runner.createCustomQuery(JdbcConstants.QUERYTYPE_MULTIPLE, statement, null, fbo.getObject());
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), new QueryParameters(query), qname, null, dialect, fbo.getFormat()));
		
		return (model);
		}
	
	/**
	 * Show the schema selection dialog
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/form-export.html", method = RequestMethod.GET)
	public Map<String, Object> showExportWSForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
