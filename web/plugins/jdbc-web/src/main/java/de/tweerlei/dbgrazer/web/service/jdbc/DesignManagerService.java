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
package de.tweerlei.dbgrazer.web.service.jdbc;

import java.util.SortedSet;

import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableSet;

/**
 * Manage saved table designs
 * 
 * @author Robert Wruck
 */
public interface DesignManagerService
	{
	/**
	 * Get the currently loaded design
	 * @return TableSet
	 */
	public TableSet getCurrentDesign();
	
	/**
	 * List available saved designs
	 * @return Desgin names
	 */
	public SortedSet<String> listAvailableDesigns();
	
	/**
	 * Load a saved design
	 * @param name Design name
	 */
	public void loadDesign(String name);
	
	/**
	 * Save the current design
	 * @param name Design name
	 */
	public void saveDesign(String name);
	
	/**
	 * Remove a saved design
	 * @param name Design name
	 */
	public void removeDesign(String name);
	}
