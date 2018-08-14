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
package de.tweerlei.spring.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import de.tweerlei.common.contract.ContractProof;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Implementation of ModuleLookupService
 * 
 * @author Robert Wruck
 */
@Service("moduleLookupService")
public class ModuleLookupServiceImpl implements ModuleLookupService
	{
	private final ApplicationContext context;
	
	/**
	 * Constructor
	 * @param context ApplicationContext
	 */
	@Autowired
	public ModuleLookupServiceImpl(ApplicationContext context)
		{
		this.context = context;
		}
	
	public <T> T findModuleInstance(String m, Class<T> base)
		{
		ContractProof.notNull("module", m);
		ContractProof.notNull("baseClass", base);
		
		final Object instance = context.getBean(m, base);
		return (base.cast(instance));
		}
	
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> findModuleInstances(Class<T> base)
		{
		ContractProof.notNull("baseClass", base);
		
		return (context.getBeansOfType(base));
		}
	
	public <T> T findModuleInstance(Class<T> base)
		{
		ContractProof.notNull("baseClass", base);
		
		final Map<String, T> modules = findModuleInstances(base);
		
		if (modules.size() != 1)
			throw new RuntimeException("Expected a single instance of " + base + " but got " + modules.keySet());
		
		return (modules.values().iterator().next());
		}
	}
