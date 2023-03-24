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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.AggregateColumn;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.AggregationMode;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.QueryException;
import de.tweerlei.dbgrazer.web.exception.QueryNotFoundException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class AggregateQueryRunController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private Query query;
		private final Map<Integer, String> params;
		private final Map<Integer, String> columns;
		private final Map<Integer, AggregationMode> funcs;
		private final Map<Integer, String> exprs;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.params = new TreeMap<Integer, String>();
			this.columns = new TreeMap<Integer, String>();
			this.funcs = new TreeMap<Integer, AggregationMode>();
			this.exprs = new TreeMap<Integer, String>();
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
		public Map<Integer, String> getColumns()
			{
			return columns;
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, AggregationMode> getFuncs()
			{
			return funcs;
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getExprs()
			{
			return exprs;
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
		}
	
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final SQLGeneratorService sqlGenerator;
	private final DataFormatterFactory factory;
	private final ResultDownloadService downloadService;
	private final ResultTransformerService resultTransformer;
	private final QuerySettingsManager querySettingsManager;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param sqlGenerator SQLGeneratorService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param downloadService ResultDownloadService
	 * @param resultTransformer ResultTransformerService
	 * @param querySettingsManager QuerySettingsManager
	 */
	@Autowired
	public AggregateQueryRunController(QueryService queryService,
			QueryPerformerService runner, SQLGeneratorService sqlGenerator, ResultDownloadService downloadService,
			DataFormatterFactory factory, ResultTransformerService resultTransformer,
			QuerySettingsManager querySettingsManager,
			ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.runner = runner;
		this.sqlGenerator = sqlGenerator;
		this.factory = factory;
		this.downloadService = downloadService;
		this.resultTransformer = resultTransformer;
		this.querySettingsManager = querySettingsManager;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
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
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final FormBackingObject ret = new FormBackingObject();
		ret.setQuery(q);
		ret.getParams().putAll(querySettingsManager.getDefaultParameterValues(q, fmt));
		
		return (ret);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param subQueryIndex Subquery index
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/aggregate-export.html", method = RequestMethod.POST)
	public Map<String, Object> performAggregateSubQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("index") Integer subQueryIndex,
			@RequestParam("format") String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!fbo.getQuery().getType().isColumnPrefixed())
			{
			logger.log(Level.WARNING, "performAggregateSubQuery: Not an aggregate query: " + fbo.getQuery().getName());
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			return (model);
			}
		
		final Query agg = buildAggregateQuery(fbo);
		final QueryParameters query = querySettingsManager.prepareParameters(agg, fbo.getParams());
		
		final Result r = performQuery(connectionSettings.getLinkName(), query);
		
		RowSet rowSet = null;
		int i = 0;
		for (RowSet rs : r.getRowSets().values())
			{
			rowSet = rs;
			if (i++ == subQueryIndex)
				break;
			}
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rowSet, format));
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param subQueryIndex Subquery index
	 * @param formIndex Target form index
	 * @param target Target DOM element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/aggregate.html", method = RequestMethod.POST)
	public Map<String, Object> performAggregateQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("index") Integer subQueryIndex,
			@RequestParam("formIndex") String formIndex,
			@RequestParam(value = "target", required = false) String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!fbo.getQuery().getType().isColumnPrefixed())
			{
			logger.log(Level.WARNING, "performAggregateQuery: Not an aggregate query: " + fbo.getQuery().getName());
			return (model);
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final Query agg = buildAggregateQuery(fbo);
		final QueryParameters query = querySettingsManager.prepareParameters(agg, fbo.getParams());
		
		try	{
			final Result r = performQuery(connectionSettings.getLinkName(), query);
			
			RowSet rowSet = null;
			int i = 0;
			for (RowSet rs : r.getRowSets().values())
				{
				rowSet = rs;
				if (i++ == subQueryIndex)
					break;
				}
			
			resultTransformer.translateRowSet(rowSet, fmt);
			model.put("result", rowSet);
			model.put("label", formIndex);
			model.put("targetElement", target);
			}
		catch (QueryException e)
			{
			model.putAll(e.getModel());
			}
		
		return (model);
		}
	
	private Query buildAggregateQuery(FormBackingObject fbo)
		{
		final List<AggregateColumn> columns = new ArrayList<AggregateColumn>(fbo.getColumns().size());
		for (Map.Entry<Integer, String> s : fbo.getColumns().entrySet())
			{
			AggregationMode agg = fbo.getFuncs().get(s.getKey());
			if (agg == null)
				agg = AggregationMode.NONE;
			String expr = fbo.getExprs().get(s.getKey());
			columns.add(new AggregateColumn(s.getValue(), expr, agg));
			}
		
		final String q = sqlGenerator.createAggregateQuery(fbo.getQuery().getStatement(), columns);
		
		return (new QueryImpl(fbo.getQuery().getName(), new SchemaDef(null, null), null, q, null, fbo.getQuery().getType(), fbo.getQuery().getParameters(), fbo.getQuery().getTargetQueries(), fbo.getQuery().getAttributes()));
		}
	
	private Result performQuery(String connection, QueryParameters query)
		{
		try	{
			return (runner.performQuery(connection, query));
			}
		catch (PerformQueryException e)
			{
			throw new QueryException(e.getQueryName(), query.getQuery().getName(), e.getCause());
			}
		catch (RuntimeException e)
			{
			throw new QueryException(query.getQuery().getName(), null, e);
			}
		}
	}
