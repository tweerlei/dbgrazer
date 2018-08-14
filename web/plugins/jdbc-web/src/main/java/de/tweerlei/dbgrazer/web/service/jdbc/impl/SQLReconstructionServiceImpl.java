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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLConsumer;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.service.jdbc.SQLReconstructionService;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
@Service
public class SQLReconstructionServiceImpl implements SQLReconstructionService
	{
	private final SQLGeneratorService sqlGenerator;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 */
	@Autowired
	public SQLReconstructionServiceImpl(SQLGeneratorService sqlGenerator)
		{
		this.sqlGenerator = sqlGenerator;
		}
	
	@Override
	public String buildSQL(RowSet rs, SQLDialect dialect)
		{
		final Map<QualifiedName, String> tables = new LinkedHashMap<QualifiedName, String>();
		
		final SQLConsumer sb = sqlGenerator.createConsumer(Style.INDENTED);
		
		sb.appendName("SELECT");
		
		final int last = rs.getColumns().size() - 1;
		int n = 0;
		for (ColumnDef c : rs.getColumns())
			{
			sb.appendEOLComment((c.getTypeName() == null) ? c.getType().toString() : c.getTypeName());
			
			if (c.getSourceObject() != null)
				{
				String alias = tables.get(c.getSourceObject());
				if (alias == null)
					{
					alias = "t" + tables.size();
					tables.put(c.getSourceObject(), alias);
					}
				sb.appendName(alias).appendOperator(".");
				}
			sb.appendName((c.getSourceColumn() == null) ? c.getName() : c.getSourceColumn());
			sb.appendName("AS");
			sb.appendName(c.getName());
			
			if (n != last)
				sb.appendOperator(",");
			
			n++;
			}
		
		if (!tables.isEmpty())
			{
			sb.appendName("FROM");
			
			n = 0;
			for (Map.Entry<QualifiedName, String> ent : tables.entrySet())
				{
				if (n > 0)
					sb.appendName("JOIN");
				
				sb.appendName(dialect.getQualifiedTableName(ent.getKey()));
				sb.appendName(ent.getValue());
				if (n > 0)
					{
					sb.appendName("ON");
					sb.openBrace();
					sb.appendName(ent.getValue()).appendOperator(".").appendName("col");
					sb.appendOperator("=");
					sb.appendName("t0").appendOperator(".").appendName("col");
					sb.closeBrace();
					}
				n++;
				}
			}
		
		return (sb.finish().toString());
		}
	}
