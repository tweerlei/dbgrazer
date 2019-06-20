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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common5.collections.Pair;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesClientService;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.TimeService;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1ConfigMapList;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1EndpointsList;
import io.kubernetes.client.models.V1Event;
import io.kubernetes.client.models.V1EventList;
import io.kubernetes.client.models.V1LimitRange;
import io.kubernetes.client.models.V1LimitRangeList;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1PersistentVolumeClaimList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1PodTemplate;
import io.kubernetes.client.models.V1PodTemplateList;
import io.kubernetes.client.models.V1ReplicationController;
import io.kubernetes.client.models.V1ReplicationControllerList;
import io.kubernetes.client.models.V1ResourceQuota;
import io.kubernetes.client.models.V1ResourceQuotaList;
import io.kubernetes.client.models.V1Secret;
import io.kubernetes.client.models.V1SecretList;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceAccount;
import io.kubernetes.client.models.V1ServiceAccountList;
import io.kubernetes.client.models.V1ServiceList;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KubernetesBrowseController
	{
	private static enum Kind
		{
		ConfigMap,
		Endpoints,
		Event,
		LimitRange,
		Pod,
		PodTemplate,
		ReplicationController,
		ResourceQuota,
		Secret,
		Service,
		ServiceAccount,
		PersistentVolumeClaim
		}
	
	private final KubernetesClientService clientService;
	private final ResultBuilderService resultBuilder;
	private final TextTransformerService textFormatterService;
	private final QuerySettingsManager querySettingsManager;
	private final TimeService timeService;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param clientService KubernetesClientService
	 * @param resultBuilder ResultBuilderService
	 * @param textFormatterService TextFormatterService
	 * @param querySettingsManager QuerySettingsManager
	 * @param timeService TimeService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KubernetesBrowseController(KubernetesClientService clientService, TextTransformerService textFormatterService,
			ResultBuilderService resultBuilder, QuerySettingsManager querySettingsManager,
			TimeService timeService,
			ConnectionSettings connectionSettings)
		{
		this.clientService = clientService;
		this.resultBuilder = resultBuilder;
		this.textFormatterService = textFormatterService;
		this.querySettingsManager = querySettingsManager;
		this.timeService = timeService;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
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
		
		final CoreV1Api api = clientService.getCoreV1Api(connectionSettings.getLinkName());
		
		final List<String> apiObjects = new LinkedList<String>();
		final long start = timeService.getCurrentTime();
		try	{
			final V1NamespaceList l = api.listNamespace(null, null, null, null, null, null, null, null, null);
			for (V1Namespace n : l.getItems())
				apiObjects.add(n.getMetadata().getName());
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		final long end = timeService.getCurrentTime();
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
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
	
	private RowSet buildNamespaceRowSet(Query query, List<String> values, long time)
		{
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
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.NAMESPACE_LEVEL, null));
		final Query query = new ViewImpl(KubernetesMessageKeys.KIND_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildKindsRowSet(query);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KubernetesMessageKeys.KIND_TAB, new TabItem<RowSet>(cats, cats.getRows().size() - 1));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(namespace)));
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildKindsRowSet(Query query)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.KIND, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		
		for (Kind kind : Kind.values())
			rs.getRows().add(new DefaultResultRow(kind.name(), kind.name()));
		
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
			@RequestParam("kind") Kind kind
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		
		final CoreV1Api api = clientService.getCoreV1Api(connectionSettings.getLinkName());
		
		final List<Pair<String, String>> apiObjects = new LinkedList<Pair<String, String>>();
		final long start = timeService.getCurrentTime();
		switch (kind)
			{
			case ConfigMap:
				try	{
					final V1ConfigMapList l = api.listNamespacedConfigMap(namespace, null, null, null, null, null, null, null, null, null);
					for (V1ConfigMap n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case Endpoints:
				try	{
					final V1EndpointsList l = api.listNamespacedEndpoints(namespace, null, null, null, null, null, null, null, null, null);
					for (V1Endpoints n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case Event:
				try	{
					final V1EventList l = api.listNamespacedEvent(namespace, null, null, null, null, null, null, null, null, null);
					for (V1Event n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case Pod:
				try	{
					final V1PodList l = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);
					for (V1Pod n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case Secret:
				try	{
					final V1SecretList l = api.listNamespacedSecret(namespace, null, null, null, null, null, null, null, null, null);
					for (V1Secret n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case Service:
				try	{
					final V1ServiceList l = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null);
					for (V1Service n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case LimitRange:
				try	{
					final V1LimitRangeList l = api.listNamespacedLimitRange(namespace, null, null, null, null, null, null, null, null, null);
					for (V1LimitRange n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case PodTemplate:
				try	{
					final V1PodTemplateList l = api.listNamespacedPodTemplate(namespace, null, null, null, null, null, null, null, null, null);
					for (V1PodTemplate n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case ReplicationController:
				try	{
					final V1ReplicationControllerList l = api.listNamespacedReplicationController(namespace, null, null, null, null, null, null, null, null, null);
					for (V1ReplicationController n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case ResourceQuota:
				try	{
					final V1ResourceQuotaList l = api.listNamespacedResourceQuota(namespace, null, null, null, null, null, null, null, null, null);
					for (V1ResourceQuota n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case ServiceAccount:
				try	{
					final V1ServiceAccountList l = api.listNamespacedServiceAccount(namespace, null, null, null, null, null, null, null, null, null);
					for (V1ServiceAccount n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			
			case PersistentVolumeClaim:
				try	{
					final V1PersistentVolumeClaimList l = api.listNamespacedPersistentVolumeClaim(namespace, null, null, null, null, null, null, null, null, null);
					for (V1PersistentVolumeClaim n : l.getItems())
						apiObjects.add(new Pair<String, String>(n.getKind(), n.getMetadata().getName()));
					}
				catch (ApiException e)
					{
					logger.log(Level.WARNING, e.getResponseBody(), e);
					}
				break;
			}
		final long end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.NAMESPACE_LEVEL, null));
		levels.add(new SubQueryDefImpl(KubernetesMessageKeys.KIND_LEVEL, null));
		final Query query = new ViewImpl(KubernetesMessageKeys.OBJECT_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, apiObjects, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KubernetesMessageKeys.OBJECT_TAB, new TabItem<RowSet>(cats, cats.getRows().size() - 1));
		
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(namespace, kind.name())));
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, List<Pair<String, String>> values, long time)
		{
		if (values.isEmpty())
			return (resultBuilder.createEmptyRowSet(query, RowSetConstants.INDEX_MULTILEVEL, time));
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KubernetesMessageKeys.OBJECT, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		for (Pair<String, String> n : values)
			rs.getRows().add(new DefaultResultRow(n.getSecond(), n.getSecond()));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, false);
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
			@RequestParam("kind") Kind kind,
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
			@RequestParam("kind") Kind kind,
			@RequestParam("name") String name,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showApiObjectInternal(namespace, kind, name, format, formatting));
		}
	
	private Map<String, Object> showApiObjectInternal(String namespace, Kind kind, String name, String format, Boolean formatting)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
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
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("namespace", namespace);
		model.put("kind", kind);
		model.put("name", name);
		model.put("format", formatName);
		model.put("formatting", formattingActive);
		
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final CoreV1Api api = clientService.getCoreV1Api(connectionSettings.getLinkName());
		Object content = null;
		try	{
			switch (kind)
				{
				case ConfigMap:
					content = api.readNamespacedConfigMap(name, namespace, null, null, null);
					break;
				case Endpoints:
					content = api.readNamespacedEndpoints(name, namespace, null, null, null);
					break;
				case Event:
					content = api.readNamespacedEvent(name, namespace, null, null, null);
					break;
				case LimitRange:
					content = api.readNamespacedLimitRange(name, namespace, null, null, null);
					break;
				case PersistentVolumeClaim:
					content = api.readNamespacedPersistentVolumeClaim(name, namespace, null, null, null);
					break;
				case Pod:
					content = api.readNamespacedPod(name, namespace, null, null, null);
					break;
				case PodTemplate:
					content = api.readNamespacedPodTemplate(name, namespace, null, null, null);
					break;
				case ReplicationController:
					content = api.readNamespacedReplicationController(name, namespace, null, null, null);
					break;
				case ResourceQuota:
					content = api.readNamespacedResourceQuota(name, namespace, null, null, null);
					break;
				case Secret:
					content = api.readNamespacedSecret(name, namespace, null, null, null);
					break;
				case Service:
					content = api.readNamespacedService(name, namespace, null, null, null);
					break;
				case ServiceAccount:
					content = api.readNamespacedServiceAccount(name, namespace, null, null, null);
					break;
				}
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		final Map<String, TabItem<Object>> tabs = new HashMap<String, TabItem<Object>>(1);
		final String txt;
		if (content == null)
			txt = null;
		else
			{
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
			if (formattingActive)
				options.add(TextTransformerService.Option.FORMATTING);
			final String json = api.getApiClient().getJSON().serialize(content);
			txt = textFormatterService.format(json, formatName, options);
			}
		tabs.put(name, new TabItem<Object>(txt, 1));
		
		model.put("tabs", tabs);
		model.put("extensionJS", KubernetesMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	}
