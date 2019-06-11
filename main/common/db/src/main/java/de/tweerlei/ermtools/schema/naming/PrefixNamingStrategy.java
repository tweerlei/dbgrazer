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
package de.tweerlei.ermtools.schema.naming;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.ermtools.schema.SchemaNamingStrategy;

/**
 * Strict comparison, but ignoring catalog and schema
 * 
 * @author Robert Wruck
 */
public class PrefixNamingStrategy implements SchemaNamingStrategy
	{
	private final String prefix;
	
	public PrefixNamingStrategy(String prefix)
		{
		this.prefix = prefix;
		}
	
	public String getTableName(QualifiedName qn)
		{
		if (qn.getObjectName().startsWith(prefix))
			return (qn.getObjectName().substring(prefix.length()));
		else
			return (qn.getObjectName());
		}
	
	public String getColumnName(String c)
		{
		return (c);
		}
	}