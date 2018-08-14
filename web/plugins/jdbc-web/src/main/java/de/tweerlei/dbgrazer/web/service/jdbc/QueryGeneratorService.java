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

import java.util.Map;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Generate Queries from table metadata
 *
 * @author Robert Wruck
 */
public interface QueryGeneratorService
	{
	/**
	 * Generate a SELECT query for a TableDescription
	 * @param t TableDescription
	 * @param dialect SQLDialect
	 * @param fmt DataFormatter
	 * @return Query
	 */
	public Query createSelectQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt);
	
	/**
	 * Create an INSERT query for a TableDescription
	 * @param t TableDescription
	 * @param dialect SQLDialect
	 * @param fmt DataFormatter
	 * @param pkExpr PK value expression, null to create a query parameter for the PK
	 * @param values Actual values (columns with NULL values will not be included in the statement)
	 * @return Query
	 */
	public Query createInsertQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, String pkExpr, Map<Integer, String> values);
	
	/**
	 * Create an UPDATE query for a TableDescription
	 * @param t TableDescription
	 * @param dialect SQLDialect
	 * @param fmt DataFormatter
	 * @param includePK Include the PK column as query parameter
	 * @return Query
	 */
	public Query createUpdateQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, boolean includePK);
	
	/**
	 * Create a DELETE query for a TableDescription
	 * @param t TableDescription
	 * @param dialect SQLDialect
	 * @param fmt DataFormatter
	 * @return Query
	 */
	public Query createDeleteQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt);
	}
