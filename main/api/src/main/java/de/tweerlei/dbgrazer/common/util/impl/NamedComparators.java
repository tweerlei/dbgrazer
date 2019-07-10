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

import java.util.Comparator;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Compare named objects
 * 
 * @author Robert Wruck
 */
public final class NamedComparators
	{
	/** Compare by name */
	public static final Comparator<Named> BY_NAME = new Comparator<Named>()
		{
		@Override
		public int compare(Named a, Named b)
			{
			return (a.getName().compareTo(b.getName()));
			}
		};
	
	/**
	 * Compare according to Spring's Ordered interface or @Order annotation.
	 * Elements that have the same order will be ordered by their name
	 */
	@SuppressWarnings("unchecked")
	public static final Comparator<Named> BY_ORDER = new AnnotationAwareOrderComparator()
		{
		@Override
		public int compare(Object o1, Object o2)
			{
			final int ret = super.compare(o1, o2);
			if (ret != 0)
				return (ret);
			
			return (((Named) o1).getName().compareTo(((Named) o2).getName()));
			}
		};
	
	private NamedComparators()
		{
		}
	}
