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
package de.tweerlei.dbgrazer.web.controller.kubernetes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesApiService;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiObject;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiResource;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.dbgrazer.text.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.TimeService;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KubernetesBrowseController
	{
	private final KubernetesApiService clientService;
	private final ResultBuilderService resultBuilder;
	private final DataFormatterFactory factory;
	private final ResultTransformerService resultTransformer;
	private final TextTransformerService textFormatterService;
	private final QuerySettingsManager querySettingsManager;
	private final TimeService timeService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param clientService KubernetesApiService
	 * @param resultBuilder ResultBuilderService
	 * @param factory DataFormatterFactory
	 * @param resultTransformer ResultTransformerService
	 * @param textFormatterService TextFormatterService
	 * @param querySettingsManager QuerySettingsManager
	 * @param timeService TimeService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KubernetesBrowseController(KubernetesApiService clientService, TextTransformerService textFormatterService,
			DataFormatterFactory factory, ResultTransformerService resultTransformer,
			ResultBuilderService resultBuilder, QuerySettingsManager querySettingsManager,
			TimeService timeService,
			ConnectionSettings connectionSettings)
		{
		this.clientService = clientService;
		this.resultBuilder = resultBuilder;
		this.factory = factory;
		this.resultTransformer = resultTransformer;
		this.textFormatterService = textFormatterService;
		this.querySettingsManager = querySettingsManager;
		this.timeService = timeService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/cluster.html", method = RequestMethod.GET)
	public Map<String, Object> showNamespaces()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final long start = timeService.getCurrentTime();
		final Set<String> apiObjects = clientService.listNamespaces(connectionSettings.getLinkName());
		final long end = timeService.getCurrentTime();
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
		final long start2 = timeService.getCurrentTime();
		final Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources = clientService.getApiResources(connectionSettings.getLinkName());
		final long end2 = timeService.getCurrentTime();
		final Query query2 = new ViewImpl(KubernetesMessageKeys.KIND_LEVEL, null, null, null, null, null, null);
		final RowSet rs = buildResourcesRowSet(query2, apiResources, false, end2 - start2);
		results.put(KubernetesMessageKeys.KIND_TAB, new TabItem<RowSet>(rs, rs.getRows().size()));
		model.put("results", results);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(KubernetesMessageKeys.NAMESPACE_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildNamespaceRowSet(query, apiObjects, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KubernetesMessageKeys.NAMESPACE_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildNamespaceRowSet(Query query, Set<String> values, long time)
		{
		if (values.isEmpty())
			return (resultBuilder.createEmptyRowSet(query, RowSetConstants.INDEX_MULTILEVEL, time));
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.NAMESPACE, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		for (String n : values)
			rs.getRows().add(new DefaultResultRow(n, n));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, true);
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param namespace Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/namespace.html", method = RequestMethod.GET)
	public Map<String, Object> showNamespace(
			@RequestParam("namespace") String namespace
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		
		final long start = timeService.getCurrentTime();
		final Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources = clientService.getApiResources(connectionSettings.getLinkName());
		final long end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.NAMESPACE_LEVEL, null));
		final Query query = new ViewImpl(KubernetesMessageKeys.KIND_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildResourcesRowSet(query, apiResources, true, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KubernetesMessageKeys.KIND_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(namespace)));
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildResourcesRowSet(Query query, Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources, boolean namespaced, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.KIND, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.API_GROUP, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.API_VERSION, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, namespaced ? RowSetConstants.INDEX_MULTILEVEL : 0, columns);
		rs.setQueryTime(time);
		
		for (Map.Entry<String, Map<String, Map<String, KubernetesApiResource>>> ent : apiResources.entrySet())
			{
			for (Map.Entry<String, Map<String, KubernetesApiResource>> ent2 : ent.getValue().entrySet())
				{
				for (Map.Entry<String, KubernetesApiResource> ent3 : ent2.getValue().entrySet())
					{
					if (ent3.getValue().isNamespaced() == namespaced)
						rs.getRows().add(new DefaultResultRow(new ObjectKind(ent.getKey(), ent2.getKey(), ent3.getValue().getName()).toString(), ent3.getKey(), ent.getKey(), ent2.getKey()));
					}
				}
			}
		
		Collections.sort(rs.getRows(), new Comparator<ResultRow>()
			{
			@Override
			public int compare(ResultRow a, ResultRow b)
				{
				int d = a.getValues().get(1).toString().compareTo(b.getValues().get(1).toString());
				if (d != 0)
					return (d);
				d = a.getValues().get(2).toString().compareTo(b.getValues().get(2).toString());
				if (d != 0)
					return (d);
				d = a.getValues().get(3).toString().compareTo(b.getValues().get(3).toString());
				return (d);
				}
			});
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, true);
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/apiobjects.html", method = RequestMethod.GET)
	public Map<String, Object> showApiObjects(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		
		final ObjectKind parts = ObjectKind.parse(kind);
		
		final long start = timeService.getCurrentTime();
		final Set<KubernetesApiObject> apiObjects = clientService.listApiObjects(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind);
		final long end = timeService.getCurrentTime();
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
		final Query query2 = new ViewImpl(KubernetesMessageKeys.OBJECT_LEVEL, null, null, null, null, null, null);
		final RowSet rs = buildDetailsRowSet(query2, apiObjects, end - start);
		results.put(KubernetesMessageKeys.KIND_TAB, new TabItem<RowSet>(rs, rs.getRows().size()));
		model.put("results", results);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.NAMESPACE_LEVEL, null));
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.KIND_LEVEL, null));
		final Query query = new ViewImpl(KubernetesMessageKeys.OBJECT_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, apiObjects, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KubernetesMessageKeys.OBJECT_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(namespace, kind)));
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Set<KubernetesApiObject> values, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.OBJECT, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		for (KubernetesApiObject n : values)
			rs.getRows().add(new DefaultResultRow(n.getName(), n.getName()));
		
		// Format dates
		resultTransformer.translateRowSet(rs, factory.getWebFormatter());
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, false);
		return (rs);
		}
	
	private RowSet buildDetailsRowSet(Query query, Set<KubernetesApiObject> values, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.OBJECT, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.TIMESTAMP, ColumnType.DATE, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.LABELS, ColumnType.STRING, null, null, null, null));
		if (!values.isEmpty())
			{
			for (Map.Entry<String, Object> ent : values.iterator().next().getProperties().entrySet())
				columns.add(new ColumnDefImpl(ent.getKey(), ColumnType.forObject(ent.getValue()), null, null, null, null));
			}
		final RowSetImpl rs = new RowSetImpl(query, 0, columns);
		rs.setQueryTime(time);
		
		for (KubernetesApiObject n : values)
			{
			final DefaultResultRow row = new DefaultResultRow(3 + n.getProperties().size());
			row.getValues().add(n.getName());
			row.getValues().add(n.getCreationTimestamp());
			row.getValues().add(n.getLabels().toString());
			for (Object v : n.getProperties().values())
				row.getValues().add(v);
			rs.getRows().add(row);
			}
		
		// Format dates
		resultTransformer.translateRowSet(rs, factory.getWebFormatter());
		
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @param name Object name
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/apiobject.html", method = RequestMethod.GET)
	public Map<String, Object> showApiObject(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind,
			@RequestParam("name") String name,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showApiObjectInternal(namespace, kind, name, format, formatting));
		}
	
	/**
	 * Show the file browser
	 * @param namespace Namespace name
	 * @param kind Kind
	 * @param name Object name
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/apiobject.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxApiObject(
			@RequestParam("namespace") String namespace,
			@RequestParam("kind") String kind,
			@RequestParam("name") String name,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showApiObjectInternal(namespace, kind, name, format, formatting));
		}
	
	private Map<String, Object> showApiObjectInternal(String namespace, String kind, String name, String format, Boolean formatting)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final String formatName = "JSON";
		final boolean formattingActive = true;
/*		if (format == null)
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
			}*/
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		model.put("name", name);
		model.put("format", formatName);
		model.put("formatting", formattingActive);
		
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final ObjectKind parts = ObjectKind.parse(kind);
		
		final String json = clientService.getApiObject(connectionSettings.getLinkName(), namespace, parts.group, parts.version, parts.kind, name);
		
		final Map<String, TabItem<Object>> tabs = new HashMap<String, TabItem<Object>>(1);
		final String txt;
		if (json == null)
			txt = null;
		else
			{
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
			if (formattingActive)
				options.add(TextTransformerService.Option.FORMATTING);
			txt = textFormatterService.format(json, formatName, options);
			}
		tabs.put(name, new TabItem<Object>(txt, 1));
		
		model.put("tabs", tabs);
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/cluster-reload.html", method = RequestMethod.GET)
	public String clearCache()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		clientService.flushCache(connectionSettings.getLinkName());
		
		return (ViewConstants.EMPTY_VIEW);
		}
	}
