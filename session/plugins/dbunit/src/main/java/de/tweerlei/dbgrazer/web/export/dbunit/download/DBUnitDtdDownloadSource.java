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
package de.tweerlei.dbgrazer.web.export.dbunit.download;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Create DBUnit DTD files from a TableDescription
 * 
 * @author Robert Wruck
 */
public class DBUnitDtdDownloadSource extends AbstractDtdDownloadSource
	{
	private final Set<TableDescription> tables;
	private final SQLDialect dialect;
	private final String comment;
	
	/**
	 * Constructor
	 * @param name File name
	 * @param tables TableDescriptions
	 * @param dialect SQLDialect
	 * @param comment Comment text
	 */
	public DBUnitDtdDownloadSource(String name, Set<TableDescription> tables, SQLDialect dialect, String comment)
		{
		super(name);
		this.tables = tables;
		this.dialect = dialect;
		this.comment = comment;
		}
	
	@Override
	protected void writeDtd(Writer w) throws IOException
		{
		if (!StringUtils.empty(comment))
			{
			w.write("<!-- ");
			w.write(comment);
			w.write(" -->\n\n");
			}
		
		w.write("<!ELEMENT dataset (\n\t");
		boolean first = true;
		for (TableDescription info : tables)
			{
			if (first)
				first = false;
			else
				w.write("\n\t| ");
			w.write(dialect.getQualifiedTableName(info.getName()));
			}
		w.write("\n\t)*>\n\n");
		
		for (TableDescription info : tables)
			{
			w.write("<!ELEMENT ");
			w.write(dialect.getQualifiedTableName(info.getName()));
			w.write(" EMPTY>\n");
			
			w.write("<!ATTLIST ");
			w.write(dialect.getQualifiedTableName(info.getName()));
			for (ColumnDescription c : info.getColumns())
				{
				w.write("\n\t");
				w.write(c.getName());
				w.write(" CDATA ");
				if (c.isNullable())
					w.write(" #IMPLIED");
				else
					w.write(" #REQUIRED");
				}
			w.write("\n\t>\n\n");
			}
		}
	}
