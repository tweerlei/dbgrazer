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
package de.tweerlei.common5.collections;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Iterator-Adapter für Enumerations
 * @param <T> Elementtyp
 * 
 * @author Robert Wruck
 */
public class EnumIterator<T> implements Iterator<T>
	{
	private final Enumeration<T> e;
	
	/**
	 * Konstruktor
	 * @param a Enumeration
	 */
	public EnumIterator(Enumeration<T> a)
		{
		e = a;
		}
	
	public boolean hasNext()
		{
		return (e.hasMoreElements());
		}
	
	public T next()
		{
		return (e.nextElement());
		}
	
	public void remove()
		{
		throw new UnsupportedOperationException();
		}
	}
