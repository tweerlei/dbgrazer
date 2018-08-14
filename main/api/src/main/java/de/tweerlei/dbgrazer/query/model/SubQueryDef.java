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
package de.tweerlei.dbgrazer.query.model;

import java.util.List;

/**
 * Referenced query definition
 * 
 * @author Robert Wruck
 */
public interface SubQueryDef
	{
	/**
	 * Get the referenced query name
	 * @return Query name
	 */
	public String getName();
	
	/**
	 * Get the fixed parameter values
	 * @return Parameter values
	 */
	public List<String> getParameterValues();
	}
