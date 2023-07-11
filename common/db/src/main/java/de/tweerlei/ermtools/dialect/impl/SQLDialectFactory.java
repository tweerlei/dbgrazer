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
package de.tweerlei.ermtools.dialect.impl;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.mssql.MSSQLDialect;
import de.tweerlei.ermtools.dialect.mysql.MySQLDialect;
import de.tweerlei.ermtools.dialect.oracle.OracleDialect;
import de.tweerlei.ermtools.dialect.postgresql.PostgreSQLDialect;

/**
 * Factory that knows about all available SQLDialect implementations
 * 
 * @author Robert Wruck
 */
public class SQLDialectFactory
	{
	private static Map<String, SQLDialect> dialects;
	static
		{
		final Map<String, SQLDialect> tmp = new TreeMap<String, SQLDialect>();
		tmp.put("generic", new GenericDialect());
		tmp.put("mssql", new MSSQLDialect());
		tmp.put("mysql", new MySQLDialect());
		tmp.put("oracle", new OracleDialect());
		tmp.put("postgresql", new PostgreSQLDialect());
		
		dialects = Collections.unmodifiableMap(tmp);
		}
	
	private SQLDialectFactory()
		{
		}
	
	/**
	 * Get all available dialects
	 * @return Map: Name -> SQLDialect
	 */
	public static Map<String, SQLDialect> getSQLDialects()
		{
		return (dialects);
		}
	
	/**
	 * Get a dialect by name or a default dialect if the name is unknown
	 * @param name Dialect name
	 * @return SQLDialect, never null
	 */
	public static SQLDialect getSQLDialect(String name)
		{
		if (name != null)
			{
			final SQLDialect ret = dialects.get(name);
			if (ret != null)
				return (ret);
			}
		
		return (dialects.get("generic"));
		}
	}
