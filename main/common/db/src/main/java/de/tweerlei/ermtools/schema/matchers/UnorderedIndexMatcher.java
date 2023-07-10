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

import de.tweerlei.common5.jdbc.model.IndexDescription;

/**
 * Index matching ignoring the name and column ordering
 * 
 * @author Robert Wruck
 */
public class UnorderedIndexMatcher implements Comparator<IndexDescription>
	{
	public int compare(IndexDescription a, IndexDescription b)
		{
		if (a.isUnique() && !b.isUnique())
			return (-1);
		if (!a.isUnique() && b.isUnique())
			return (1);
		int d = b.getColumns().size() - a.getColumns().size();
		if (d != 0)
			return (d);
		for (String v : a.getColumns())
			{
			if (!b.getColumns().contains(v))
				return (-1);
			}
		return (0);
		}
	}
