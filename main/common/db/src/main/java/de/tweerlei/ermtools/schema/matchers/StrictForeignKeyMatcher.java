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
import java.util.Map;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;

/**
 * Strict FK matching
 * 
 * @author Robert Wruck
 */
public class StrictForeignKeyMatcher implements Comparator<ForeignKeyDescription>
	{
	public int compare(ForeignKeyDescription a, ForeignKeyDescription b)
		{
		int d = StringUtils.compareTo(a.getName(), b.getName());
		if (d != 0)
			return (d);
		d = a.getTableName().compareTo(b.getTableName());
		if (d != 0)
			return (d);
		d = b.getColumns().size() - a.getColumns().size();
		if (d != 0)
			return (d);
		for (Map.Entry<String, String> ent : a.getColumns().entrySet())
			{
			final String v = b.getColumns().get(ent.getKey());
			if (v == null)
				return (-1);
			d = StringUtils.compareTo(ent.getValue(), v);
			if (d != 0)
				return (d);
			}
		return (0);
		}
	}
