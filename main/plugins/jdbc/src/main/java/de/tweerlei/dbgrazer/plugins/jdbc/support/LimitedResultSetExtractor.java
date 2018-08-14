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
package de.tweerlei.dbgrazer.plugins.jdbc.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * A ResultSetExtractor that employs a RowCallbackHandler for handling rows but limits the number of rows processed.
 * This is necessary because some JDBC drivers don't honor Statement.setMaxRows().
 * 
 * @author Robert Wruck
 */
public class LimitedResultSetExtractor implements ResultSetExtractor
	{
	private final RowCallbackHandler handler;
	private final int limit;
	private int rows;
	
	/**
	 * Constructor
	 * @param handler RowCallbackHandler for processing single rows
	 * @param limit Max number of rows to process
	 */
	public LimitedResultSetExtractor(RowCallbackHandler handler, int limit)
		{
		this.handler = handler;
		this.limit = limit;
		this.rows = 0;
		}
	
	/**
	 * Get the number of processed rows (<= limit)
	 * @return number of processed rows
	 */
	public int getRowCount()
		{
		return (rows);
		}
	
	@Override
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException
		{
		while ((rows < limit) && rs.next())
			{
			handler.processRow(rs);
			rows++;
			}
		
		return (null);
		}
	}
