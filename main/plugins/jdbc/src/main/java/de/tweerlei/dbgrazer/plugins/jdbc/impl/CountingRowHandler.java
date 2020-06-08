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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import de.tweerlei.dbgrazer.query.model.RowHandler;

/**
 * StatementHandler that executes passed statements
 * 
 * @author Robert Wruck
 */
public interface CountingRowHandler extends RowHandler
	{
	/**
	 * Get the total number of rows affected by all statements so far
	 * @return Row count
	 */
	public int getTotalRowCount();
	
	/**
	 * Get the number of uncommitted rows
	 * @return Row count
	 */
	public int getUncommittedRowCount();
	
	/**
	 * Get any error messages
	 * @return Errors
	 */
	public String getErrors();
	}
