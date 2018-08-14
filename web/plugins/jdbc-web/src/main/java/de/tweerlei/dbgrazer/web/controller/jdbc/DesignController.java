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
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService.GraphMode;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignPersister;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class DesignController
	{
	private static final String DESIGN_EXTENSION = "designs";
	
	private static class ToplevelLinkBuilder implements SchemaTransformerService.LinkBuilder
		{
		private final FrontendHelperService frontendHelper;
		private final String conn;
		
		public ToplevelLinkBuilder(FrontendHelperService frontendHelper, String conn)
			{
			this.frontendHelper = frontendHelper;
			this.conn = conn;
			}
		
		@Override
		public String buildLink(QualifiedName qname, boolean fk)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append("catalog=");
			if (qname.getCatalogName() != null)
				sb.append(qname.getCatalogName());
			sb.append("&amp;schema=");
			if (qname.getSchemaName() != null)
				sb.append(qname.getSchemaName());
			sb.append("&amp;object=").append(qname.getObjectName());
			return (frontendHelper.buildPath(MessageKeys.PATH_DB, conn, "dbobject.html", sb.toString()));
			}
		}
	
	private static class DesignerLinkBuilder implements SchemaTransformerService.LinkBuilder
		{
		private final FrontendHelperService frontendHelper;
		private final String conn;
		
		public DesignerLinkBuilder(FrontendHelperService frontendHelper, String conn)
			{
			this.frontendHelper = frontendHelper;
			this.conn = conn;
			}
		
		@Override
		public String buildLink(QualifiedName qname, boolean fk)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append("catalog=");
			if (qname.getCatalogName() != null)
				sb.append(qname.getCatalogName());
			sb.append("&amp;schema=");
			if (qname.getSchemaName() != null)
				sb.append(qname.getSchemaName());
			sb.append("&amp;object=").append(qname.getObjectName());
			return (frontendHelper.buildPath(MessageKeys.PATH_DB, conn, fk ? "dbdesigner-add.html" : "dbdesigner-remove.html", sb.toString()));
			}
		}
	
	private static class TableDescriptionVisualizer implements Runnable
		{
		private final SchemaTransformerService schemaTransformer;
		private final FrontendHelperService frontendHelper;
		private final Set<TableDescription> infos;
		private final String connection;
		private final boolean norefs;
		private final boolean toplevel;
		private final SQLDialect dialect;
		
		private Visualization def;
		
		public TableDescriptionVisualizer(SchemaTransformerService schemaTransformer, FrontendHelperService frontendHelper,
				Set<TableDescription> infos, String connection, boolean norefs, boolean toplevel, SQLDialect dialect)
			{
			this.schemaTransformer = schemaTransformer;
			this.frontendHelper = frontendHelper;
			this.infos = infos;
			this.norefs = norefs;
			this.connection = connection;
			this.toplevel = toplevel;
			this.dialect = dialect;
			this.def = null;
			}
		
		@Override
		public void run()
			{
			def = schemaTransformer.buildGraph(infos, null, ViewConstants.IMAGEMAP_ID,
					norefs ? GraphMode.NO_REFS : GraphMode.ALL_REFS,
					toplevel ? new ToplevelLinkBuilder(frontendHelper, connection) : new DesignerLinkBuilder(frontendHelper, connection),
					dialect);
			}
		
		public Visualization getVisualization()
			{
			if (def == null)
				run();
			return (def);
			}
		}
	
	private final MetadataService metadataService;
	private final DataFormatterFactory dataFormatterFactory;
	private final VisualizationService visualizationService;
	private final SchemaTransformerService schemaTransformer;
	private final UserManagerService userManagerService;
	private final ConnectionSettings connectionSettings;
	private final FrontendHelperService frontendHelper;
	private final FrontendNotificationService frontendNotificationService;
	private final DesignPersister designPersister;
	private final UserSettings userSettings;
	private final ResultCache resultCache;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param userManagerService UserManagerService
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param visualizationService VisualizationService
	 * @param frontendHelper FrontendHelperService
	 * @param frontendNotificationService FrontendNotificationService
	 * @param schemaTransformer SchemaTransformerService
	 * @param designPersister DesignPersister
	 * @param connectionSettings ConnectionSettings
	 * @param userSettings UserSettings
	 * @param resultCache ResultCache
	 */
	@Autowired
	public DesignController(MetadataService metadataService, UserManagerService userManagerService,
			DataFormatterFactory dataFormatterFactory, VisualizationService visualizationService,
			SchemaTransformerService schemaTransformer, FrontendHelperService frontendHelper,
			FrontendNotificationService frontendNotificationService, DesignPersister designPersister,
			ConnectionSettings connectionSettings, UserSettings userSettings, ResultCache resultCache)
		{
		this.metadataService = metadataService;
		this.userManagerService = userManagerService;
		this.dataFormatterFactory = dataFormatterFactory;
		this.visualizationService = visualizationService;
		this.schemaTransformer = schemaTransformer;
		this.frontendHelper = frontendHelper;
		this.frontendNotificationService = frontendNotificationService;
		this.designPersister = designPersister;
		this.connectionSettings = connectionSettings;
		this.userSettings = userSettings;
		this.resultCache = resultCache;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Show schema designer
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/dbdesigner-start.html", method = RequestMethod.GET)
	public String startDesigner(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		connectionSettings.getDesign().getTableNames().clear();
		connectionSettings.getDesign().getTableNames().add(new QualifiedName(catalog, schema, object));
		connectionSettings.getDesign().reset();
		
		connectionSettings.setDesignerPreviewMode(false);
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Show the schema selection dialog
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-add.html", method = RequestMethod.GET)
	public Map<String, Object> showAddDialog()
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalogs", metadataService.getCatalogs(connectionSettings.getLinkName()));
		
		if (connectionSettings.getCatalog() != null)
			{
			model.put("catalog", connectionSettings.getCatalog());
			model.put("schemas", metadataService.getSchemas(connectionSettings.getLinkName()));
			if (connectionSettings.getSchema() != null)
				{
				model.put("schema", connectionSettings.getSchema());
				model.put("objects", metadataService.getTables(connectionSettings.getLinkName(), connectionSettings.getCatalog(), connectionSettings.getSchema()).keySet());
				}
			}
		
		return (model);
		}
	
	/**
	 * Show schema designer
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/dbdesigner-add.html", method = RequestMethod.GET)
	public String addDesigner(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		connectionSettings.setCatalog(catalog);
		connectionSettings.setSchema(schema);
		
		connectionSettings.getDesign().getTableNames().add(new QualifiedName(catalog, schema, object));
		connectionSettings.getDesign().modify();
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Show the schema selection dialog
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-addall.html", method = RequestMethod.GET)
	public Map<String, Object> showAddAllDialog()
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalogs", metadataService.getCatalogs(connectionSettings.getLinkName()));
		
		if (connectionSettings.getCatalog() != null)
			{
			model.put("catalog", connectionSettings.getCatalog());
			model.put("schemas", metadataService.getSchemas(connectionSettings.getLinkName()));
			if (connectionSettings.getSchema() != null)
				model.put("schema", connectionSettings.getSchema());
			}
		
		return (model);
		}
	
	/**
	 * Show schema designer
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/dbdesigner-addall.html", method = RequestMethod.GET)
	public String addAllDesigner(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		connectionSettings.setCatalog(catalog);
		connectionSettings.setSchema(schema);
		
		for (QualifiedName qn : metadataService.getTables(connectionSettings.getLinkName(), catalog, schema, object).keySet())
			connectionSettings.getDesign().getTableNames().add(qn);
		
		connectionSettings.getDesign().modify();
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Show schema designer
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/dbdesigner-remove.html", method = RequestMethod.GET)
	public String removeDesigner(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		connectionSettings.getDesign().getTableNames().remove(new QualifiedName(catalog, schema, object));
		connectionSettings.getDesign().modify();
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Show schema designer
	 * @return View
	 */
	@RequestMapping(value = "/db/*/dbdesigner-reset.html", method = RequestMethod.GET)
	public String resetDesigner(
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		connectionSettings.getDesign().getTableNames().clear();
		connectionSettings.getDesign().reset();
		
		connectionSettings.setDesignerPreviewMode(false);
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-load.html", method = RequestMethod.GET)
	public Map<String, Object> showLoadDialog(
			@RequestParam("q") String name
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("designs", userManagerService.listExtensionObjects(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION));
		model.put("design", name);
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-save.html", method = RequestMethod.GET)
	public Map<String, Object> showSaveDialog(
			@RequestParam("q") String name
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("designs", userManagerService.listExtensionObjects(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION));
		model.put("design", name);
		
		return (model);
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/design-load.html", method = RequestMethod.POST)
	public String loadDesign(
			@RequestParam(value = "q", required = false) String name
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name))
			{
			try	{
				final SortedSet<QualifiedName> tables = userManagerService.loadExtensionObject(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name, designPersister);
				connectionSettings.getDesign().getTableNames().clear();
				connectionSettings.getDesign().getTableNames().addAll(tables);
				connectionSettings.getDesign().persist(name);
				
				connectionSettings.setDesignerPreviewMode(true);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "loadDesign", e);
				}
			}
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/design-load.html", method = RequestMethod.POST, params = "delete=true")
	public String removeDesign(
			@RequestParam(value = "q", required = false) String name
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name))
			{
			try	{
				userManagerService.removeExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "removeDesign", e);
				}
			}
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Display the users
	 * @param name Preferred name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/design-save.html", method = RequestMethod.POST)
	public String saveDesign(
			@RequestParam(value = "q", required = false) String name
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(name) && !connectionSettings.getDesign().getTableNames().isEmpty())
			{
			try	{
				final String dn = userManagerService.saveExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name, connectionSettings.getDesign().getTableNames(), designPersister);
				connectionSettings.getDesign().persist(dn);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "saveDesign", e);
				}
			}
		
		return ("redirect:dbdesigner.html");
		}
	
	/**
	 * Show schema designer
	 * @param preview Preview flag
	 * @param compact Compact flag
	 * @param sort Sort flag
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbdesigner.html", method = RequestMethod.GET)
	public Map<String, Object> showDesigner(
			@RequestParam(value = "preview", required = false) Boolean preview,
			@RequestParam(value = "compact", required = false) Boolean compact,
			@RequestParam(value = "sort", required = false) Boolean sort
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		if (preview != null)
			connectionSettings.setDesignerPreviewMode(preview);
		if (compact != null)
			connectionSettings.setDesignerCompactMode(compact);
		if (sort != null)
			connectionSettings.setSortColumns(sort);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!connectionSettings.getDesign().getTableNames().isEmpty())
			{
			final SQLDialect dialect = getSQLDialect();
			
			final Set<QualifiedName> missing = new TreeSet<QualifiedName>();
			final Set<TableDescription> infos = metadataService.getTableInfos(connectionSettings.getLinkName(), connectionSettings.getDesign().getTableNames(), missing,
					connectionSettings.isDesignerCompactMode() ? ColumnMode.PK_FK : (connectionSettings.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL), null);
			
			for (QualifiedName qn : missing)
				frontendNotificationService.logObjectError(qn, ErrorKeys.TABLE_NOT_FOUND, dialect.getQualifiedTableName(qn));
			
			final Visualization def = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, connectionSettings.getLinkName(), connectionSettings.isDesignerPreviewMode(), connectionSettings.isDesignerPreviewMode(), dialect).getVisualization();
			resultCache.clearCachedObjects(CacheClass.SCHEMA_VISUALIZATION);
			final String key = resultCache.addCachedObject(CacheClass.SCHEMA_VISUALIZATION, def);
			
			model.put(RowSetConstants.ATTR_IMAGE_ID, key);
			model.put(RowSetConstants.ATTR_IMAGEMAP, visualizationService.getHtmlMap(def));
			model.put(RowSetConstants.ATTR_IMAGEMAP_ID, ViewConstants.IMAGEMAP_ID);
			}
		
		model.put("extensionJS", "jdbc.js");
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/designgraph.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignGraph(
			@RequestParam("key") String key
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Visualization def = resultCache.getCachedObject(CacheClass.SCHEMA_VISUALIZATION, key, Visualization.class);
		if (def == null)
			{
			logger.log(Level.WARNING, "Cached design graph not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getVisualizationDownloadSource(def, "design"));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/designgraph-source.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignGraphSource(
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SQLDialect dialect = getSQLDialect();
		
		final Set<QualifiedName> missing = new TreeSet<QualifiedName>();
		final Set<TableDescription> infos = metadataService.getTableInfos(connectionSettings.getLinkName(), connectionSettings.getDesign().getTableNames(), missing,
				connectionSettings.isDesignerCompactMode() ? ColumnMode.PK_FK : (connectionSettings.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL), null);
		
		final TableDescriptionVisualizer v = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, connectionSettings.getLinkName(), connectionSettings.isDesignerPreviewMode(), true, dialect);
		dataFormatterFactory.doWithDefaultTheme(v);
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getSourceTextDownloadSource(v.getVisualization(), "design"));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/designgraph-image.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignGraphImage()
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SQLDialect dialect = getSQLDialect();
		
		final Set<QualifiedName> missing = new TreeSet<QualifiedName>();
		final Set<TableDescription> infos = metadataService.getTableInfos(connectionSettings.getLinkName(), connectionSettings.getDesign().getTableNames(), missing,
				connectionSettings.isDesignerCompactMode() ? ColumnMode.PK_FK : (connectionSettings.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL), null);
		
		final TableDescriptionVisualizer v = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, connectionSettings.getLinkName(), connectionSettings.isDesignerPreviewMode(), true, dialect);
		dataFormatterFactory.doWithDefaultTheme(v);
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getImageDownloadSource(v.getVisualization(), "design"));
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
