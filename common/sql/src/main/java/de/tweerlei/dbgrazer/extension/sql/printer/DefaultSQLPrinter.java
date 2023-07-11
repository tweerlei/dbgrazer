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
package de.tweerlei.dbgrazer.extension.sql.printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.tweerlei.dbgrazer.extension.sql.handler.SQLPrinter;

/**
 * Consume SQL language tokens, counting brace levels
 * 
 * @author Robert Wruck
 */
public class DefaultSQLPrinter implements SQLPrinter
	{
	@Override
	public String printName(String t)
		{
		return (t);
		}
	
	@Override
	public String printIdentifier(String t)
		{
		return (t);
		}
	
	@Override
	public String printString(String t)
		{
		return ("'" + t.replace("'", "''") + "'");
		}
	
	@Override
	public String printNumber(String t)
		{
		return (t);
		}
	
	@Override
	public String printOperator(String t)
		{
		return (t);
		}
	
	@Override
	public List<String> printComment(List<String> lines)
		{
		if (lines.size() == 1)
			return (Collections.singletonList("/*" + lines.get(0) + "*/"));
		
		final List<String> ret = new ArrayList<String>(lines.size());
		for (Iterator<String> i = lines.iterator(); i.hasNext(); )
			{
			final String line = i.next();
			if (ret.isEmpty())
				ret.add("/*" + line);
			else if (i.hasNext())
				ret.add(line);
			else
				ret.add(line + "*/");
			}
		
		return (ret);
		}
	
	@Override
	public String printEOLComment(String comment)
		{
		return ("--" + comment);
		}
	}
