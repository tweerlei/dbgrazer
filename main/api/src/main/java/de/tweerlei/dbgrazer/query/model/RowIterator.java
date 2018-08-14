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

import java.util.Iterator;
import java.util.List;

/**
 * Iterate over result rows
 * 
 * @author Robert Wruck
 */
public interface RowIterator extends Iterator<ResultRow>
	{
	/**
	 * Get the column definitions. Only valid after the first call to nextRow().
	 * @return Column definitions
	 */
	public List<ColumnDef> getColumns();
	
	/**
	 * Signal premature end of fetching; don't block the producer anymore
	 */
	public void abort();
	}
