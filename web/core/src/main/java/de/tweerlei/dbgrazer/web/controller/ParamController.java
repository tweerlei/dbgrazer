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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class ParamController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String p;
		private String target;
		private final Map<Integer, String> params;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.params = new TreeMap<Integer, String>();
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
		 * Get the p
		 * @return the p
		 */
		public String getParamName()
			{
			return p;
			}

		/**
		 * Set the p
		 * @param p the p to set
		 */
		public void setParamName(String p)
			{
			this.p = p;
			}

		/**
		 * @return the target
		 */
		public String getTarget()
			{
			return target;
			}

		/**
		 * @param target the target to set
		 */
		public void setTarget(String target)
			{
			this.target = target;
			}
		}
	
	private final QueryService queryService;
	private final FrontendHelperService frontendHelper;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param frontendHelper FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public ParamController(QueryService queryService, FrontendHelperService frontendHelper,
			ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.frontendHelper = frontendHelper;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param param Parameter name
	 * @param target Target DOM element
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam("p") String param,
			@RequestParam(value = "target", required = false) String target
			)
		{
		final FormBackingObject ret = new FormBackingObject();
		ret.setParamName(param);
		ret.setTarget(target);
		
		return (ret);
		}
	
	/**
	 * Display the target menu
	 * @param fbo Form backing object
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/targets.html", method = RequestMethod.GET)
	public Map<String, Object> showTargets(@ModelAttribute("model") FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryGroup all = queryService.groupQueriesByParameter(connectionSettings.getLinkName(), fbo.getParamName());
		
		final String param = frontendHelper.paramEncode(fbo.getParams().get(0), false);
		
		final Map<String, String> tmp = new TreeMap<String, String>();
		for (Query q : all.getViews())
			tmp.put(q.getName(), q.getName() + param);
		for (Query q : all.getQueries())
			tmp.put(q.getName(), q.getName() + param);
		
		model.put("targets", tmp);
		model.put("params", fbo.getParams().get(0));
		model.put("targetElement", fbo.getTarget());
		
		return (model);
		}
	}
