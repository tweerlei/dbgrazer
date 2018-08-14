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
package de.tweerlei.ermtools.dialect;

import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Create DDL for SQL objects
 * 
 * @author Robert Wruck
 */
public interface SQLObjectDDLWriter
	{
	/**
	 * Create a SELECT statement that returns SQL objects. Columns:
	 * - CATALOG
	 * - SCHEMA
	 * - NAME
	 * - TYPE
	 * - LENGTH (may be null if not supported)
	 * - HASH (may be null if not supported)
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name (null for all objects)
	 * @param type Type name (null for all types)
	 * @return SELECT statement
	 */
	public String findObjects(String catalog, String schema, String name, String type);
	
	/**
	 * Get a TableDescription for the results returned by the statement created by findObjects
	 * @return TableDescription
	 */
	public TableDescription getFindObjectsTableDescription();
	
	/**
	 * Create a SELECT statement that returns SQL objects. Columns:
	 * - CATALOG
	 * - SCHEMA
	 * - NAME
	 * - TYPE
	 * - LINE (may contain the whole object or just a single line)
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name (null for all objects)
	 * @param type Type name (null for all types)
	 * @return SELECT statement
	 */
	public String findObjectSource(String catalog, String schema, String name, String type);
	
	/**
	 * Get a TableDescription for the results returned by the statement created by findObjectSource
	 * @return TableDescription
	 */
	public TableDescription getFindObjectSourceTableDescription();
	
	/**
	 * Create a SELECT statement that returns SQL object privileges. Columns:
	 * - CATALOG
	 * - SCHEMA
	 * - NAME
	 * - TYPE
	 * - GRANTEE
	 * - PRIVILEGE
	 * - IS_GRANTABLE (YES or NO)
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name (null for all objects)
	 * @param type Type name (null for all types)
	 * @return SELECT statement
	 */
	public String findObjectPrivileges(String catalog, String schema, String name, String type);
	
	/**
	 * Get a TableDescription for the results returned by the statement created by findObjectPrivileges
	 * @return TableDescription
	 */
	public TableDescription getFindObjectPrivilegesTableDescription();
	
	/**
	 * Create a DDL statement for creating an SQL object
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @param source Object source
	 * @return DDL statement
	 */
	public String createObject(String catalog, String schema, String name, String type, String source);
	
	/**
	 * Create a DDL statement for dropping an SQL object
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @return DDL statement
	 */
	public String dropObject(String catalog, String schema, String name, String type);
	
	/**
	 * Create a DDL statement for replacing an SQL object
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @param source Object source
	 * @return DDL statement
	 */
	public String replaceObject(String catalog, String schema, String name, String type, String source);
	
	/**
	 * Check whether replaceObject can be used to replace an object's content.
	 * If not, you have to use dropObject / createObject.
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @return true if replaceObject can be used
	 */
	public boolean canReplaceObject(String catalog, String schema, String name, String type);
	
	/**
	 * Grant an object privilege
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @param grantee Grantee
	 * @param privilege Privilege to grant
	 * @param grantable Whether the grantee may grant this privilege
	 * @return DDL statement
	 */
	public String grantObjectPrivilege(String catalog, String schema, String name, String type, String grantee, String privilege, boolean grantable);
	
	/**
	 * Revoke an object privilege
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param name Object name
	 * @param type Object type
	 * @param grantee Grantee
	 * @param privilege Privilege to revoke
	 * @return DDL statement
	 */
	public String revokeObjectPrivilege(String catalog, String schema, String name, String type, String grantee, String privilege);
	}
