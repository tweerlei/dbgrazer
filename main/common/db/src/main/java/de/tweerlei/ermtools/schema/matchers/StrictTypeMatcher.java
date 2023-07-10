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
import de.tweerlei.common5.jdbc.model.TypeDescription;

/**
 * Strict type matching
 * 
 * @author Robert Wruck
 */
public class StrictTypeMatcher implements Comparator<TypeDescription>
	{
	public int compare(TypeDescription a, TypeDescription b)
		{
		int d = StringUtils.compareTo(a.getName(), b.getName());
		if (d != 0)
			return (d);
		d = b.getDecimals() - a.getDecimals();
		if (d != 0)
			return (d);
		d = b.getLength() - a.getLength();
		if (d != 0)
			return (d);
		d = b.getType() - a.getType();
		if (d != 0)
			return (d);
		return (0);
		}
	}
