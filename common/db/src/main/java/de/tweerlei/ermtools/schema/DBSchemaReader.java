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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.impl.JdbcMetadataReader;
import de.tweerlei.ermtools.model.SQLSchema;

/**
 * Auslesen von Schemainformationen per JDBC
 * 
 * @author Robert Wruck
 */
public class DBSchemaReader
	{
	/**
	 * Erzeugt ein SQLSchema durch Auslesen einer DB
	 * @param c Connection
	 * @param catalogName Katalogname oder null
	 * @param schemaName Schemaname oder null
	 * @param pattern Suchmuster für Tabellen oder null
	 * @return SQLSchema
	 * @throws SQLException bei Fehlern
	 */
	public SQLSchema dumpSchema(Connection c, String catalogName, String schemaName, String pattern) throws SQLException
		{
		final String curCat = (catalogName == null) ? c.getCatalog() : catalogName;
		
		final SQLSchema schema = new SQLSchema(curCat, schemaName);
		
		final MetadataReader md = new JdbcMetadataReader(c.getMetaData());
		final Map<String, String> tables = md.getTables(curCat, schemaName);
		int i = 0;
		for (Map.Entry<String, String> ent : tables.entrySet())
			{
			if ("TABLE".equals(ent.getValue()))
				schema.addTable(md.getTableDescription(curCat, schemaName, ent.getKey()));
			i++;
			System.err.print("\r" + i + "/" + tables.size());
			System.err.flush();
			}
		System.err.println();
		System.err.flush();
		
		return (schema);
		}
	}
