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
 * A RowSet returned by a database query
 * 
 * @author Robert Wruck
 */
public interface ResultRow extends RowContainer<ResultRow>
	{
	/**
	 * Get this row's values
	 * @return Values
	 */
	public List<Object> getValues();
	
	/**
	 * Clone this ResultRow
	 * @return New ResultRow
	 */
	@Override
	public ResultRow clone();
	}
