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
package de.tweerlei.ermtools.schema.matchers;

import java.util.Comparator;

import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Type matching by JDBC type and dialect specific handling of length and decimals
 * 
 * @author Robert Wruck
 */
public class DialectTypeMatcher implements Comparator<TypeDescription>
	{
	private final SQLDialect dialect;
	
	/**
	 * Constructor
	 * @param dialect SQLDialect
	 */
	public DialectTypeMatcher(SQLDialect dialect)
		{
		this.dialect = dialect;
		}
	
	public int compare(TypeDescription a, TypeDescription b)
		{
		int d = b.getType() - a.getType();
		if (d != 0)
			return (d);
		final SQLDataType t = dialect.getSQLDataType(a.getType());
		if ((t == null) || (t.hasDecimals()))
			{
			d = b.getDecimals() - a.getDecimals();
			if (d != 0)
				return (d);
			}
		if ((t == null) || (t.hasLength()))
			{
			d = b.getLength() - a.getLength();
			if (d != 0)
				return (d);
			}
		return (0);
		}
	}
