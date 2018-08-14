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
 * Handler for streamed data processing
 * 
 * @author Robert Wruck
 */
public interface RowHandler
	{
	/**
	 * Called before any rows
	 * @param columns Result column definitions
	 */
	public void startRows(List<ColumnDef> columns);
	
	/**
	 * Called for each row
	 * @param row Data row
	 * @return true to continue, false to abort
	 */
	public boolean handleRow(ResultRow row);
	
	/**
	 * Called after all rows
	 */
	public void endRows();
	
	/**
	 * Signal an error during row production
	 * @param e RuntimeException
	 */
	public void error(RuntimeException e);
	}
