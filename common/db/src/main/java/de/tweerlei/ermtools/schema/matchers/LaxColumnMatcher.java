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

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.TypeDescription;

/**
 * Column matching ignoring the name
 * 
 * @author Robert Wruck
 */
public class LaxColumnMatcher implements Comparator<ColumnDescription>
	{
	private final Comparator<TypeDescription> typeMatcher;
	
	/**
	 * Constructor
	 * @param typeMatcher Type matcher
	 */
	public LaxColumnMatcher(Comparator<TypeDescription> typeMatcher)
		{
		this.typeMatcher = typeMatcher;
		}
	
	public int compare(ColumnDescription a, ColumnDescription b)
		{
		int d = StringUtils.compareTo(a.getDefaultValue(), b.getDefaultValue());
		if (d != 0)
			return (d);
		if (a.isNullable() && !b.isNullable())
			return (-1);
		if (!a.isNullable() && b.isNullable())
			return (1);
		return (typeMatcher.compare(a.getType(), b.getType()));
		}
	}
