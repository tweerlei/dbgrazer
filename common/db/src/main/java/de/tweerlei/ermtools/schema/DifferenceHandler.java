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
package de.tweerlei.ermtools.schema;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Handler für erkannte ERM-Differenzen
 * 
 * @author Robert Wruck
 */
public interface DifferenceHandler
	{
	/**
	 * Aufgerufen für hinzugefügte Tabellen
	 * @param t SQLTable
	 */
	public void tableAdded(TableDescription t);
	
	/**
	 * Aufgerufen für entfernte Tabellen
	 * @param t SQLTable Alte Tabellendefinition
	 */
	public void tableRemoved(TableDescription t);
	
	/**
	 * Aufgerufen, bevor zwei existierende Tabellen verglichen werden 
	 * @param cOld Alte Tabellendefinition
	 * @param cNew Neue Tabellendefinition
	 */
	public void startTable(TableDescription cOld, TableDescription cNew);
	
	/**
	 * Aufgerufen, nachdem zwei existierende Tabellen verglichen wurden 
	 * @param cOld Alte Tabellendefinition
	 * @param cNew Neue Tabellendefinition
	 */
	public void endTable(TableDescription cOld, TableDescription cNew);
	
	/**
	 * Aufgerufen für hinzugefügte Spalten
	 * @param t SQLTable
	 * @param c SQLColumn Neue Spaltendefinition
	 */
	public void columnAdded(TableDescription t, ColumnDescription c);
	
	/**
	 * Aufgerufen für entfernte Spalten
	 * @param t SQLTable
	 * @param c SQLColumn Alte Spaltendefinition
	 */
	public void columnRemoved(TableDescription t, ColumnDescription c);
	
	/**
	 * Aufgerufen für geänderte Spalten
	 * @param t SQLTable
	 * @param c Neue Spaltendefinition
	 * @param old Alte Spaltendefinition
	 */
	public void columnChanged(TableDescription t, ColumnDescription c, ColumnDescription old);
	
	/**
	 * Aufgerufen für hinzugefügte Primärschlüssel
	 * @param t SQLTable
	 * @param k SQLPrimaryKey
	 */
	public void pkAdded(TableDescription t, PrimaryKeyDescription k);
	
	/**
	 * Aufgerufen für entfernte Primärschlüssel
	 * @param t SQLTable
	 * @param k SQLPrimaryKey
	 */
	public void pkRemoved(TableDescription t, PrimaryKeyDescription k);
	
	/**
	 * Aufgerufen für geänderte Primärschlüssel
	 * @param t SQLTable
	 * @param k Neue PK-Definition
	 * @param old Alte PK-Definition
	 */
	public void pkChanged(TableDescription t, PrimaryKeyDescription k, PrimaryKeyDescription old);
	
	/**
	 * Aufgerufen für hinzugefügte Indizes
	 * @param t SQLTable
	 * @param i Neue Indexdefinition
	 */
	public void indexAdded(TableDescription t, IndexDescription i);
	
	/**
	 * Aufgerufen für entfernte Indizes
	 * @param t SQLTable
	 * @param i Alte Indexdefinition
	 */
	public void indexRemoved(TableDescription t, IndexDescription i);
	
	/**
	 * Aufgerufen für hinzugefügte Fremdschlüssel
	 * @param t SQLTable
	 * @param i Neue Fremdschlüsseldefinition
	 */
	public void fkAdded(TableDescription t, ForeignKeyDescription i);
	
	/**
	 * Aufgerufen für entfernte Fremdschlüssel
	 * @param t SQLTable
	 * @param i Alte Fremdschlüsseldefinition
	 */
	public void fkRemoved(TableDescription t, ForeignKeyDescription i);
	
	/**
	 * Aufgerufen für hinzugefügte Berechtigungen
	 * @param t SQLTable
	 * @param i Neue Berechtigungsdefinition
	 */
	public void privilegeAdded(TableDescription t, PrivilegeDescription i);
	
	/**
	 * Aufgerufen für entfernte Berechtigungen
	 * @param t SQLTable
	 * @param i Alte Berechtigungsdefinition
	 */
	public void privilegeRemoved(TableDescription t, PrivilegeDescription i);
	}
