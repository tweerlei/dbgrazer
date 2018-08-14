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

import java.io.Serializable;


/**
 * Definition of a query parameter
 * 
 * @author Robert Wruck
 */
public interface ParameterDef extends Serializable
	{
	/**
	 * Get the name
	 * @return the name
	 */
	public String getName();

	/**
	 * Get the type
	 * @return the type
	 */
	public ColumnType getType();

	/**
	 * Get the valueQuery
	 * @return the valueQuery
	 */
	public String getValueQuery();
	}
