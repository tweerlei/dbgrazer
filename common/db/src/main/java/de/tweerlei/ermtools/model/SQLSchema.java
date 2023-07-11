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
package de.tweerlei.ermtools.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Schemadefinition
 * 
 * @author Robert Wruck
 */
public class SQLSchema
	{
	private final String catalog;
	private final String schema;
	private final Map<QualifiedName, TableDescription> tables;
	
	/**
	 * Konstruktor
	 * @param c Katalogname
	 * @param s Schemaname
	 */
	public SQLSchema(String c, String s)
		{
		catalog = StringUtils.notNull(c);
		schema = StringUtils.notNull(s);
		tables = new TreeMap<QualifiedName, TableDescription>();
		}
	
	/**
	 * Konstruktor
	 * @param c Katalogname
	 * @param s Schemaname
	 * @param td Tabellen
	 */
	public SQLSchema(String c, String s, Set<TableDescription> td)
		{
		this(c, s);
		for (TableDescription t : td)
			tables.put(t.getName(), t);
		}
	
	/**
	 * Liefert den Katalognamen
	 * @return Katalogname
	 */
	public String getCatalog()
		{
		return (catalog);
		}
	
	/**
	 * Liefert den Schemanamen
	 * @return Schemaname
	 */
	public String getSchema()
		{
		return (schema);
		}
	
	/**
	 * Fügt eine Tabelle hinzu
	 * @param t Tabellendefinition
	 */
	public void addTable(TableDescription t)
		{
		if (t == null)
			throw new SQLModelException("SQLSchema: Table is null");
		if (tables.containsKey(t.getName()))
			throw new SQLModelException("SQLSchema: Table already used: " + t.getName());
		
		tables.put(t.getName(), t);
		}
	
	/**
	 * Ersetzt eine Tabelle
	 * @param t Tabellendefinition
	 */
	public void replaceTable(TableDescription t)
		{
		if (t == null)
			throw new SQLModelException("SQLSchema: Table is null");
		if (!tables.containsKey(t.getName()))
			throw new SQLModelException("SQLSchema: Table not present: " + t.getName());
		
		tables.put(t.getName(), t);
		}
	
	/**
	 * Liefert eine Tabelle
	 * @param n Tabellenname
	 * @return SQLTable oder null
	 */
	public TableDescription getTable(String n)
		{
		return (tables.get(new QualifiedName(catalog, schema, n)));
		}
	
	/**
	 * Liefert eine Tabelle
	 * @param n Tabellenname
	 * @return SQLTable oder null
	 */
	public TableDescription getTable(QualifiedName n)
		{
		return (tables.get(n));
		}
	
	/**
	 * Liefert die Tabellen
	 * @return Map mit Tabellen
	 */
	public Map<QualifiedName, TableDescription> getTables()
		{
		return (Collections.unmodifiableMap(tables));
		}
	
	/**
	 * Akzeptiert einen SQLVisitor
	 * @param v SQLVisitor
	 */
	public void accept(SQLVisitor v)
		{
		v.beginSchema(this);
		for (Iterator<TableDescription> i = tables.values().iterator(); i.hasNext(); )
			{
			final TableDescription t = i.next();
			t.accept(v);
			}
		v.endSchema(this);
		}
	}
