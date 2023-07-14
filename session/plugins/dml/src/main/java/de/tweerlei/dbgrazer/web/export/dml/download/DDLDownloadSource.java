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
package de.tweerlei.dbgrazer.web.export.dml.download;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.dbgrazer.web.service.ScriptWriterService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.model.SQLSchema;

/**
 * Download a RowSet as SQL INSERT statements
 * 
 * @author Robert Wruck
 */
public class DDLDownloadSource extends AbstractDDLDownloadSource
	{
	private final SchemaTransformerService schemaTransformerService;
	private final ScriptWriterService resultBuilder;
	private final Set<TableDescription> tables;
	private final String comment;
	
	/**
	 * Constructor
	 * @param name File name
	 * @param tables TableDescriptions
	 * @param dialect SQLDialect
	 * @param comment Comment
	 * @param schemaTransformerService SchemaTransformerService
	 * @param resultBuilder ScriptWriterService
	 */
	public DDLDownloadSource(String name, Set<TableDescription> tables, SQLDialect dialect, String comment,
			SchemaTransformerService schemaTransformerService, ScriptWriterService resultBuilder)
		{
		super(name, dialect);
		this.schemaTransformerService = schemaTransformerService;
		this.resultBuilder = resultBuilder;
		this.tables = tables;
		this.comment = comment;
		}
	
	@Override
	protected void writeDDL(Writer sw, SQLDialect d) throws IOException
		{
		final StatementProducer p = schemaTransformerService.compareSchemas(new SQLSchema(null, null), new SQLSchema(null, null, tables), true, null, d, false);
		sw.write(resultBuilder.writeScript(p, comment, d.getScriptStatementWrapper()));
		}
	}
