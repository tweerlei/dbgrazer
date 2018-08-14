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

import java.util.HashSet;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.ermtools.schema.ObjectMatcher;

/**
 * Index matching ignoring the name and column ordering
 * 
 * @author Robert Wruck
 */
public class UnorderedIndexMatcher implements ObjectMatcher<IndexDescription>
	{
	public boolean equals(IndexDescription a, IndexDescription b)
		{
		if (a.isUnique() != b.isUnique())
			return false;
		final Set<String> ca = new HashSet<String>(a.getColumns());
		final Set<String> cb = new HashSet<String>(b.getColumns());
		if (!ca.equals(cb))
			return false;
		return true;
		}
	}
