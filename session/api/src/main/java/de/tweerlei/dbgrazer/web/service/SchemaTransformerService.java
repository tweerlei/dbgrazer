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
package de.tweerlei.dbgrazer.web.service;

import java.util.List;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.model.SQLSchema;

/**
 * Transform SQLSchema objects
 * 
 * @author Robert Wruck
 */
public interface SchemaTransformerService
	{
	/**
	 * Mode for buildGraph
	 */
	public static enum GraphMode
		{
		/** No references */
		NO_REFS,
		/** Referenced tables */
		OUT_REFS,
		/** Referenced table and referencing tables of the starting table */
		START_REFS,
		/** Referenced and referencing tables */
		ALL_REFS
		}
	
	/**
	 * Delegate for creating hyperlinks to tables
	 */
	public static interface LinkBuilder
		{
		/**
		 * Create a table link
		 * @param qname Table name
		 * @param fk Table is only a reference
		 * @return Link
		 */
		public String buildLink(QualifiedName qname, boolean fk);
		}
	
	/**
	 * Extract a table description from a set of table descriptions
	 * @param s Set
	 * @param qn Table name
	 * @param dialect SQLDialect
	 * @return TableDescription or null
	 */
	public TableDescription findTable(Set<TableDescription> s, QualifiedName qn, SQLDialect dialect);
	
	/**
	 * Create a graph definition from a set of tables
	 * @param tableSet Tables
	 * @param start Starting table name
	 * @param name Graph name
	 * @param mode Graph mode
	 * @param linkBuilder Delegate for creating hyperlinks
	 * @param dialect SQLDialect
	 * @return GraphDefinition
	 */
	public Visualization buildGraph(Set<TableDescription> tableSet, QualifiedName start, String name, GraphMode mode, LinkBuilder linkBuilder, SQLDialect dialect);
	
	/**
	 * Get a List of formatted key descriptions in column order
	 * @param info TableDescription
	 * @return Key descriptions
	 */
	public List<String> getKeyIndices(TableDescription info);
	
	/**
	 * Build example DML statements
	 * @param t TableDescription
	 * @param dialect SQLDialect
	 * @return StatementProducer
	 */
	public StatementProducer buildDML(TableDescription t, SQLDialect dialect);
	
	/**
	 * Compare two SQLSchemas, producing DDL statements for modification
	 * @param left Left schema
	 * @param right Right schema
	 * @param ignoreCatalogSchema Ignore catalog and schema of tables
	 * @param prefix Table prefix to strip when comparing
	 * @param dialect SQLDialect
	 * @param crossDialect Schemas belong to different SQLDialects
	 * @return StatementProducer
	 */
	public StatementProducer compareSchemas(SQLSchema left, SQLSchema right, boolean ignoreCatalogSchema, String prefix, SQLDialect dialect, boolean crossDialect);
	}
