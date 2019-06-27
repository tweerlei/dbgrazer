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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.func.predicate.Predicate;
import de.tweerlei.common5.func.unary.UnaryFunction;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryCheckResult;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.exception.RedirectException;
import de.tweerlei.dbgrazer.web.extension.ExtensionGroup;
import de.tweerlei.dbgrazer.web.extension.ExtensionLink;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.ExportService;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryTransformerService;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.web.view.GenericDownloadView;
import de.tweerlei.spring.web.view.JsonDownloadSource;

/**
 * Controller for query browsing
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryController
	{
	private final QueryService queryService;
	private final QueryTransformerService queryTransformerService;
	private final ResultDownloadService downloadService;
	private final ExportService exportService;
	private final FrontendHelperService frontendHelper;
	private final FrontendExtensionService extensionService;
	private final UserSettings userSettings;
	private final ResultCache resultCache;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Model object for passing multiple parameters
	 */
	public static class Params
		{
		private final List<String> params;
		
		/**
		 * Constructor
		 */
		public Params()
			{
			this.params = new ArrayList<String>();
			}
		
		/**
		 * Get the params
		 * @return Params
		 */
		public List<String> getParams()
			{
			return (params);
			}
		}
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param queryTransformerService QueryTransformerService
	 * @param downloadService ResultDownloadService
	 * @param exportService ExportService
	 * @param frontendHelper FrontendHelperService
	 * @param extensionService FrontendExtensionService
	 * @param userSettings UserSettings
	 * @param resultCache ResultCache
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QueryController(QueryService queryService, QueryTransformerService queryTransformerService,
			ResultDownloadService downloadService, ExportService exportService, FrontendHelperService frontendHelper,
			FrontendExtensionService extensionService,
			UserSettings userSettings, ResultCache resultCache, ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.queryTransformerService = queryTransformerService;
		this.downloadService = downloadService;
		this.exportService = exportService;
		this.frontendHelper = frontendHelper;
		this.extensionService = extensionService;
		this.userSettings = userSettings;
		this.resultCache = resultCache;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the index page
	 * @param term Optional search term
	 * @param group Selected group
	 * @return Model
	 * @throws RedirectException if a welcome query is defined
	 */
	@RequestMapping(value = "/db/*/index.html", method = RequestMethod.GET)
	public Map<String, Object> showIndexPage(
			@RequestParam(value = "q", required = false) String term,
			@RequestParam(value = "group", required = false) String group
			) throws RedirectException
		{
		if (!connectionSettings.isEditorActive())
			{
			final String welcomeQueryName = queryService.getSchemaAttributes(connectionSettings.getLinkName()).get(MessageKeys.WELCOME_QUERY);
			if (welcomeQueryName != null)
				throw new RedirectException("result.html?q=" + welcomeQueryName);
			}
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, TabItem<QueryGroup>> groups = new LinkedHashMap<String, TabItem<QueryGroup>>();
		
		if (term != null)
			connectionSettings.setSearch(term);
		if (!StringUtils.empty(group))
			connectionSettings.setQueryGroup(group);
		
		if (!StringUtils.empty(connectionSettings.getSearch()))
			{
			final QueryGroup all = queryService.groupMatchingQueries(connectionSettings.getLinkName(), connectionSettings.getSearch(), connectionSettings.isEditorActive());
			groups.put(MessageKeys.RESULT_TAB, new TabItem<QueryGroup>(all));
			}
		
		final SortedMap<String, QueryGroup> all = queryTransformerService.splitQueryGroup(
			queryService.groupAllQueries(connectionSettings.getLinkName(), false, true),
			new UnaryFunction<Query, String>()
				{
				@Override
				public String applyTo(Query i)
					{
					return (i.getGroupName());
					}
				}
			);
		
		for (ExtensionGroup e : extensionService.getQueryOverviewExtensions())
			{
			if ((e.getQueries() != null) && !e.getQueries().isEmpty())
				groups.put(e.getLabel(), new TabItem<QueryGroup>(e.getQueries()));
			}
		
/*		final QueryGroup fav = queryService.groupQueries(connectionSettings.getConnection(), connectionSettings.getFavorites(), false, true);
		if (!fav.isEmpty())
			groups.put(MessageKeys.BOOKMARK_TAB, new TabItem<QueryGroup>(fav));*/
		
		if (all.isEmpty())
			{
			if (groups.isEmpty())
				groups.put(MessageKeys.EMPTY_TAB, new TabItem<QueryGroup>(new QueryGroup()));
			}
		else
			{
			final QueryGroup noGroupName = all.remove("");
			
			for (Map.Entry<String, QueryGroup> ent : all.entrySet())
				groups.put(ent.getKey(), new TabItem<QueryGroup>(ent.getValue(), -1, ent.getKey()));
			
			if (noGroupName != null)
				groups.put(MessageKeys.EMPTY_TAB, new TabItem<QueryGroup>(noGroupName));
			}
		
		model.put("groups", groups);
		
		// Validate query group
		if (!groups.containsKey(connectionSettings.getQueryGroup()))
			connectionSettings.setQueryGroup(null);
		
		// Select a group
		if (!StringUtils.empty(group) && (connectionSettings.getQueryGroup() != null))
			model.put("selectedGroup", connectionSettings.getQueryGroup());
		else if (!StringUtils.empty(connectionSettings.getSearch()))
			model.put("selectedGroup", MessageKeys.RESULT_TAB);
		else
			model.put("selectedGroup", connectionSettings.getQueryGroup());
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @param term Search term
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/search-result.html", method = RequestMethod.GET)
	public Map<String, Object> showSearchResult(
			@RequestParam(value = "q") String term
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		connectionSettings.setSearch(term);
		final QueryGroup all = queryService.groupMatchingQueries(connectionSettings.getLinkName(), connectionSettings.getSearch(), connectionSettings.isEditorActive());
		model.put("result", all);
		
		return (model);
	}
	
	/**
	 * Display the query menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/views.html", method = RequestMethod.GET)
	public Map<String, Object> showViews()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupAllQueries(connectionSettings.getLinkName(), false, true);
		
		final Map<String, String> queries = new TreeMap<String, String>();
		for (Query q : all.getViews())
			queries.put(q.getName(), q.getName());
		model.put("queries", queries);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/lists.html", method = RequestMethod.GET)
	public Map<String, Object> showLists()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupAllQueries(connectionSettings.getLinkName(), false, true);
		
		final Map<String, String> queries = new TreeMap<String, String>();
		for (Query q : all.getLists())
			queries.put(q.getName(), q.getName());
		model.put("queries", queries);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/queries.html", method = RequestMethod.GET)
	public Map<String, Object> showQueries()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupAllQueries(connectionSettings.getLinkName(), false, true);
		
		final Map<String, String> queries = new TreeMap<String, String>();
		for (Query q : all.getQueries())
			queries.put(q.getName(), q.getName());
		model.put("queries", queries);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/actions.html", method = RequestMethod.GET)
	public Map<String, Object> showActions()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupAllQueries(connectionSettings.getLinkName(), false, true);
		
		final Map<String, String> queries = new TreeMap<String, String>();
		for (Query q : all.getActions())
			queries.put(q.getName(), q.getName());
		model.put("queries", queries);
		
		return (model);
		}
	
	/**
	 * Display the history menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/history.html", method = RequestMethod.GET)
	public Map<String, Object> showHistory()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> history = new LinkedHashMap<String, String>();
		for (QueryHistoryEntry ent : connectionSettings.getQueryHistory())
			history.put(frontendHelper.getQueryTitle(ent.getQueryName(), ent.getParams()), ent.getQueryName() + frontendHelper.getQueryParams(ent.getParams(), false));
		model.put("history", history);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @param query Query name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/sql.html", method = RequestMethod.GET)
	public Map<String, Object> showSQL(
			@RequestParam("q") String query
			)
		{
		if (!userSettings.isSqlDisplayEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		if (q != null)
			model.put("query", q);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param query Query name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/queryhistory.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryHistory(
			@RequestParam("q") String query
			)
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("query", query);
		model.put("history", queryService.getHistory(connectionSettings.getLinkName(), query, 10));
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @param query Query name
	 * @param version Query version
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/queryversion.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryVersion(
			@RequestParam("q") String query,
			@RequestParam("version") String version
			)
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Query q = queryService.getQueryVersion(connectionSettings.getLinkName(), query, version);
		if (q != null)
			model.put("query", q);
		
		return (model);
		}
	
	/**
	 * Run a query
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/remove-query.html", method = RequestMethod.GET)
	public String removeQuery(
			@RequestParam("q") String query
			)
		{
		if (connectionSettings.isEditorEnabled())
			queryService.removeQuery(connectionSettings.getLinkName(), userSettings.getPrincipal().getLogin(), query);
		
		return ("redirect:index.html");
		}
	
	/**
	 * Reload queries
	 * @return View
	 */
	@RequestMapping(value = "/db/*/reload-queries.html", method = RequestMethod.GET)
	public String reload()
		{
		if (connectionSettings.isEditorEnabled())
			queryService.reloadQueries();
		
		return ("redirect:index.html");
		}
	
	/**
	 * Display the editor menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/edit.html", method = RequestMethod.GET)
	public Map<String, Object> showEditMenu()
		{
		final List<ExtensionLink> ext = extensionService.getEditMenuExtensions();
		
		if (!connectionSettings.isEditorEnabled() && !connectionSettings.isSubmitEnabled() && ext.isEmpty())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("extensions", ext);
		
		return (model);
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/types.html", method = RequestMethod.GET)
	public Map<String, Object> showTypes()
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SortedMap<String, QueryGroup> all = queryTransformerService.splitQueryGroup(
			queryService.groupAllQueries(connectionSettings.getLinkName(), false, true),
			new UnaryFunction<Query, String>()
				{
				@Override
				public String applyTo(Query i)
					{
					return ("$" + i.getType().getName());
					}
				}
			);
		
		final Map<String, TabItem<QueryGroup>> groups = new TreeMap<String, TabItem<QueryGroup>>();
		for (Map.Entry<String, QueryGroup> ent : all.entrySet())
			groups.put(ent.getKey(), new TabItem<QueryGroup>(ent.getValue(), -1, ent.getKey()));
		
		model.put("types", groups);
		
		return (model);
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/parameters.html", method = RequestMethod.GET)
	public Map<String, Object> showParameters()
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SortedMap<String, QueryGroup> all = queryTransformerService.splitQueryGroupMulti(
			queryService.groupAllQueries(connectionSettings.getLinkName(), false, true),
			new UnaryFunction<Query, Set<String>>()
				{
				@Override
				public Set<String> applyTo(Query i)
					{
					if (i.getParameters().isEmpty())
						return (null);
					
					final Set<String> keys = new HashSet<String>(i.getParameters().size());
					for (ParameterDef p : i.getParameters())
						keys.add(p.getName());
					return (keys);
					}
				}
			);
		
		final Map<String, TabItem<QueryGroup>> groups = new TreeMap<String, TabItem<QueryGroup>>();
		for (Map.Entry<String, QueryGroup> ent : all.entrySet())
			groups.put(ent.getKey(), new TabItem<QueryGroup>(ent.getValue(), -1, ent.getKey()));
		
		model.put("params", groups);
		
		return (model);
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/check.html", method = RequestMethod.GET)
	public Map<String, Object> checkQueries()
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, List<QueryCheckResult>> result = queryService.checkQueries(connectionSettings.getLinkName());
		
		model.put("errors", result);
		
		return (model);
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/subschema.html", method = RequestMethod.GET)
	public Map<String, Object> showConnectionSpecifics()
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupAllQueries(connectionSettings.getLinkName(), false, true);
		
		queryTransformerService.filterQueryGroup(all, new Predicate<Query>()
			{
			@Override
			public boolean evaluate(Query o)
				{
				return (o.getSourceSchema().isSubschema());
				}
			});
		
		model.put("p", all);
		
		return (model);
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dialect.html", method = RequestMethod.GET)
	public Map<String, Object> showDialectSpecifics()
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SortedMap<String, QueryGroup> all = queryTransformerService.splitQueryGroup(
			queryService.groupAllQueries(connectionSettings.getLinkName(), false, true),
			new UnaryFunction<Query, String>()
				{
				@Override
				public String applyTo(Query i)
					{
					return (i.getSourceSchema().isQuerySet() ? i.getSourceSchema().getVersion() : null);
					}
				}
			);
		
		final Map<String, TabItem<QueryGroup>> groups = new LinkedHashMap<String, TabItem<QueryGroup>>();
		
		if (all.isEmpty())
			groups.put(MessageKeys.EMPTY_TAB, new TabItem<QueryGroup>(new QueryGroup()));
		else
			{
			final QueryGroup noGroupName = all.remove("");
			
			for (Map.Entry<String, QueryGroup> ent : all.entrySet())
				groups.put(ent.getKey(), new TabItem<QueryGroup>(ent.getValue(), -1, ent.getKey()));
			
			if (noGroupName != null)
				groups.put(MessageKeys.EMPTY_TAB, new TabItem<QueryGroup>(noGroupName));
			}
		
		model.put("groups", groups);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @param group Group name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/group.html", method = RequestMethod.GET)
	public Map<String, Object> showGroup(
			@RequestParam("q") String group
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("groupName", group);
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @param param Parameter name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/param.html", method = RequestMethod.GET)
	public Map<String, Object> showParameter(
			@RequestParam("q") String param
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("paramName", param);
		
		return (model);
		}
	
	/**
	 * Rename a group
	 * @param from Group name
	 * @param to New group name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/rename-group.html", method = RequestMethod.POST)
	public String renameGroup(
			@RequestParam("from") String from,
			@RequestParam("to") String to
			)
		{
		if (connectionSettings.isEditorEnabled())
			queryService.renameGroup(connectionSettings.getLinkName(), userSettings.getPrincipal().getLogin(), from, to);
		
		return ("redirect:index.html");
		}
	
	/**
	 * Rename a parameter
	 * @param from Parameter name
	 * @param to New parameter name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/rename-param.html", method = RequestMethod.POST)
	public String renameParameter(
			@RequestParam("from") String from,
			@RequestParam("to") String to
			)
		{
		if (connectionSettings.isEditorEnabled())
			queryService.renameParameter(connectionSettings.getLinkName(), userSettings.getPrincipal().getLogin(), from, to);
		
		return ("redirect:parameters.html");
		}
	
	/**
	 * Show the search form
	 * @param term Search term
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/search.html", method = RequestMethod.GET)
	public Map<String, Object> showSearchForm(
			@RequestParam(value = "q", required = false) String term
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (term != null)
			{
			connectionSettings.setSearch(term);
			final QueryGroup all = queryService.groupMatchingQueries(connectionSettings.getLinkName(), connectionSettings.getSearch(), connectionSettings.isEditorActive());
			model.put("result", all);
			model.put("term", term);
			}
		
		return (model);
		}
	
	/**
	 * Get all possible parameter value queries
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-valuequery.html", method = RequestMethod.GET)
	public Map<String, Object> getValueQueries(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allQueries", queryService.groupAllQueries(connectionSettings.getLinkName(), true, false));
		
		return (model);
		}
	
	/**
	 * Get all possible parameter value queries
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-linkquery.html", method = RequestMethod.GET)
	public Map<String, Object> getLinkQueries(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allQueries", queryService.groupAllQueries(connectionSettings.getLinkName(), true, false));
		model.put("allParams", queryService.findSingleParameterNames(connectionSettings.getLinkName()));
		
		return (model);
		}
	
	/**
	 * Get all possible parameter value queries
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-viewquery.html", method = RequestMethod.GET)
	public Map<String, Object> getViewQueries(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allQueries", queryService.groupAllQueries(connectionSettings.getLinkName(), false, true));
		
		return (model);
		}
	
	/**
	 * Get all possible parameter names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-param.html", method = RequestMethod.GET)
	public Map<String, Object> getParameterNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allParams", queryService.findAllParameterNames(connectionSettings.getLinkName()));
		
		return (model);
		}
	
	/**
	 * Get all possible group names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-group.html", method = RequestMethod.GET)
	public Map<String, Object> getGroupNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allGroups", queryService.findAllGroupNames(connectionSettings.getLinkName()));
		
		return (model);
		}
	
	/**
	 * Find queries that accept a given set of parameters
	 * @param params Parameter names
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/match-views.html", method = RequestMethod.GET)
	public Map<String, Object> getMatchingQueries(@ModelAttribute Params params)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final List<Query> queries;
		if (!params.getParams().isEmpty())
			queries = queryService.findQueriesByParameters(connectionSettings.getLinkName(), params.getParams(), false);
		else
			queries = Collections.emptyList();
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new JsonDownloadSource(queries));
		
		return (model);
		}
	
	/**
	 * Display the query menu
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/timechart-reset.html", method = RequestMethod.GET)
	public String resetTimechart()
		{
		resultCache.clearCachedObjects(CacheClass.RESULT);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Show WS download links
	 * @param query Query name
	 * @param params Parameter values
	 * @param subQueryIndex Subquery index
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/querylinks.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryLinks(
			@RequestParam("q") String query,
			@ModelAttribute Params params,
			@RequestParam("index") Integer subQueryIndex
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("queryName", query);
		model.put("params", params.getParams());
		model.put("index", subQueryIndex);
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Show WS download links
	 * @param query Query name
	 * @param params Parameter values
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/resultlinks.html", method = RequestMethod.GET)
	public Map<String, Object> showResultLinks(
			@RequestParam("q") String query,
			@ModelAttribute Params params
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("queryName", query);
		model.put("params", params.getParams());
		model.put("exportFormats", exportService.getSupportedExportFormats());
		
		return (model);
		}
	}
