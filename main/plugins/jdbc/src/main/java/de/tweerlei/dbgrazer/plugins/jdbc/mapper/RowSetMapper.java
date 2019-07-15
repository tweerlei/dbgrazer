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
package de.tweerlei.dbgrazer.plugins.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.jdbc.core.RowCallbackHandler;

import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.impl.ResultSetAccessor;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * RowCallbackHandler that maps rows to RowSets
 * 
 * @author Robert Wruck
 */
public abstract class RowSetMapper implements RowCallbackHandler
	{
	private final ResultSetAccessor rsa;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param timeZone TimeZone to use for temporal results
	 */
	public RowSetMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone)
		{
		this.rsa = new ResultSetAccessor(sqlGenerator, dialect, timeZone);
		}
	
	@Override
	public final void processRow(ResultSet rs) throws SQLException
		{
		processRow(rs, rsa);
		}
	
	/**
	 * Process a single row
	 * @param rs ResultSet
	 * @param accessor ResultSetAccessor
	 * @throws SQLException on errors
	 */
	protected abstract void processRow(ResultSet rs, ResultSetAccessor accessor) throws SQLException;
	
	/**
	 * Get the extracted RowSets
	 * @return Map: Name -> RowSet
	 */
	public abstract Map<String, RowSetImpl> getRowSets();
	}
