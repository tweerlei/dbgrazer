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
package de.tweerlei.common5.jdbc.model;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Übersetzung von SQL-Typen
 * 
 * @author Robert Wruck
 */
public final class ColumnType
	{
	private static final Map<String, Integer> name_to_type = new HashMap<String, Integer>();
	private static final Map<Integer, String> type_to_name = new HashMap<Integer, String>();
	
	static
		{
		type_to_name.put(new Integer(Types.ARRAY),			"ARRAY");
		type_to_name.put(new Integer(Types.BIGINT),			"BIGINT");
		type_to_name.put(new Integer(Types.BINARY),			"BINARY");
		type_to_name.put(new Integer(Types.BIT),			"BIT");
		type_to_name.put(new Integer(Types.BLOB),			"BLOB");
		type_to_name.put(new Integer(Types.BOOLEAN),		"BOOLEAN");
		type_to_name.put(new Integer(Types.CHAR),			"CHAR");
		type_to_name.put(new Integer(Types.CLOB),			"CLOB");
		type_to_name.put(new Integer(Types.DATALINK),		"DATALINK");
		type_to_name.put(new Integer(Types.DATE),			"DATE");
		type_to_name.put(new Integer(Types.DECIMAL),		"DECIMAL");
		type_to_name.put(new Integer(Types.DISTINCT),		"DISTINCT");
		type_to_name.put(new Integer(Types.DOUBLE),			"DOUBLE");
		type_to_name.put(new Integer(Types.FLOAT),			"FLOAT");
		type_to_name.put(new Integer(Types.INTEGER),		"INTEGER");
		type_to_name.put(new Integer(Types.JAVA_OBJECT),	"JAVA_OBJECT");
		type_to_name.put(new Integer(Types.LONGVARBINARY),	"LONGVARBINARY");
		type_to_name.put(new Integer(Types.LONGVARCHAR),	"LONGVARCHAR");
		type_to_name.put(new Integer(Types.NULL),			"NULL");
		type_to_name.put(new Integer(Types.NUMERIC),		"NUMERIC");
		type_to_name.put(new Integer(Types.OTHER),			"OTHER");
		type_to_name.put(new Integer(Types.REAL),			"REAL");
		type_to_name.put(new Integer(Types.REF),			"REF");
		type_to_name.put(new Integer(Types.SMALLINT),		"SMALLINT");
		type_to_name.put(new Integer(Types.STRUCT),			"STRUCT");
		type_to_name.put(new Integer(Types.TIME),			"TIME");
		type_to_name.put(new Integer(Types.TIMESTAMP),		"TIMESTAMP");
		type_to_name.put(new Integer(Types.TINYINT),		"TINYINT");
		type_to_name.put(new Integer(Types.VARBINARY),		"VARBINARY");
		type_to_name.put(new Integer(Types.VARCHAR),		"VARCHAR");
		
		for (Iterator<Map.Entry<Integer, String>> i = type_to_name.entrySet().iterator(); i.hasNext(); )
			{
			final Map.Entry<Integer, String> ent = i.next();
			name_to_type.put(ent.getValue(), ent.getKey());
			}
		}
	
	/**
	 * Liefert die JDBC-Konstante zum Typnamen
	 * @param tn Typname
	 * @return JDBC-Konstante oder null
	 */
	public static Integer parseTypeName(String tn)
		{
		return (name_to_type.get(tn));
		}
	
	/**
	 * Liefert den Namen zur JDBC-Konstante
	 * @param t Konstante
	 * @return Name oder null
	 */
	public static String getTypeName(Integer t)
		{
		return (type_to_name.get(t));
		}
	
	private ColumnType()
		{
		}
	}
