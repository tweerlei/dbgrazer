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
package de.tweerlei.dbgrazer.web.extension;

import java.util.Collections;
import java.util.List;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.Query;

/**
 * Frontend extension adapter
 * 
 * @author Robert Wruck
 */
public abstract class FrontendExtensionAdapter extends NamedBase implements FrontendExtension
	{
	/**
	 * Constructor
	 * @param name Name
	 */
	protected FrontendExtensionAdapter(String name)
		{
		super(name);
		}
	
	@Override
	public List<ExtensionLink> getTopMenuExtensions()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getEditMenuExtensions()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getAdminMenuExtensions()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionGroup> getQueryOverviewExtensions()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getQueryViewExtensions(Query query)
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<String> getQueryViewJS(Query query)
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getLinkOverviewExtensions()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getLinkViewExtensions(String linkName)
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ExtensionLink> getRestApiExtensions()
		{
		return (Collections.emptyList());
		}
	}
