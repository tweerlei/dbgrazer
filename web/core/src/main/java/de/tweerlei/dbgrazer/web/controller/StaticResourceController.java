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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.web.constant.ConfigKeys;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.web.view.FileDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller that exposes static files (e.g. custom CSS)
 * 
 * @author Robert Wruck
 */
@Controller
public class StaticResourceController
	{
	private final ConfigAccessor configService;
	private final ConfigFileStore configFileStore;
	private final KeywordService keywordService;
	private final ServletContext servletContext;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param configFileStore ConfigFileStore
	 * @param keywordService KeywordService
	 * @param servletContext ServletContext
	 */
	@Autowired
	public StaticResourceController(ConfigAccessor configService, ConfigFileStore configFileStore,
			KeywordService keywordService, ServletContext servletContext)
		{
		this.configService = configService;
		this.configFileStore = configFileStore;
		this.keywordService = keywordService;
		this.servletContext = servletContext;
		}
	
	/**
	 * Serve static resources from the configured local path
	 * @param request HttpServletRequest
	 * @return Model
	 */
	@RequestMapping(value = "/static/*", method = RequestMethod.GET)
	public Map<String, Object> getFile(HttpServletRequest request)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final File dir = configFileStore.getFileLocation(configService.get(ConfigKeys.STATIC_RESOURCE_PATH));
		final String path = keywordService.normalizePath(request.getPathInfo());
		final File file = new File(dir, path);
		final String contentType = servletContext.getMimeType(path);
		
		final FileDownloadSource downloadSource = new FileDownloadSource(file, contentType);
		downloadSource.setExpireTime(configService.get(ConfigKeys.STATIC_RESOURCE_TTL));
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadSource);
		
		return (model);
		}
	}
