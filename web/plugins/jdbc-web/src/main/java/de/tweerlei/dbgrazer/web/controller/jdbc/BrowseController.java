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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.ConfigKeys;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Joins;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.OrderBy;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.MetadataExportService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService.GraphMode;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.service.jdbc.BrowserSettingsManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableFilterEntry;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Browse the DB metadata
 * 
 * @author Robert Wruck
 */
@Controller
public class BrowseController
	{
	// Magic indexes for the tabs in dbobject view
	private static final int INDEX_GRAPH = 0;
	private static final int INDEX_DETAIL = 1;
	private static final int INDEX_DDL = 2;
	private static final int INDEX_DML = 3;
	private static final int INDEX_DATA = 4;
	private static final int INDEX_ANCESTORS = 5;
	private static final int INDEX_DESCENDANTS = 6;
	
	/** Helper bean for table relations */
	public static class RelationInfo
		{
		private final TableDescription info;
		private final ForeignKeyDescription fk;
		
		/**
		 * Constructor
		 * @param info TableDescription
		 * @param fk ForeignKeyDescription through which the TableDescription was referenced
		 */
		public RelationInfo(TableDescription info, ForeignKeyDescription fk)
			{
			this.info = info;
			this.fk = fk;
			}
		
		/**
		 * Get the info
		 * @return the info
		 */
		public TableDescription getInfo()
			{
			return info;
			}
		
		/**
		 * Get the FK
		 * @return the fk
		 */
		public ForeignKeyDescription getFk()
			{
			return fk;
			}
		}
	
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
	
	private static class ExplorerLinkBuilder implements SchemaTransformerService.LinkBuilder
		{
		public ExplorerLinkBuilder()
			{
			}
		
		@Override
		public String buildLink(QualifiedName qname, boolean fk)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append("javascript:void(showDBObject(null, '");
			if (qname.getCatalogName() != null)
				sb.append(qname.getCatalogName());
			sb.append("', '");
			if (qname.getSchemaName() != null)
				sb.append(qname.getSchemaName());
			sb.append("', '").append(qname.getObjectName()).append("'))");
			return (sb.toString());
			}
		}
	
	private static class TableDescriptionVisualizer implements Runnable
		{
		private final SchemaTransformerService schemaTransformer;
		private final FrontendHelperService frontendHelper;
		private final Set<TableDescription> infos;
		private final QualifiedName qname;
		private final String connection;
		private final boolean toplevel;
		private final SQLDialect dialect;
		
		private Visualization def;
		
		public TableDescriptionVisualizer(SchemaTransformerService schemaTransformer, FrontendHelperService frontendHelper,
				Set<TableDescription> infos, QualifiedName qname, String connection, boolean toplevel, SQLDialect dialect)
			{
			this.schemaTransformer = schemaTransformer;
			this.frontendHelper = frontendHelper;
			this.infos = infos;
			this.qname = qname;
			this.connection = connection;
			this.toplevel = toplevel;
			this.dialect = dialect;
			this.def = null;
			}
		
		@Override
		public void run()
			{
			def = schemaTransformer.buildGraph(infos, qname, ViewConstants.IMAGEMAP_ID, GraphMode.START_REFS,
					toplevel ? new ToplevelLinkBuilder(frontendHelper, connection) : new ExplorerLinkBuilder(),
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
	private final ConfigAccessor configService;
	private final SQLGeneratorService sqlGenerator;
	private final ResultBuilderService resultBuilder;
	private final DataFormatterFactory dataFormatterFactory;
	private final MetadataExportService exportService;
	private final VisualizationService visualizationService;
	private final TextTransformerService textFormatterService;
	private final SchemaTransformerService schemaTransformer;
	private final FrontendHelperService frontendHelper;
	private final QuerySettingsManager querySettingsManager;
	private final BrowserSettingsManagerService browserSettingsManager;
	private final ResultCache resultCache;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param configService ConfigAccessor
	 * @param sqlGenerator SQLGeneratorService
	 * @param resultBuilder ResultBuilderService
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param exportService MetadataExportService
	 * @param visualizationService VisualizationService
	 * @param frontendHelper FrontendHelperService
	 * @param querySettingsManager QuerySettingsManager
	 * @param textFormatterService TextFormatterService
	 * @param browserSettingsManager BrowserSettingsManagerService
	 * @param schemaTransformer SchemaTransformerService
	 * @param resultCache ResultCache
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public BrowseController(MetadataService metadataService, ConfigAccessor configService,
			SQLGeneratorService sqlGenerator, DataFormatterFactory dataFormatterFactory,
			ResultBuilderService resultBuilder, MetadataExportService exportService,
			VisualizationService visualizationService, SchemaTransformerService schemaTransformer,
			TextTransformerService textFormatterService, BrowserSettingsManagerService browserSettingsManager,
			FrontendHelperService frontendHelper, QuerySettingsManager querySettingsManager,
			ResultCache resultCache, ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.configService = configService;
		this.sqlGenerator = sqlGenerator;
		this.resultBuilder = resultBuilder;
		this.dataFormatterFactory = dataFormatterFactory;
		this.exportService = exportService;
		this.visualizationService = visualizationService;
		this.textFormatterService = textFormatterService;
		this.browserSettingsManager = browserSettingsManager;
		this.schemaTransformer = schemaTransformer;
		this.frontendHelper = frontendHelper;
		this.querySettingsManager = querySettingsManager;
		this.resultCache = resultCache;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbcatalogs.html", method = RequestMethod.GET)
	public Map<String, Object> showCatalogs()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("dbinfo", metadataService.getDBInfo(connectionSettings.getLinkName()));
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(JdbcMessageKeys.CATALOG_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, metadataService.getCatalogs(connectionSettings.getLinkName()), true);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(JdbcMessageKeys.CATALOG_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		
		return (model);
		}
	
	/**
	 * Show schemas
	 * @param catalog Catalog
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbschemas.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemas(
			@RequestParam("catalog") String catalog
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(JdbcMessageKeys.CATALOG_LEVEL, null));
		final Query query = new ViewImpl(JdbcMessageKeys.SCHEMA_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, metadataService.getSchemas(connectionSettings.getLinkName()), true);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(JdbcMessageKeys.SCHEMA_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(catalog)));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param conn Connection name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbcatalogs.html", method = RequestMethod.GET)
	public Map<String, Object> showCatalogs(
			@RequestParam("c") String conn
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalogs", metadataService.getCatalogs(conn));
		
		return (model);
		}
	
	/**
	 * Show schemas
	 * @param conn Connection name
	 * @param catalog Catalog
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbschemas.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemas(
			@RequestParam("c") String conn,
			@RequestParam("catalog") String catalog
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		model.put("schemas", metadataService.getSchemas(conn));
		
		return (model);
		}
	
	/**
	 * Show objects
	 * @param conn Connection name
	 * @param catalog Catalog
	 * @param schema Schema
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbobjects.html", method = RequestMethod.GET)
	public Map<String, Object> showObjects(
			@RequestParam("c") String conn,
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		model.put("schema", schema);
		model.put("objects", metadataService.getTables(conn, catalog, schema).keySet());
		
		return (model);
		}
	
	/**
	 * Show the schema selection dialog
	 * @param catalog Selected catalog
	 * @param schema Selected schema
	 * @param backTo Page to return to
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/choose-schema.html", method = RequestMethod.GET)
	public Map<String, Object> showChooseSchemaDialog(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("backTo") String backTo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("backTo", backTo);
		model.put("catalogs", metadataService.getCatalogs(connectionSettings.getLinkName()));
		
		if (catalog != null)
			{
			model.put("catalog", catalog);
			model.put("schemas", metadataService.getSchemas(connectionSettings.getLinkName()));
			if (schema != null)
				model.put("schema", schema);
			}
		
		return (model);
		}
	
	/**
	 * Show the schema selection dialog
	 * @param catalog Selected catalog
	 * @param schema Selected schema
	 * @param object Selected object
	 * @param backTo Page to return to
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/choose-object.html", method = RequestMethod.GET)
	public Map<String, Object> showChooseObjectDialog(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("backTo") String backTo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("backTo", backTo);
		model.put("catalogs", metadataService.getCatalogs(connectionSettings.getLinkName()));
		
		if (catalog != null)
			{
			model.put("catalog", catalog);
			model.put("schemas", metadataService.getSchemas(connectionSettings.getLinkName()));
			if (schema != null)
				{
				model.put("schema", schema);
				model.put("objects", metadataService.getTables(connectionSettings.getLinkName(), catalog, schema).keySet());
				model.put("object", object);
				}
			}
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbobjects.html", method = RequestMethod.GET)
	public Map<String, Object> showObjects(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		browserSettingsManager.setCatalog(catalog);
		browserSettingsManager.setSchema(schema);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		model.put("schema", schema);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(JdbcMessageKeys.CATALOG_LEVEL, null));
		levels.add(new SubQueryDefImpl(JdbcMessageKeys.SCHEMA_LEVEL, null));
		final Query query = new ViewImpl(JdbcMessageKeys.OBJECT_LEVEL, null, null, null, null, levels, null);
		
		final Map<String, TabItem<RowSet>> groups = groupObjects(query, metadataService.getTables(connectionSettings.getLinkName(), catalog, schema));
		
		model.put("query", query);
		model.put("tabs", groups);
		model.put("tableColumns", Collections.emptyList());
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(catalog, schema)));
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private Map<String, TabItem<RowSet>> groupObjects(Query query, Map<QualifiedName, String> objects)
		{
		final Map<String, Set<String>> tmp = new HashMap<String, Set<String>>();
		
		for (Map.Entry<QualifiedName, String> ent : objects.entrySet())
			{
			Set<String> s = tmp.get(ent.getValue());
			if (s == null)
				{
				s = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
				tmp.put(ent.getValue(), s);
				}
			s.add(ent.getKey().getObjectName());
			}
		
		final Map<String, TabItem<RowSet>> ret = new LinkedHashMap<String, TabItem<RowSet>>();
		
		// Make sure that "TABLE" is always the first entry, even if there are no tables
		ret.put(TableDescription.TABLE, new TabItem<RowSet>(resultBuilder.createEmptyRowSet(query, 0, 0), 0));
		
		for (Map.Entry<String, Set<String>> ent : tmp.entrySet())
			{
			final RowSet rs = buildRowSet(query, ent.getValue(), false);
			ret.put(ent.getKey(), new TabItem<RowSet>(rs, rs.getRows().size()));
			}
		
		return (ret);
		}
	
	private RowSet buildRowSet(Query query, Set<String> values, boolean more)
		{
		final RowSetImpl ret = resultBuilder.createRowSet(query, RowSetConstants.INDEX_MULTILEVEL, "Name", values, 0);
		ret.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, more);
		return (ret);
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param depth Table graph recursion depth
	 * @param allSchemas Include tables from other schemas
	 * @param sort Sort flag
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbobject.html", method = RequestMethod.GET)
	public Map<String, Object> showObject(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam(value = "depth", required = false) Integer depth,
			@RequestParam(value = "allSchemas", required = false) Boolean allSchemas,
			@RequestParam(value = "sort", required = false) Boolean sort
			)
		{
		return (showDbObject(catalog, schema, object, depth, allSchemas, sort, true));
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param depth Table graph recursion depth
	 * @param allSchemas Include tables from other schemas
	 * @param sort Sort flag
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbobject.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxObject(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam(value = "depth", required = false) Integer depth,
			@RequestParam(value = "allSchemas", required = false) Boolean allSchemas,
			@RequestParam(value = "sort", required = false) Boolean sort
			)
		{
		return (showDbObject(catalog, schema, object, depth, allSchemas, sort, false));
		}
	
	private Map<String, Object> showDbObject(String catalog, String schema, String object, Integer depth, Boolean allSchemas, Boolean sort, boolean toplevel)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		browserSettingsManager.setCatalog(catalog);
		browserSettingsManager.setSchema(schema);
		if (allSchemas != null)
			browserSettingsManager.setExpandOtherSchemas(allSchemas);
		if (sort != null)
			browserSettingsManager.setSortColumns(sort);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final int d = getEffectiveDepth(depth);
		final QualifiedName qname = new QualifiedName(catalog, schema, object);
		
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qname, d, browserSettingsManager.isExpandOtherSchemas(),
				browserSettingsManager.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		final TableDescription info = schemaTransformer.findTable(infos, qname, dialect);
		final List<PrivilegeDescription> privs = new ArrayList<PrivilegeDescription>(info.getPrivileges());
		Collections.sort(privs, new Comparator<PrivilegeDescription>()
			{
			@Override
			public int compare(PrivilegeDescription a, PrivilegeDescription b)
				{
				int i = StringUtils.compareTo(a.getGrantee(), b.getGrantee());
				if (i != 0)
					return (i);
				i = StringUtils.compareTo(a.getPrivilege(), b.getPrivilege());
				return (i);
				}
			});
		final List<String> pkIndices = schemaTransformer.getKeyIndices(info);
		
		final Visualization def = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, qname, connectionSettings.getLinkName(), toplevel, dialect).getVisualization();
		
		resultCache.clearCachedObjects(CacheClass.SCHEMA_VISUALIZATION);
		final String key = resultCache.addCachedObject(CacheClass.SCHEMA_VISUALIZATION, def);
		
		model.put(RowSetConstants.ATTR_IMAGE_ID, key);
		model.put(RowSetConstants.ATTR_IMAGEMAP, visualizationService.getHtmlMap(def));
		model.put(RowSetConstants.ATTR_IMAGEMAP_ID, ViewConstants.IMAGEMAP_ID);
		
		final Map<String, TabItem<Integer>> tabs = new LinkedHashMap<String, TabItem<Integer>>();
		tabs.put(MessageKeys.GRAPH_TAB, new TabItem<Integer>(INDEX_GRAPH));
		tabs.put(MessageKeys.DETAIL_TAB, new TabItem<Integer>(INDEX_DETAIL));
		tabs.put(MessageKeys.ANCESTORS_TAB, new TabItem<Integer>(INDEX_ANCESTORS));
		tabs.put(MessageKeys.DESCENDANTS_TAB, new TabItem<Integer>(INDEX_DESCENDANTS));
		tabs.put(MessageKeys.DDL_TAB, new TabItem<Integer>(INDEX_DDL));
		tabs.put(MessageKeys.DML_TAB, new TabItem<Integer>(INDEX_DML));
		tabs.put(MessageKeys.DATA_TAB, new TabItem<Integer>(INDEX_DATA));
		
		model.put("browserSettings", browserSettingsManager);
		model.put("depth", d);
		model.put("tabs", tabs);
		model.put("catalog", catalog);
		model.put("schema", schema);
		model.put("object", object);
		model.put("info", info);
		model.put("infos", Collections.singletonMap(dialect.getQualifiedTableName(info.getName()), new RelationInfo(info, null)));
		model.put("pkIndices", pkIndices);
		model.put("privs", privs);
		model.put("statement", generateSELECT(info, dialect));
		model.put("ddl", generateDDL(info, dialect));
		model.put("dml", generateDML(info, dialect));
		model.put("maxDepth", configService.get(ConfigKeys.ERM_LEVELS));
		model.put("tableColumns", Collections.emptyList());
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final TableFilterEntry filter = browserSettingsManager.getTableFilters().get(qname.toString());
		if (filter != null)
			{
			model.put("where", filter.getWhere());
			model.put("orderBy", filter.getOrderBy());
			}
		
		return (model);
		}
	
	private int getEffectiveDepth(Integer depth)
		{
		if (depth == null)
			return (0);
		
		final int maxDepth = configService.get(ConfigKeys.ERM_LEVELS);
		
		return ((depth < maxDepth) ? depth : maxDepth);
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbobject-export.html", method = RequestMethod.GET)
	public Map<String, Object> exportObject(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qname = new QualifiedName(catalog, schema, object);
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qname, 0, browserSettingsManager.isExpandOtherSchemas(),
				browserSettingsManager.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		final TableDescription info = schemaTransformer.findTable(infos, qname, dialect);
		
		final Map<String, Object> attributes = new HashMap<String, Object>();
		final TableDescriptionVisualizer v = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, qname, connectionSettings.getLinkName(), true, dialect);
		dataFormatterFactory.doWithDefaultTheme(v);
		attributes.put(RowSetConstants.ATTR_VISUALIZATION, v.getVisualization());
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, exportService.getExportDownloadSource(connectionSettings.getLinkName(), info, dialect, attributes, format));
		
		return (model);
		}
	
	private String generateDDL(TableDescription t, SQLDialect dialect)
		{
		final StatementProducer p = schemaTransformer.buildDDL(new SQLSchema(null, null, Collections.singleton(t)), dialect);
		
		return (resultBuilder.writeScript(p, null, dialect.getStatementTerminator() + "\n"));
		}
	
	private String generateDML(TableDescription t, SQLDialect dialect)
		{
		final StatementProducer p = schemaTransformer.buildDML(t, dialect);
		
		return (resultBuilder.writeScript(p, null, dialect.getStatementTerminator() + "\n"));
		}
	
	private String generateSELECT(TableDescription t, SQLDialect dialect)
		{
		final String stmt = sqlGenerator.generateSelect(t, Style.INDENTED, Joins.ALL, null, OrderBy.NONE, dialect);
		
		return (stmt + "\n");
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbgraph.html", method = RequestMethod.GET)
	public Map<String, Object> showGraph(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("key") String key
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qn = new QualifiedName(catalog, schema, object);
		final SQLDialect dialect = getSQLDialect();
		final Visualization def = resultCache.getCachedObject(CacheClass.SCHEMA_VISUALIZATION, key, Visualization.class);
		if (def == null)
			{
			logger.log(Level.WARNING, "Cached graph not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getVisualizationDownloadSource(def, dialect.getQualifiedTableName(qn)));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param depth Table graph recursion depth
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbgraph-source.html", method = RequestMethod.GET)
	public Map<String, Object> showGraphSource(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("depth") Integer depth
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final int d = getEffectiveDepth(depth);
		final QualifiedName qn = new QualifiedName(catalog, schema, object);
		
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qn, d, browserSettingsManager.isExpandOtherSchemas(),
				browserSettingsManager.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		
		final TableDescriptionVisualizer v = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, qn, connectionSettings.getLinkName(), true, dialect);
		dataFormatterFactory.doWithDefaultTheme(v);
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getSourceTextDownloadSource(v.getVisualization(), dialect.getQualifiedTableName(qn)));
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param depth Table graph recursion depth
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dbgraph-image.html", method = RequestMethod.GET)
	public Map<String, Object> showGraphImage(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("depth") Integer depth
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final int d = getEffectiveDepth(depth);
		final QualifiedName qn = new QualifiedName(catalog, schema, object);
		
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qn, d, browserSettingsManager.isExpandOtherSchemas(),
				browserSettingsManager.isSortColumns() ? ColumnMode.SORTED : ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		
		final TableDescriptionVisualizer v = new TableDescriptionVisualizer(schemaTransformer, frontendHelper, infos, qn, connectionSettings.getLinkName(), true, dialect);
		dataFormatterFactory.doWithDefaultTheme(v);
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getImageDownloadSource(v.getVisualization(), dialect.getQualifiedTableName(qn)));
		
		return (model);
		}
	
	/**
	 * Get the referenced foreign keys of a table
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param dir Get referenced instead of referencing tables
	 * @param label Control label
	 * @param left Parent IDs
	 * @param target Target element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbtree.html", method = RequestMethod.GET)
	public Map<String, Object> getRelations(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("dir") Boolean dir,
			@RequestParam("label") String label,
			@RequestParam("left") String left,
			@RequestParam("target") String target
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qn = new QualifiedName(catalog, schema, object);
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qn, 1, true, ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		final TableDescription info = schemaTransformer.findTable(infos, qn, dialect);
		
		final Map<String, RelationInfo> tables = new TreeMap<String, RelationInfo>();
		final List<ForeignKeyDescription> fks = dir ? info.getReferencedKeys() : info.getReferencingKeys();
		for (ForeignKeyDescription fk : fks)
			{
			final TableDescription t = schemaTransformer.findTable(infos, fk.getTableName(), dialect);
			if (t != null)
				tables.put(dialect.getQualifiedTableName(fk.getTableName()), new RelationInfo(t, fk));
			}
		
		model.put("dir", dir);
		model.put("label", label);
		model.put("left", StringUtils.empty(left) ? left : (left + "-"));
		model.put("targetElement", target);
		model.put("infos", tables);
		
		return (model);
		}	
	
	/**
	 * Get the referenced foreign keys of a table
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param object Object name
	 * @param pk PK
	 * @param target Target element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbrefs.html", method = RequestMethod.GET)
	public Map<String, Object> getRefs(
			@RequestParam("catalog") String catalog,
			@RequestParam("schema") String schema,
			@RequestParam("object") String object,
			@RequestParam("pk") String pk,
			@RequestParam("target") String target
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QualifiedName qn = new QualifiedName(catalog, schema, object);
		final Set<TableDescription> infos = metadataService.getTableInfoRecursive(connectionSettings.getLinkName(), qn, 1, true, ColumnMode.ALL, null);
		final SQLDialect dialect = getSQLDialect();
		final TableDescription info = schemaTransformer.findTable(infos, qn, dialect);
		
		final Map<String, RelationInfo> tables = new TreeMap<String, RelationInfo>();
		final List<ForeignKeyDescription> fks = info.getReferencingKeys();
		for (ForeignKeyDescription fk : fks)
			{
			final TableDescription t = schemaTransformer.findTable(infos, fk.getTableName(), dialect);
			if (t != null)
				tables.put(dialect.getQualifiedTableName(fk.getTableName()), new RelationInfo(t, fk));
			}
		
		model.put("pk", pk);
		model.put("targetElement", target);
		model.put("infos", tables);
		
		return (model);
		}	
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/metadata-reset.html", method = RequestMethod.GET)
	public String clearCache()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		metadataService.flushCache(connectionSettings.getLinkName());
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/metalinks.html", method = RequestMethod.GET)
	public Map<String, Object> showDownloadMenu()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", exportService.getSupportedExportFormats());
		
		return (model);
		}
	
	/**
	 * Create a chart image for a CHART type query
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-chart.html", method = RequestMethod.GET)
	public Map<String, Object> performChartQuery(@RequestParam("key") String key)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Visualization c = resultCache.getCachedObject(CacheClass.RESULT_VISUALIZATION, key, Visualization.class);
		if (c == null)
			{
			logger.log(Level.WARNING, "Cached SQL chart not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getVisualizationDownloadSource(c, "chart"));
		
		return (model);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
