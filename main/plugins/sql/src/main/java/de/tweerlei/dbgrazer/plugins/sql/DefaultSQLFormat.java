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
package de.tweerlei.dbgrazer.plugins.sql;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.sql.SQLFormat;
import de.tweerlei.dbgrazer.extension.sql.handler.MultilineSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.handler.SQLPlusStatementSplitter;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLHandler;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;

/**
 * Default SQL format
 * 
 * @author Robert Wruck
 */
@Service
@Order(1)
public class DefaultSQLFormat implements SQLFormat
	{
	@Override
	public SQLHandler createHandler()
		{
		return (new MultilineSQLHandler());
		}
	
	@Override
	public List<String> parseScript(String script, boolean includeSeparator)
		{
		final SQLPlusStatementSplitter h = new SQLPlusStatementSplitter(includeSeparator);
		new SQLParser(h, true).parse(script);
		return (h.getStatements());
		}
	}
