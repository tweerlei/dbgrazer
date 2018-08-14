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
package de.tweerlei.dbgrazer.web.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;

/**
 * Handle special URLs
 * 
 * @author Robert Wruck
 */
public class ViewNameResolver extends DefaultRequestToViewNameTranslator
	{
	private final FrontendHelperService frontendHelper;
	
	/**
	 * Constructor
	 * @param frontendHelper FrontendHelperService
	 */
	@Autowired
	public ViewNameResolver(FrontendHelperService frontendHelper)
		{
		this.frontendHelper = frontendHelper;
		}
	
	@Override
	public String getViewName(HttpServletRequest request)
		{
		final String view = super.getViewName(request);
		final PathInfo pi = frontendHelper.parsePath(view, null);
		return (pi.getViewName());
		}
	}
