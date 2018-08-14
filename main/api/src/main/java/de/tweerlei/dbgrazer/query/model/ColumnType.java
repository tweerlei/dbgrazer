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

import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;

import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.dbgrazer.common.util.Named;

/**
 * High level column type
 * 
 * @author Robert Wruck
 */
public enum ColumnType implements Named
	{
	/** SQL Number -> Java Number without decimal digits */
	INTEGER(Types.DECIMAL),
	/** SQL Decimal -> Java Number with decimal digits */
	FLOAT(Types.DECIMAL),
	/** SQL String -> Java String */
	STRING(Types.VARCHAR),
	/** SQL long String -> Java String */
	TEXT(Types.VARCHAR),
	/** SQL LIKE Pattern -> Java String with wildcard characters */
	PATTERN(Types.VARCHAR),
	/** SQL Date -> Java Date */
	DATE(Types.TIMESTAMP),
	/** SQL Boolean -> Java Boolean */
	BOOLEAN(Types.BOOLEAN),
	/** SQL Binary -> Java byte array */
	BINARY(Types.VARBINARY),
	/** SQL RowID -> Java RowId */
	ROWID(Types.ROWID),
	/** SQL Array -> Java Array */
	ARRAY(Types.ARRAY),
	/** SQL Structure -> Java Struct */
	STRUCT(Types.STRUCT),
	/** SQL BLOB -> Java Blob */
	BLOB(Types.BLOB),
	/** SQL CLOB -> Java Clob */
	CLOB(Types.CLOB),
	/** SQL XML -> Java SQLXML */
	XML(Types.SQLXML),
	/** SQL Type Reference -> Java Ref */
	REF(Types.REF),
	/** Unmapped data type */
	UNKNOWN(Types.OTHER);
	
	private final int defaultSQLType;
	
	private ColumnType(int defaultSQLType)
		{
		this.defaultSQLType = defaultSQLType;
		}
	
	@Override
	public String getName()
		{
		return (name());
		}
	
	/**
	 * Get the default SQL type (java.sql.Types) for this ColumnType.
	 * 
	 * @return SQL type
	 */
	public int getDefaultSQLType()
		{
		return (defaultSQLType);
		}
	
	/**
	 * Get the ColumnType for a given value from java.sql.Types
	 * @param type SQL type
	 * @return ColumnType
	 */
	public static ColumnType forSQLType(TypeDescription type)
		{
		switch (type.getType())
			{
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
				if (type.getLength() > 100)
					return (ColumnType.TEXT);
				else
					return (ColumnType.STRING);
			
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				return (ColumnType.INTEGER);
			
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.REAL:
				return (ColumnType.FLOAT);
			
			case Types.NUMERIC:
			case Types.DECIMAL:
				if (type.getDecimals() > 0)
					return (ColumnType.FLOAT);
				else if (type.getLength() == 0)	// Unknown precision - better use FLOAT
					return (ColumnType.FLOAT);
				else
					return (ColumnType.INTEGER);
			
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return (ColumnType.DATE);
			
			case Types.BIT:
			case Types.BOOLEAN:
				return (ColumnType.BOOLEAN);
			
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				return (ColumnType.BINARY);
			
			case Types.ARRAY:
				return (ColumnType.ARRAY);
			
			case Types.ROWID:
				return (ColumnType.ROWID);
			
			case Types.STRUCT:
				return (ColumnType.STRUCT);
			
			case Types.CLOB:
			case Types.NCLOB:
				return (ColumnType.CLOB);
			
			case Types.BLOB:
				return (ColumnType.BLOB);
			
			case Types.SQLXML:
				return (ColumnType.XML);
			
			case Types.REF:
				return (ColumnType.REF);
			
			default:
				return (ColumnType.UNKNOWN);
			}
		}
	
	/**
	 * Get the ColumnType for a given Java Object
	 * @param o Object
	 * @return ColumnType
	 */
	public static ColumnType forObject(Object o)
		{
		return (forClass((o == null) ? null : o.getClass()));
		}
	
	/**
	 * Get the ColumnType for a given Java Class
	 * @param type Class
	 * @return ColumnType
	 */
	public static ColumnType forClass(Class<?> type)
		{
		if (type == null)
			return (ColumnType.STRING);
		else if (Byte.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type) || BigInteger.class.isAssignableFrom(type))
			return (ColumnType.INTEGER);
		else if (Number.class.isAssignableFrom(type))
			return (ColumnType.FLOAT);
		else if (Date.class.isAssignableFrom(type))
			return (ColumnType.DATE);
		else if (Boolean.class.isAssignableFrom(type))
			return (ColumnType.BOOLEAN);
		else if (byte[].class.isAssignableFrom(type))
			return (ColumnType.BINARY);
		else
			return (ColumnType.STRING);
		}
	}
