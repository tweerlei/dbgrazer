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
import de.tweerlei.common5.jdbc.model.IndexDescription;

/**
 * Strict index matching
 * 
 * @author Robert Wruck
 */
public class StrictIndexMatcher implements Comparator<IndexDescription>
	{
	public int compare(IndexDescription a, IndexDescription b)
		{
		int d = StringUtils.compareTo(a.getName(), b.getName());
		if (d != 0)
			return (d);
		if (a.isUnique() && !b.isUnique())
			return (-1);
		if (!a.isUnique() && b.isUnique())
			return (1);
		int n = a.getColumns().size();
		d = b.getColumns().size() - n;
		if (d != 0)
			return (d);
		for (int i = 0; i < n; i++)
			{
			d = StringUtils.compareTo(a.getColumns().get(i), b.getColumns().get(i));
			if (d != 0)
				return (d);
			}
		return (0);
		}
	}
