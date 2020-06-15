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

import java.sql.Connection;
import java.sql.SQLException;

import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.common5.jdbc.model.TypeDescription;

/**
 * Erzeugt DBMS-spezifische DDL-Statements
 * 
 * @author Robert Wruck
 */
public interface SQLDialect
	{
	/**
	 * Liefert den Spaltentyp für ein CREATE-Statement
	 * @param c TypeDescription
	 * @return Typname
	 */
	public String dataTypeToString(TypeDescription c);
	
	/**
	 * Get the dialect specific data type definition
	 * @param type JDBC type
	 * @return SQLDataType or null
	 */
	public SQLDataType getSQLDataType(int type);
	
	/**
	 * Get the object name to use in SQL statements
	 * @param qn QualifiedName
	 * @return Formatted name
	 */
	public String getQualifiedTableName(QualifiedName qn);
	
	/**
	 * Get the statement terminator string
	 * @return Statement terminator
	 */
	public String getStatementTerminator();
	
	/**
	 * Get a SimpleDateFormat format String to use for creatig DATE literals
	 * @return Format String
	 */
	public String getDateFormat();
	
	/**
	 * Get a SimpleDateFormat format String to use for creatig TIME literals
	 * @return Format String
	 */
	public String getTimeFormat();
	
	/**
	 * Get a SimpleDateFormat format String to use for creatig DATETIME literals
	 * @return Format String
	 */
	public String getDatetimeFormat();
	
	/**
	 * Get a SimpleDateFormat format String to use for creatig TIMESTAMP literals
	 * @return Format String
	 */
	public String getTimestampFormat();
	
	/**
	 * Get whether this dialect supports boolean literals
	 * @return true if true/false can be used
	 */
	public boolean supportsBoolean();
	
	/**
	 * Check whether SQL:2003 MERGE statements are supported
	 * @return Merge support
	 */
	public boolean supportsMerge();
	
	/**
	 * Check whether DML statements require the statement terminator
	 * @return true if required
	 */
	public boolean dmlRequiresTerminator();
	
	/**
	 * Get the table name for usage in SELECT statements that don't need a source table
	 * @return Table name or null if the whole FROM clause can be omitted
	 */
	public String getDefaultTableName();
	
	/**
	 * Get the statement to execute before inserting rows into a table
	 * @param t TableDescription
	 * @return Prepare statement
	 */
	public String prepareInsert(TableDescription t);
	
	/**
	 * Get the statement to execute after inserting rows into a table
	 * @param t TableDescription
	 * @return Finish statement
	 */
	public String finishInsert(TableDescription t);
	
	/**
	 * Create a table
	 * @param t TableDescription
	 * @return Statement
	 */
	public String createTable(TableDescription t);
	
	/**
	 * Modify a table (name/comment only)
	 * @param old Old TableDescription
	 * @param t New TableDescription
	 * @return Statement
	 */
	public String modifyTable(TableDescription old, TableDescription t);
	
	/**
	 * Drop a table
	 * @param t TableDescription
	 * @return Statement
	 */
	public String dropTable(TableDescription t);
	
	/**
	 * Grant a privilege
	 * @param t TableDescription
	 * @param p PrivilegeDescription
	 * @return Statement
	 */
	public String grantPrivilege(TableDescription t, PrivilegeDescription p);
	
	/**
	 * Revoke a privilege
	 * @param t TableDescription
	 * @param p PrivilegeDescription
	 * @return Statement
	 */
	public String revokePrivilege(TableDescription t, PrivilegeDescription p);
	
	/**
	 * Add a column to a table
	 * @param t TableDescription
	 * @param c New ColumnDescription
	 * @return Statement
	 */
	public String addColumn(TableDescription t, ColumnDescription c);
	
	/**
	 * Modify a column in a table
	 * @param t TableDescription
	 * @param c New ColumnDescription
	 * @param old Old ColumnDescription
	 * @return Statement
	 */
	public String modifyColumn(TableDescription t, ColumnDescription c, ColumnDescription old);
	
	/**
	 * Remove a column from a table
	 * @param t TableDescription
	 * @param c New ColumnDescription
	 * @return Statement
	 */
	public String removeColumn(TableDescription t, ColumnDescription c);
	
	/**
	 * Create an index
	 * @param t TableDescription
	 * @param ix IndexDescription
	 * @return Statement
	 */
	public String createIndex(TableDescription t, IndexDescription ix);
	
	/**
	 * Drop an index
	 * @param t TableDescription
	 * @param ix IndexDescription
	 * @return Statement
	 */
	public String dropIndex(TableDescription t, IndexDescription ix);
	
	/**
	 * Add a PK to a table
	 * @param t TableDescription
	 * @param k New PrimaryKeyDescription
	 * @return Statement
	 */
	public String addPrimaryKey(TableDescription t, PrimaryKeyDescription k);
	
	/**
	 * Remove a PK from a table
	 * @param t TableDescription
	 * @param k New PrimaryKeyDescription
	 * @return Statement
	 */
	public String removePrimaryKey(TableDescription t, PrimaryKeyDescription k);
	
	/**
	 * Add an FK to a table
	 * @param t TableDescription
	 * @param fk New ForeignKeyDescription
	 * @return Statement
	 */
	public String addForeignKey(TableDescription t, ForeignKeyDescription fk);
	
	/**
	 * Remove an FK from a table
	 * @param t TableDescription
	 * @param fk New ForeignKeyDescription
	 * @return Statement
	 */
	public String removeForeignKey(TableDescription t, ForeignKeyDescription fk);
	
	/**
	 * Get a MetadataReader for a JDBC connection
	 * @param c Connection
	 * @return MetadataReader
	 * @throws SQLException on error
	 */
	public MetadataReader getMetadataReader(Connection c) throws SQLException;
	
	/**
	 * Get an SQLStatementAnalyzer for a JDBC connection
	 * @param c Connection
	 * @return SQLStatementAnalyzer
	 * @throws SQLException on error
	 */
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException;
	
	/**
	 * Get a SQLScriptOutputReader for a JDBC connection
	 * @param c Connection
	 * @return SQLScriptOutputReader
	 * @throws SQLException on error
	 */
	public SQLScriptOutputReader getScriptOutputReader(Connection c) throws SQLException;
	
	/**
	 * Get a SQLObjectDDLWriter
	 * @return SQLObjectDDLWriter
	 */
	public SQLObjectDDLWriter getObjectDDLWriter();
	}
