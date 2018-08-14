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
package de.tweerlei.dbgrazer.web.extension.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.web.extension.ExtensionGroup;
import de.tweerlei.dbgrazer.web.extension.ExtensionLink;
import de.tweerlei.dbgrazer.web.extension.FrontendExtension;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.spring.util.OrderedSet;

/**
 * Manage frontend extensions
 * 
 * @author Robert Wruck
 */
@Service
public class FrontendExtensionServiceImpl implements FrontendExtensionService
	{
	private final Logger logger;
	private final Set<FrontendExtension> extensions;
	
	/**
	 * Constructor
	 * @param extensions FrontendExtensions
	 */
	@Autowired(required = false)
	public FrontendExtensionServiceImpl(Set<FrontendExtension> extensions)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.extensions = Collections.unmodifiableSet(new OrderedSet<FrontendExtension>(extensions));
		
		this.logger.log(Level.INFO, "Frontend extensions: " + this.extensions);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 */
	public FrontendExtensionServiceImpl()
		{
		this(Collections.<FrontendExtension>emptySet());
		}
	
	@Override
	public List<ExtensionLink> getTopMenuExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getTopMenuExtensions());
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getEditMenuExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getEditMenuExtensions());
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getAdminMenuExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getAdminMenuExtensions());
		
		return (ret);
		}
	
	@Override
	public List<ExtensionGroup> getQueryOverviewExtensions()
		{
		final List<ExtensionGroup> ret = new ArrayList<ExtensionGroup>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getQueryOverviewExtensions());
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getQueryViewExtensions(Query query)
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getQueryViewExtensions(query));
		
		return (ret);
		}
	
	@Override
	public List<String> getQueryViewJS(Query query)
		{
		final List<String> ret = new ArrayList<String>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getQueryViewJS(query));
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getLinkOverviewExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getLinkOverviewExtensions());
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getLinkViewExtensions(String linkName)
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getLinkViewExtensions(linkName));
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getRestApiExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		for (FrontendExtension ext : extensions)
			ret.addAll(ext.getRestApiExtensions());
		
		return (ret);
		}
	}
