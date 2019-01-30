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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.QueryException;
import de.tweerlei.dbgrazer.web.exception.QueryNotFoundException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.ExportService;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultDiffService;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for comparing query results
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryDiffController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private Query query;
		private String connection2;
		private final Map<Integer, String> params;
		private final Map<Integer, String> params2;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.params = new TreeMap<Integer, String>();
			this.params2 = new TreeMap<Integer, String>();
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getParams()
			{
			return params;
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getParams2()
			{
			return params2;
			}
		
		/**
		 * Get the selected
		 * @return the selected
		 */
		public Query getQuery()
			{
			return query;
			}

		/**
		 * Set the selected
		 * @param query the selected to set
		 */
		public void setQuery(Query query)
			{
			this.query = query;
			}
		
		/**
		 * Get the connection2
		 * @return the connection2
		 */
		public String getConnection2()
			{
			return connection2;
			}
		
		/**
		 * Set the connection2
		 * @param connection2 the connection2 to set
		 */
		public void setConnection2(String connection2)
			{
			this.connection2 = connection2;
			}
		}
	
	private final LinkService linkService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final DataFormatterFactory factory;
	private final ResultDownloadService downloadService;
	private final ExportService exportService;
	private final ResultDiffService resultCompareService;
	private final FrontendHelperService frontendHelper;
	private final QuerySettingsManager querySettingsManager;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param factory DataFormatterFactory
	 * @param downloadService ResultDownloadService
	 * @param exportService ExportService
	 * @param resultCompareService ResultDiffService
	 * @param frontendHelper FrontendHelperService
	 * @param querySettingsManager QuerySettingsManager
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QueryDiffController(LinkService linkService, QueryService queryService,
			QueryPerformerService runner, DataFormatterFactory factory, ResultDownloadService downloadService,
			ExportService exportService, ResultDiffService resultCompareService,
			FrontendHelperService frontendHelper,
			QuerySettingsManager querySettingsManager, UserSettingsManager userSettingsManager,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.linkService = linkService;
		this.queryService = queryService;
		this.runner = runner;
		this.factory = factory;
		this.downloadService = downloadService;
		this.exportService = exportService;
		this.resultCompareService = resultCompareService;
		this.frontendHelper = frontendHelper;
		this.querySettingsManager = querySettingsManager;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param query Query name
	 * @return FormBackingObject
	 * @throws QueryNotFoundException if the query name is invalid
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam("q") String query
			) throws QueryNotFoundException
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		if (q == null)
			throw new QueryNotFoundException(query);
		
		connectionSettings.setQueryGroup(q.getGroupName());
		
		final FormBackingObject ret = new FormBackingObject();
		ret.setQuery(q);
		
		return (ret);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/compare.html", method = RequestMethod.GET)
	public Map<String, Object> showCompareForm(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		fbo.getParams2().putAll(fbo.getParams());
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), connectionSettings.getSchemaName(), null);
		final String selected = connectionSettings.getParameterHistory().get("connection2");
		
		final Map<Integer, String> values = new HashMap<Integer, String>();
		
		int i = 0 ;
		for (ParameterDef p : fbo.getQuery().getParameters())
			{
			if (!StringUtils.empty(p.getValueQuery()))
				values.put(i, p.getValueQuery());
			i++;
			}
		
		model.put("values", values);
		model.put("allConnections", all);
		if (selected != null && all.containsValue(selected))
			model.put("selectedConnection", selected);
		model.put("paramString", frontendHelper.getQueryParams(fbo.getParams(), true));
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/compare2.html", method = RequestMethod.GET)
	public Map<String, Object> showCompare2Form(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		fbo.getParams2().putAll(fbo.getParams());
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), connectionSettings.getSchemaName(), null);
		final String selected = connectionSettings.getParameterHistory().get("connection2");
		
		final Map<Integer, String> values = new HashMap<Integer, String>();
		
		int i = 0 ;
		for (ParameterDef p : fbo.getQuery().getParameters())
			{
			if (!StringUtils.empty(p.getValueQuery()))
				values.put(i, p.getValueQuery());
			i++;
			}
		
		model.put("values", values);
		model.put("allConnections", all);
		if (selected != null && all.containsValue(selected))
			model.put("selectedConnection", selected);
		model.put("paramString", frontendHelper.getQueryParams(fbo.getParams(), true));
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/compare.html", method = RequestMethod.GET)
	public Map<String, Object> performQuery(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final List<String> values = querySettingsManager.getEffectiveParameters(fbo.getQuery(), fbo.getParams());
		final List<Object> params = querySettingsManager.translateParameters(fbo.getQuery(), values, fmt);
		
		final Map<String, Result> results1 = performRecursiveQuery(connectionSettings.getLinkName(), fbo.getQuery(), fbo.getParams());
		final Map<String, Result> results2 = performRecursiveQuery(fbo.getConnection2(), fbo.getQuery(), fbo.getParams2());
		
		final Result r;
		try	{
			r = mergeResults(fbo.getQuery(), results1, results2, connectionSettings.getLinkName(), fbo.getConnection2(), fmt, true);
			}
		catch (RuntimeException e)
			{
			throw new QueryException(fbo.getQuery().getName(), null, e);
			}
		
		final Map<String, TabItem<RowSet>> rowSets = new LinkedHashMap<String, TabItem<RowSet>>();
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			final RowSet rs = ent.getValue();
			final Map<Integer, String> effectiveParams = (fbo.getQuery() == null) ? null : querySettingsManager.buildParameterMap(CollectionUtils.concat(values, rs.getParameterValues()));
			
			rowSets.put(ent.getKey(), new TabItem<RowSet>(
					rs,
					(rs.getSubQueryIndex() < 0) ? -1 : rs.getRows().size(),
					(rs.getQuery() == null) ? null : rs.getQuery().getName(),
					effectiveParams,
					(effectiveParams == null) ? null : frontendHelper.getQueryParams(effectiveParams, true)
					));
			}
		
		model.put("results", rowSets);
		model.put("title", frontendHelper.getQueryTitle(fbo.getQuery().getName(), params));
