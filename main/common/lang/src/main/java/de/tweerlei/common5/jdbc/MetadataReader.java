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
package de.tweerlei.common5.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import de.tweerlei.common5.jdbc.model.ProcedureDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Read database metadata
 * 
 * @author Robert Wruck
 */
public interface MetadataReader
	{
	/**
	 * Get the default catalog name
	 * @return Catalog name
	 * @throws SQLException on error
	 */
	public String getDefaultCatalogName() throws SQLException;
	
	/**
	 * Get the default schema name
	 * @return Schema name
	 * @throws SQLException on error
	 */
	public String getDefaultSchemaName() throws SQLException;
	
	/**
	 * Get available catalog names
	 * @return Catalog names
	 * @throws SQLException on error
	 */
	public List<String> getCatalogNames() throws SQLException;
	
	/**
	 * Get available schema names
	 * @return Schema names
	 * @throws SQLException on error
	 */
	public List<String> getSchemaNames() throws SQLException;
	
	/**
	 * Get table names
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @return Map: Table name -> Table type
	 * @throws SQLException on error
	 */
	public Map<String, String> getTables(String catalog, String schema) throws SQLException;
	
	/**
	 * Get procedure names
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @return Map: Procedure name -> Procedure type (PROCEDURE or FUNCTION)
	 * @throws SQLException on error
	 */
	public Map<String, String> getProcedures(String catalog, String schema) throws SQLException;
	
	/**
	 * Get data type names
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @return Map: UDT name -> Type (from java.sql.Types)
	 * @throws SQLException on error
	 */
	public Map<String, Integer> getDatatypes(String catalog, String schema) throws SQLException;
	
	/**
	 * Get a table description
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param table Table name
	 * @return TableDescription
	 * @throws SQLException on error
	 */
	public TableDescription getTableDescription(String catalog, String schema, String table) throws SQLException;
	
	/**
	 * Get a table description
	 * @param catalog Catalog name
	 * @param schema Schema name
	 * @param proc Procedure name
	 * @return ProcedureDescription
	 * @throws SQLException on error
	 */
	public ProcedureDescription getProcedureDescription(String catalog, String schema, String proc) throws SQLException;
	}
