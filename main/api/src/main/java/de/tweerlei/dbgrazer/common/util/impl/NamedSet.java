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
package de.tweerlei.dbgrazer.common.util.impl;

import java.util.Collection;
import java.util.TreeSet;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * TreeSet that orders items according to Spring's Ordered interface or @Order annotation.
 * Elements that have the same order will be ordered by their name
 * @param <T> Element type
 * 
 * @author Robert Wruck
 */
public class NamedSet<T extends Named> extends TreeSet<T>
	{
	/**
	 * Constructor
	 */
	public NamedSet()
		{
		this(null);
		}
	
	/**
	 * Constructor
	 * @param c Initial values
	 */
	public NamedSet(Collection<? extends T> c)
		{
		super(NamedComparators.BY_ORDER);
		if (c != null)
			addAll(c);
		}
	}