//		model.put("favorite", connectionSettings.getFavorites().contains(fbo.getQuery().getName()));
		model.put("isdiff", Boolean.TRUE);
		model.put("paramString", frontendHelper.getQueryParams(fbo.getParams(), true));
		model.put("paramString2", frontendHelper.getQueryParams(fbo.getParams2(), true));
		model.put("formats", exportService.getSupportedExportFormats());
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		
		final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(rowSets.size());
		for (TabItem<RowSet> rs : rowSets.values())
			{
			if ((rs.getPayload().getQuery() == null) || (rs.getPayload().getQuery().getType().getResultType() != ResultType.TABLE))
				tableColumns.add(null);
			else if (rs.getPayload().getQuery().getType().isColumnPrefixed())
				{
				// Aggregate results are shown with a leading COUNT column
				final List<ColumnDef> cols = new ArrayList<ColumnDef>(rs.getPayload().getColumns().size() + 1);
				cols.add(new ColumnDefImpl(null, ColumnType.INTEGER, null, null, null, null));
				cols.addAll(rs.getPayload().getColumns());
				tableColumns.add(cols);
				}
			else
				tableColumns.add(rs.getPayload().getColumns());
			}
		model.put("tableColumns", tableColumns);
		
		connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param subQueryIndex Subquery index
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/compare-export.html", method = RequestMethod.GET)
	public Map<String, Object> performSubQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("index") Integer subQueryIndex,
			@RequestParam("format") String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final Result r1 = performQuery(connectionSettings.getLinkName(), fbo.getQuery(), fbo.getParams());
		final Result r2 = performQuery(fbo.getConnection2(), fbo.getQuery(), fbo.getParams2());
		
		final RowSet rowSet = mergeResults(r1, r2, connectionSettings.getLinkName(), fbo.getConnection2(), fmt, subQueryIndex);
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rowSet, format));
		
		connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/compare-export-full.html", method = RequestMethod.GET)
	public Map<String, Object> performExportQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("format") String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final Map<String, Result> results1 = performRecursiveQuery(connectionSettings.getLinkName(), fbo.getQuery(), fbo.getParams());
		final Map<String, Result> results2 = performRecursiveQuery(fbo.getConnection2(), fbo.getQuery(), fbo.getParams2());
		
		final Result r = mergeResults(fbo.getQuery(), results1, results2, connectionSettings.getLinkName(), fbo.getConnection2(), fmt, false);
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, exportService.getExportDownloadSource(r, format));
		
		connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
		
		return (model);
		}
	
	private Result performQuery(String connection, Query query, Map<Integer, String> params)
		{
		try	{
			return (runner.performQuery(connection, query, params));
			}
		catch (PerformQueryException e)
			{
			throw new QueryException(e.getQueryName(), query.getName(), e.getCause());
			}
		catch (RuntimeException e)
			{
			throw new QueryException(query.getName(), null, e);
			}
		}
	
	private Map<String, Result> performRecursiveQuery(String connection, Query query, Map<Integer, String> params)
		{
		try	{
			return (runner.performRecursiveQuery(connection, query, params));
			}
		catch (PerformQueryException e)
			{
			throw new QueryException(e.getQueryName(), query.getName(), e.getCause());
			}
		catch (RuntimeException e)
			{
			throw new QueryException(query.getName(), null, e);
			}
		}
	
	private Result mergeResults(Query query, Map<String, Result> results1, Map<String, Result> results2, String c1, String c2, DataFormatter fmt, boolean format)
		{
		if (results1.size() != results2.size())
			throw new IllegalStateException("Result count mismatch: " + results1.size() + " vs. " + results2.size());
		
		final Result ret = new ResultImpl(query);
		
		final Iterator<Result> it1 = results1.values().iterator();
		final Iterator<Result> it2 = results2.values().iterator();
		
		while (it1.hasNext() && it2.hasNext())
			{
			final Result r1 = it1.next();
			final Result r2 = it2.next();
			mergeResults(ret, r1, r2, c1, c2, fmt, format, format && querySettingsManager.isTrimColumnsActive(r1.getQuery()));
			}
		
		if (ret.getRowSets().isEmpty())
			ret.getRowSets().put(query.getName(), new RowSetImpl(query, 0, null));
		
		return (ret);
		}
	
	private void mergeResults(Result ret, Result result1, Result result2, String c1, String c2, DataFormatter fmt, boolean format, boolean trim)
		{
		for (Map.Entry<String, RowSet> ent : result1.getRowSets().entrySet())
			{
			RowSet rs = result2.getRowSets().get(ent.getKey());
			if (rs == null)
				rs = new RowSetImpl(ent.getValue().getQuery(), ent.getValue().getSubQueryIndex(), ent.getValue().getColumns());
			addRowSet(ret, ent.getKey(), mergeRowSets(ent.getValue(), rs, c1, c2, fmt, format, trim));
			}
		
		for (Map.Entry<String, RowSet> ent : result2.getRowSets().entrySet())
			{
			RowSet rs = result1.getRowSets().get(ent.getKey());
			if (rs == null)
				{
				rs = new RowSetImpl(ent.getValue().getQuery(), ent.getValue().getSubQueryIndex(), ent.getValue().getColumns());
				// only add if not already found above
				addRowSet(ret, ent.getKey(), mergeRowSets(rs, ent.getValue(), c1, c2, fmt, format, trim));
				}
			}
		}
	
	private void addRowSet(Result ret, String name, RowSet rs)
		{
		if (rs.getRows().isEmpty())
			return;
		
		ret.getRowSets().put(name, rs);
		}
	
	private RowSet mergeRowSets(RowSet rs1, RowSet rs2, String c1, String c2, DataFormatter fmt, boolean format, boolean trim)
		{
		if (!rs1.getColumns().isEmpty() && !rs2.getColumns().isEmpty() && (rs1.getColumns().size() != rs2.getColumns().size()))
			throw new IllegalStateException("Column count mismatch: " + rs1.getQuery().getName() + ", " + rs1.getColumns().size() + " vs. " + rs2.getColumns().size());
		
		if (rs1.getQuery().getType().getResultType() == ResultType.SINGLE)
			return (resultCompareService.diffTextResults(rs1, rs2, c1, c2, fmt));
		else if (format)
			return (resultCompareService.diffResults(rs1, rs2, fmt, trim));
		else
			return (resultCompareService.diffResults(rs1, rs2, null, false));
		}
	
	private RowSet mergeResults(Result result1, Result result2, String c1, String c2, DataFormatter fmt, int subQueryIndex)
		{
		int i = 0;
		
		for (Map.Entry<String, RowSet> ent : result1.getRowSets().entrySet())
			{
			RowSet rs = result2.getRowSets().get(ent.getKey());
			if (i++ == subQueryIndex)
				{
				if (rs == null)
					rs = new RowSetImpl(ent.getValue().getQuery(), ent.getValue().getSubQueryIndex(), ent.getValue().getColumns());
				return (mergeRowSets(ent.getValue(), rs, c1, c2, fmt, false, false));
				}
			}
		
		for (Map.Entry<String, RowSet> ent : result2.getRowSets().entrySet())
			{
			RowSet rs = result1.getRowSets().get(ent.getKey());
			if (rs == null)
				{
				if (i++ == subQueryIndex)
					{
					rs = new RowSetImpl(ent.getValue().getQuery(), ent.getValue().getSubQueryIndex(), ent.getValue().getColumns());
					// only add if not already found above
					return (mergeRowSets(rs, ent.getValue(), c1, c2, fmt, false, false));
					}
				}
			}
		
		return (null);
		}
	}
