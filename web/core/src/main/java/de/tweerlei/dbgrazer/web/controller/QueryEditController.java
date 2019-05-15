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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ParameterTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.visualization.service.ChartScaling;
import de.tweerlei.dbgrazer.visualization.service.ChartType;
import de.tweerlei.dbgrazer.visualization.service.GraphType;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.exception.QueryNotFoundException;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.service.StringTransformerService;

/**
 * Controller for editing queries
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryEditController
	{
	private static final int MAX_PARAMS = 10;
	private static final int MAX_LINKS = 15;
	private static final int MAX_VIEWS = 15;
	
	private static final String PARAM_MARKER = "*";
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class ParameterFBO
		{
		private String name;
		private ColumnType type;
		private String valueQuery;
		
		/**
		 * Constructor
		 */
		public ParameterFBO()
			{
			}
		
		/**
		 * Constructor
		 * @param p ParameterDef
		 */
		public ParameterFBO(ParameterDef p)
			{
			this.name = p.getName();
			this.type = p.getType();
			this.valueQuery = p.getValueQuery();
			}
		
		/**
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}
		
		/**
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}
		
		/**
		 * @return the type
		 */
		public ColumnType getType()
			{
			return type;
			}
		
		/**
		 * @param type the type to set
		 */
		public void setType(ColumnType type)
			{
			this.type = type;
			}
		
		/**
		 * @return the valueQuery
		 */
		public String getValueQuery()
			{
			return valueQuery;
			}
		
		/**
		 * @param valueQuery the valueQuery to set
		 */
		public void setValueQuery(String valueQuery)
			{
			this.valueQuery = valueQuery;
			}
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class SubQueryFBO
		{
		private String name;
		private String parameter;
		
		/**
		 * Constructor
		 */
		public SubQueryFBO()
			{
			}
		
		/**
		 * Constructor
		 * @param s SubQueryDef
		 */
		public SubQueryFBO(SubQueryDef s)
			{
			this.name = s.getName();
			this.parameter = s.getParameterValues().isEmpty() ? null : s.getParameterValues().get(0);
			}
		
		/**
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}
		
		/**
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}
		
		/**
		 * @return the parameter
		 */
		public String getParameter()
			{
			return parameter;
			}
		
		/**
		 * @param parameter the parameter to set
		 */
		public void setParameter(String parameter)
			{
			this.parameter = parameter;
			}
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String originalName;
		private String backTo;
		private String name;
		private String scope;
		private String groupName;
		private String type;
		private boolean viewType;
		private final Map<String, String> attributes;
		private final Map<Integer, ParameterFBO> params;
		private final Map<Integer, String> links;
		private final Map<Integer, SubQueryFBO> views;
		private String statement;
		private QueryType resultType;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.attributes = new TreeMap<String, String>();
			this.params = new TreeMap<Integer, ParameterFBO>();
			this.links = new TreeMap<Integer, String>();
			this.views = new TreeMap<Integer, SubQueryFBO>();
			}
		
		/**
		 * Get the name
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}

		/**
		 * Set the name
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}

		/**
		 * Get the scope
		 * @return the scope
		 */
		public String getScope()
			{
			return scope;
			}

		/**
		 * Set the scope
		 * @param scope the scope to set
		 */
		public void setScope(String scope)
			{
			this.scope = scope;
			}

		/**
		 * Get the attributes
		 * @return the attributes
		 */
		public Map<String, String> getAttributes()
			{
			return attributes;
			}

		/**
		 * Get the params
		 * @return the params
		 */
		public Map<Integer, ParameterFBO> getParams()
			{
			return params;
			}

		/**
		 * Get the links
		 * @return the links
		 */
		public Map<Integer, String> getLinks()
			{
			return links;
			}

		/**
		 * Get the views
		 * @return the views
		 */
		public Map<Integer, SubQueryFBO> getViews()
			{
			return views;
			}

		/**
		 * Get the statement
		 * @return the statement
		 */
		public String getStatement()
			{
			return statement;
			}

		/**
		 * Set the statement
		 * @param statement the statement to set
		 */
		public void setStatement(String statement)
			{
			this.statement = statement;
			}

		/**
		 * Get the type
		 * @return the type
		 */
		public String getType()
			{
			return type;
			}

		/**
		 * Set the type
		 * @param type the type to set
		 */
		public void setType(String type)
			{
			this.type = type;
			}

		/**
		 * Get the backTo
		 * @return the backTo
		 */
		public String getBackTo()
			{
			return backTo;
			}

		/**
		 * Set the backTo
		 * @param backTo the backTo to set
		 */
		public void setBackTo(String backTo)
			{
			this.backTo = backTo;
			}

		/**
		 * Get the groupName
		 * @return the groupName
		 */
		public String getGroupName()
			{
			return groupName;
			}

		/**
		 * Set the groupName
		 * @param groupName the groupName to set
		 */
		public void setGroupName(String groupName)
			{
			this.groupName = groupName;
			}

		/**
		 * Get the originalName
		 * @return the originalName
		 */
		public String getOriginalName()
			{
			return originalName;
			}

		/**
		 * Set the originalName
		 * @param originalName the originalName to set
		 */
		public void setOriginalName(String originalName)
			{
			this.originalName = originalName;
			}

		/**
		 * @return the viewType
		 */
		public boolean isViewType()
			{
			return viewType;
			}

		/**
		 * @param viewType the viewType to set
		 */
		public void setViewType(boolean viewType)
			{
			this.viewType = viewType;
			}

		/**
		 * Get the resultType
		 * @return the resultType
		 */
		public QueryType getResultType()
			{
			return resultType;
			}
		
		/**
		 * Set the resultType
		 * @param resultType the resultType to set
		 */
		public void setResultType(QueryType resultType)
			{
			this.resultType = resultType;
			}
		}
	
	private final QueryService queryService;
	private final TextTransformerService textFormatterService;
	private final VisualizationService visualizationService;
	private final StringTransformerService stringTransformerService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param textFormatterService TextFormatterService
	 * @param visualizationService VisualizationService
	 * @param stringTransformerService StringTransformerService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QueryEditController(QueryService queryService, TextTransformerService textFormatterService,
			VisualizationService visualizationService, StringTransformerService stringTransformerService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.textFormatterService = textFormatterService;
		this.visualizationService = visualizationService;
		this.stringTransformerService = stringTransformerService;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param query Query name
	 * @param template Template query name
	 * @param subquery Subquery name
	 * @return FormBackingObject
	 * @throws QueryNotFoundException if the query does not exist
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "template", required = false) String template,
			@RequestParam(value = "subquery", required = false) String subquery
			) throws QueryNotFoundException
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final FormBackingObject ret = new FormBackingObject();
		
		final boolean creating = StringUtils.empty(query);
		
		final String queryName = creating ? template : query;
		
		if (queryName != null)
			{
			final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), queryName);
			if (q == null)
				throw new QueryNotFoundException(queryName);
			
			connectionSettings.setQueryGroup(q.getGroupName());
			
			ret.setName(creating ? "" : q.getName());
			ret.setOriginalName(creating ? "" : q.getName());
			ret.setScope(q.getSourceSchema().toString());
			ret.setGroupName(q.getGroupName());
			ret.setType(q.getType().getName());
			ret.setViewType(q.getType().getResultType().isView());
			ret.setStatement(q.getStatement());
			for (Map.Entry<Integer, TargetDef> ent : q.getTargetQueries().entrySet())
				{
				if (ent.getValue().isParameter())
					ret.getLinks().put(ent.getKey(), PARAM_MARKER + ent.getValue().getParameterName());
				else
					ret.getLinks().put(ent.getKey(), ent.getValue().getQueryName());
				}
			
			int i = 0;
			for (SubQueryDef s : q.getSubQueries())
				ret.getViews().put(i++, new SubQueryFBO(s));
			
			i = 0;
			for (ParameterDef p : q.getParameters())
				ret.getParams().put(i++, new ParameterFBO(p));
			
			ret.getAttributes().putAll(q.getAttributes());
			}
		else if (subquery != null)
			{
			ret.setName("");
			ret.setScope(getDefaultSchemaName());
			ret.setOriginalName("");
			ret.setGroupName(connectionSettings.getQueryGroup());
			ret.setType(queryService.findViewQueryTypes().iterator().next().getName());
			ret.setViewType(true);
			ret.setStatement("");
			
			final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), subquery);
			if (q != null)
				{
				ret.setScope(q.getSourceSchema().toString());
				ret.setGroupName(q.getGroupName());
				
				int i = 0;
				for (ParameterDef p : q.getParameters())
					ret.getParams().put(i++, new ParameterFBO(p));
				
				final SubQueryFBO sq = new SubQueryFBO();
				sq.setName(subquery);
				
				ret.getViews().put(0, sq);
				}
			}
		else
			{
			ret.setName("");
			ret.setScope(getDefaultSchemaName());
			ret.setOriginalName("");
			ret.setGroupName(connectionSettings.getQueryGroup());
			ret.setType("");
			ret.setViewType(false);
			ret.setStatement("");
			}
		
		for (int i = 0; i < MAX_PARAMS; i++)
			{
			if (!ret.getParams().containsKey(i))
				{
				final ParameterFBO p = new ParameterFBO();
				p.setName("");
				p.setType(ColumnType.INTEGER);
				p.setValueQuery("");
				ret.getParams().put(i, p);
				}
			}
		
		for (int i = 0; i < MAX_LINKS; i++)
			{
			if (!ret.getLinks().containsKey(i))
				ret.getLinks().put(i, "");
			}
		
		for (int i = 0; i < MAX_VIEWS; i++)
			{
			if (!ret.getViews().containsKey(i))
				{
				final SubQueryFBO p = new SubQueryFBO();
				p.setName("");
				p.setParameter("");
				ret.getViews().put(i, p);
				}
			}
		
		return (ret);
		}
	
	private String getDefaultSchemaName()
		{
		final Set<SchemaDef> schemas = queryService.getPossibleSchemaNames(connectionSettings.getLinkName());
		if (schemas.isEmpty())
			return ("");
		
		return (schemas.iterator().next().toString());
		}
	
	/**
	 * Get all referencing queries
	 * @param query Query name
	 * @return Queries
	 */
	@ModelAttribute("referencing")
	public List<Query> getReferencingQueries(
			@RequestParam(value = "q", required = false) String query
			)
		{
		if (StringUtils.empty(query))
			return (Collections.emptyList());
		
		return (queryService.findReferencingQueries(connectionSettings.getLinkName(), query));
		}
	
	/**
	 * Get all resultTypes
	 * @return resultTypes
	 */
	@ModelAttribute("schemas")
	public Set<SchemaDef> getSchemas()
		{
		return (queryService.getPossibleSchemaNames(connectionSettings.getLinkName()));
		}
	
	/**
	 * Get all resultTypes
	 * @return resultTypes
	 */
	@ModelAttribute("resultTypes")
	public Set<QueryType> getResultTypes()
		{
		return (queryService.findAllQueryTypes(connectionSettings.getType()));
		}
	
	/**
	 * Get all supported QueryType attributes
	 * @return Map: Attribute name -> Attribute type
	 */
	@ModelAttribute("allAttributes")
	public Map<String, Class<?>> getAllAttributes()
		{
		final Map<String, Class<?>> ret = new TreeMap<String, Class<?>>();
		
		for (QueryType t : queryService.findAllQueryTypes(connectionSettings.getType()))
			ret.putAll(t.getSupportedAttributes());
		
		return (ret);
		}
	
	/**
	 * Get all columnTypes
	 * @return columnTypes
	 */
	@ModelAttribute("columnTypes")
	public List<ColumnType> getColumnTypes()
		{
		return (CollectionUtils.list(ColumnType.values()));
		}
	
	/**
	 * Get all columnTypes
	 * @return columnTypes
	 */
	@ModelAttribute("graphTypes")
	public Set<String> getGraphTypes()
		{
		return (visualizationService.getOptionValues(VisualizationSettings.GRAPH_QUERY_TYPE, GraphType.class.getSimpleName(), -1));
		}
	
	/**
	 * Get all columnTypes
	 * @return columnTypes
	 */
	@ModelAttribute("chartTypes")
	public Set<String> getChartTypes()
		{
		return (visualizationService.getOptionValues(VisualizationSettings.CHART_QUERY_TYPE, ChartType.class.getSimpleName(), -1));
		}
	
	/**
	 * Get all columnTypes
	 * @return columnTypes
	 */
	@ModelAttribute("chartScalings")
	public Set<String> getChartScalings()
		{
		return (visualizationService.getOptionValues(VisualizationSettings.CHART_QUERY_TYPE, ChartScaling.class.getSimpleName(), -1));
		}
	
	/**
	 * Get all formatters
	 * @return formatters
	 */
	@ModelAttribute("formatters")
	public List<String> getFormatters()
		{
		return (CollectionUtils.list(textFormatterService.getSupportedTextFormats()));
		}
	
	/**
	 * Get all extensionJS
	 * @return extensionJS
	 */
	@ModelAttribute("extensionJS")
	public String getExtensionJS()
		{
		return ("edit.js");
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/edit.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryType type = queryService.findQueryType(fbo.getType());
		fbo.setResultType(type);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param result BindingResult
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/edit.html", method = RequestMethod.POST)
	public String updateQuery(@ModelAttribute("model") FormBackingObject fbo, BindingResult result)
		{
		final boolean creating = StringUtils.empty(fbo.getOriginalName());
		if (creating)
			{
			final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getName());
			if (q != null)
				{
				fbo.setName(q.getName());
				result.reject(ErrorKeys.EXISTS);
				return ("db/edit");
				}
			}
		else
			{
			final Query qOld = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getOriginalName());
			final Query qNew = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getName());
			if ((qNew != null) && (qNew != qOld))
				{
				fbo.setName(qNew.getName());
				result.reject(ErrorKeys.EXISTS);
				return ("db/edit");
				}
			}
		
		final List<ParameterDef> params = new ArrayList<ParameterDef>();
		for (ParameterFBO p : fbo.getParams().values())
			{
			if (!StringUtils.empty(p.getName()))
				params.add(new ParameterDefImpl(p.getName(), p.getType(), p.getValueQuery()));
			}
		
		final Map<Integer, TargetDef> links = new TreeMap<Integer, TargetDef>();
		for (Map.Entry<Integer, String> ent : fbo.getLinks().entrySet())
			{
			if (!StringUtils.empty(ent.getValue()))
				{
				if (ent.getValue().startsWith(PARAM_MARKER))
					links.put(ent.getKey(), new ParameterTargetImpl(ent.getValue().substring(PARAM_MARKER.length())));
				else
					links.put(ent.getKey(), new QueryTargetImpl(ent.getValue()));
				}
			}
		
		final List<SubQueryDef> views = new ArrayList<SubQueryDef>();
		for (SubQueryFBO s : fbo.getViews().values())
			{
			if (!StringUtils.empty(s.getName()))
				{
				if (!StringUtils.empty(s.getParameter()))
					views.add(new SubQueryDefImpl(s.getName(), Collections.singletonList(s.getParameter())));
				else
					views.add(new SubQueryDefImpl(s.getName(), null));
				}
			}
		
		final Map<String, String> attributes = new TreeMap<String, String>();
		for (Map.Entry<String, String> ent : fbo.getAttributes().entrySet())
			{
			if (!StringUtils.empty(ent.getKey()) && !StringUtils.empty(ent.getValue()))
				attributes.put(ent.getKey(), StringUtils.notNull(ent.getValue()));
			}
		
		final QueryType type = queryService.findQueryType(fbo.getType());
		if (type == null)
			{
			result.reject(QueryErrorKeys.UNKNOWN_TYPE);
			return ("db/edit");
			}
		fbo.setResultType(type);
		
		final Query q;
		if (type.getResultType().isView())
			q = new ViewImpl(fbo.getName(), SchemaDef.valueOf(fbo.getScope()), fbo.getGroupName(), type, params, views, attributes);
		else
			q = new QueryImpl(fbo.getName(), SchemaDef.valueOf(fbo.getScope()), fbo.getGroupName(), fbo.getStatement(), type, params, links, attributes);
		
		try	{
			final String name;
			if (creating)
				{
				name = queryService.createQuery(connectionSettings.getLinkName(), userSettings.getPrincipal().getLogin(), q);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("db/edit");
					}
				}
			else
				{
				name = queryService.updateQuery(connectionSettings.getLinkName(), userSettings.getPrincipal().getLogin(), fbo.getOriginalName(), q);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("db/edit");
					}
				}
			
			if (!StringUtils.empty(fbo.getBackTo()))
				return ("redirect:run-query.html?q=" + stringTransformerService.toURL(fbo.getBackTo()));
			else
				return ("redirect:run-query.html?q=" + stringTransformerService.toURL(name));
			}
		catch (BindException e)
			{
			result.addAllErrors(e);
			return ("db/edit");
			}
		}
	}
