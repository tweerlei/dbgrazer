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
package de.tweerlei.dbgrazer.plugins.jdbc.types;

/**
 * Attributes used by JDBC QueryTypes
 * 
 * @author Robert Wruck
 */
public final class QueryTypeAttributes
	{
	/** RowSet attribute for the TextFormatter name */
	public static final String ATTR_FORMATTER = "formatter";
	/** RowSet attribute for the colorize flag */
	public static final String ATTR_COLORIZE = "colorize";
	/** RowSet attribute for calculating a sum row */
	public static final String ATTR_SUM_ROW = "sumRow";
	/** RowSet attribute for the table catalog name */
	public static final String ATTR_CATALOG = "tableCatalog";
	/** RowSet attribute for the table schema name */
	public static final String ATTR_SCHEMA = "tableSchema";
	/** RowSet attribute for the table name */
	public static final String ATTR_TABLE = "tableName";
	/** RowSet attribute for the table PK subselect */
	public static final String ATTR_PK_SELECT = "tablePKSelect";
	/** RowSet attribute for the hide ID column flag */
	public static final String ATTR_HIDE_ID = "hideId";
	/** RowSet attribute for the tabular layout flag */
	public static final String ATTR_TABLES = "tables";
	/** RowSet attribute for the cube dimensions */
	public static final String ATTR_DIMENSIONS = "dimensions";
	/** RowSet attribute for the cube results */
	public static final String ATTR_RESULTS = "results";
	/** RowSet attribute for the cube dimension */
	public static final String ATTR_DIMENSION = "dimension";
	/** RowSet attribute for the aggregation functions */
	public static final String ATTR_FUNCS = "funcs";
	
	private QueryTypeAttributes()
		{
		}
	}
