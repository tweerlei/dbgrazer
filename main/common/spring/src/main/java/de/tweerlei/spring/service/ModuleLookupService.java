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
package de.tweerlei.spring.service;

import java.util.Map;

/**
 * Service for creating module instances
 * 
 * @author Robert Wruck
 */
public interface ModuleLookupService
	{
	/**
	 * Create a new instance of the given module
	 * @param <T> Module interface
	 * @param m Module name
	 * @param base Implementation class
	 * @return Module instance
	 */
	public <T> T findModuleInstance(String m, Class<T> base);
	
	/**
	 * Find all modules implementing a given interface
	 * @param <T> Interface
	 * @param base Interface class
	 * @return Map: module name -> module instance
	 */
	public <T> Map<String, T> findModuleInstances(Class<T> base);
	
	/**
	 * Get a single module instance
	 * @param <T> Interface
	 * @param base Interface class
	 * @return Module instance
	 * @throws RuntimeException if there is no unique match for the interface class
	 */
	public <T> T findModuleInstance(Class<T> base);
	}
