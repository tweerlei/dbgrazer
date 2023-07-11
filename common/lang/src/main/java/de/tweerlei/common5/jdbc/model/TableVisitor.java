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

/**
 * Besucher für SQLSchemata
 * 
 * @author Robert Wruck
 */
public interface TableVisitor
	{
	/**
	 * Aufgerufen bei Beginn einer Tabelle
	 * @param table SQLTable
	 */
	public void beginTable(TableDescription table);
	/**
	 * Aufgerufen bei Ende einer Tabelle
	 * @param table SQLTable
	 */
	public void endTable(TableDescription table);
	
	/**
	 * Aufgerufen bei Beginn der Spalten
	 */
	public void beginColumns();
	/**
	 * Aufgerufen für jede Spalte
	 * @param column SQLColumn
	 */
	public void visitColumn(ColumnDescription column);
	/**
	 * Aufgerufen bei Ende der Spalten
	 */
	public void endColumns();
	
	/**
	 * Aufgerufen für PK
	 * @param pk SQLPrimaryKey
	 */
	public void visitPrimaryKey(PrimaryKeyDescription pk);
	
	/**
	 * Aufgerufen bei Beginn der Indizes
	 */
	public void beginIndices();
	/**
	 * Aufgerufen für jeden Index
	 * @param index SQLIndex
	 */
	public void visitIndex(IndexDescription index);
	/**
	 * Aufgerufen bei Ende der Indizes
	 */
	public void endIndices();
	
	/**
	 * Aufgerufen bei Beginn der Fremdschlüssel
	 */
	public void beginForeignKeys();
	/**
	 * Aufgerufen für jeden Fremdschlüssel
	 * @param fk SQLForeignKey
	 */
	public void visitForeignKey(ForeignKeyDescription fk);
	/**
	 * Aufgerufen bei Ende der Fremdschlüssel
	 */
	public void endForeignKeys();
	
	/**
	 * Aufgerufen bei Beginn der Berechtigungen
	 */
	public void beginPrivileges();
	/**
	 * Aufgerufen für jede Berechtigung
	 * @param p PrivilegeDescription
	 */
	public void visitPrivilege(PrivilegeDescription p);
	/**
	 * Aufgerufen bei Ende der Berechtigungen
	 */
	public void endPrivileges();
	}
