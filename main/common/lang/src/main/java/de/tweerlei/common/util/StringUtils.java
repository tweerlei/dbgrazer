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
package de.tweerlei.common.util;

import java.util.Iterator;

/**
 * \addtogroup misc Verschiedenes
 * @{
 */

/**
 * Hilfsfunktionen f�r Strings
 * 
 * @author Robert Wruck
 */
public final class StringUtils
	{
	/** Unerreichbarer Konstruktor */
	private StringUtils()
		{
		}
	
	/**
	 * Liefert true, wenn der �bergebene String entweder null oder leer ist
	 * @param s String
	 * @return boolean
	 */
	public static boolean empty(String s)
		{
		return ((s == null) || (s.length() == 0));
		}
	
	/**
	 * Liefert einen leeren String zur�ck, wenn das Argument null ist
	 * @param s String
	 * @return niemals null
	 */
	public static String notNull(String s)
		{
		return ((s == null) ? "" : s);
		}
	
	/**
	 * Liefert null zur�ck, wenn das Argument ein leerer String ist
	 * @param s String
	 * @return null wenn empty(s), sonst s
	 */
	public static String nullIfEmpty(String s)
		{
		return (empty(s) ? null : s);
		}
	
	/**
	 * Vergleicht zwei Strings
	 * @param a String
	 * @param b String
	 * @return 0 if the strings are equal or both null,
	 *         <0 if a is less than b,
	 *         >0 if a is greater than b
	 */
	public static int compareTo(String a, String b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return (-1);
		if (b == null)
			return (1);
		
		return (a.compareTo(b));
		}
	
	/**
	 * Vergleicht zwei Strings ohne Ber�cksichtigung von Gro�-/Kleinschreibung
	 * @param a String
	 * @param b String
	 * @return 0 if the strings are equal or both null,
	 *         <0 if a is less than b,
	 *         >0 if a is greater than b
	 */
	public static int compareToIgnoreCase(String a, String b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return (-1);
		if (b == null)
			return (1);
		
		return (a.compareToIgnoreCase(b));
		}
	
	/**
	 * Liefert true, wenn die Argument gleich sind oder beide null
	 * @param a String
	 * @param b String
	 * @return boolean
	 */
	public static boolean equals(String a, String b)
		{
		if (a == b)
			return (true);
		if (a == null)
			return (false);
		if (b == null)
			return (false);
		
		return (a.equals(b));
		}
	
	/**
	 * Liefert true, wenn die Argument gleich sind oder beide null
	 * @param a String
	 * @param b String
	 * @return boolean
	 */
	public static boolean equalsIgnoreCase(String a, String b)
		{
		if (a == b)
			return (true);
		if (a == null)
			return (false);
		if (b == null)
			return (false);
		
		return (a.equalsIgnoreCase(b));
		}
	
	/**
	 * Fügt mehrere Strings aneinander und trennt die Elemente
	 * @param a Strings
	 * @param sep Trennzeichen
	 * @return Ergebnis
	 */
	public static String join(Object[] a, String sep)
		{
		final StringBuffer ret = new StringBuffer();
		
		for (int i = 0; i < a.length; i++)
			{
			if (i > 0)
				ret.append(sep);
			ret.append(a[i]);
			}
		
		return (ret.toString());
		}
	
	/**
	 * Fügt mehrere Strings aneinander und trennt die Elemente
	 * @param i Iterator
	 * @param sep Trennzeichen
	 * @return Ergebnis
	 */
	public static String join(Iterator<String> i, String sep)
		{
		final StringBuffer ret = new StringBuffer();
		boolean first = true;
		
		while (i.hasNext())
			{
			if (first)
				first = false;
			else
				ret.append(sep);
			ret.append(i.next());
			}
		
		return (ret.toString());
		}
	
	/**
	 * Workaround für unerwartetes Verhalten von String.split:
	 * Das zurückgegebene Array enthält auch leere Felder am Ende
	 * @param s String
	 * @param sep Trennzeichen
	 * @return Die ermittelten Felder
	 */
	public static String[] split(String s, String sep)
		{
		if (s == null)
			return (new String[] { "" });
		return (s.split(sep, -1));
		}
	}
