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

import java.util.Comparator;

/**
 * Popular String comparators, for use in e.g. TreeMap and TreeSet
 * 
 * @author Robert Wruck
 */
public final class StringComparators
	{
	/** Natural ordering, nulls first */
	public static final Comparator<String> NATURAL = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareTo(a, b);
			}
		};
	/** Reverse natural ordering, nulls last */
	public static final Comparator<String> REVERSE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareTo(b, a);
			}
		};
	/** Case insensitive ordering, nulls first */
	public static final Comparator<String> CASE_INSENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToIgnoreCase(a, b);
			}
		};
	/** Reverse case insensitive ordering, nulls last */
	public static final Comparator<String> REVERSE_CASE_INSENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToIgnoreCase(b, a);
			}
		};
	/** Case insensitive ordering, 'A' before 'a' before 'B' before 'b', nulls first */
	public static final Comparator<String> CASE_PRESERVING = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			final int t = StringComparators.compareToIgnoreCase(a, b);
			return ((t == 0) ? StringComparators.compareTo(a, b) : t);
			}
		};
	/** Reverse case insensitive ordering, 'b' before 'B' before 'a' before 'A', nulls last */
	public static final Comparator<String> REVERSE_CASE_PRESERVING = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			final int t = StringComparators.compareToIgnoreCase(b, a);
			return ((t == 0) ? StringComparators.compareTo(b, a) : t);
			}
		};
	/**
	 * A case-sensitive comparator, 'abc2d' before 'abc10d'
	 */
	public static final Comparator<String> NUMERIC_CASE_SENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToNumeric(a, b);
			}
		};
	/**
	 * A case-sensitive comparator, 'abc10d' before 'abc2d'
	 */
	public static final Comparator<String> REVERSE_NUMERIC_CASE_SENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToNumeric(b, a);
			}
		};
	/**
	 * A case-insensitive comparator, 'abc2d' before 'Abc10d'
	 */
	public static final Comparator<String> NUMERIC_CASE_INSENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToNumericIgnoreCase(a, b);
			}
		};
	/**
	 * A case-insensitive comparator, 'Abc10d' before 'abc2d'
	 */
	public static final Comparator<String> REVERSE_NUMERIC_CASE_INSENSITIVE = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			return StringComparators.compareToNumericIgnoreCase(b, a);
			}
		};
	/**
	 * A case-insensitive comparator, 'abc2d' before 'Abc10d' before 'abc10d'
	 */
	public static final Comparator<String> NUMERIC_CASE_PRESERVING = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			final int t = StringComparators.compareToNumericIgnoreCase(a, b);
			return ((t == 0) ? StringComparators.compareToNumeric(a, b) : t);
			}
		};
	/**
	 * A case-insensitive comparator, 'abc10d' before 'Abc10d' before 'abc2d'
	 */
	public static final Comparator<String> REVERSE_NUMERIC_CASE_PRESERVING = new Comparator<String>()
		{
		public int compare(String a, String b)
			{
			final int t = StringComparators.compareToNumericIgnoreCase(b, a);
			return ((t == 0) ? StringComparators.compareToNumeric(b, a) : t);
			}
		};
	
	private StringComparators()
		{
		}
	
	/**
	 * Compare two Strings
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static int compareTo(String a, String b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return (a.compareTo(b));
		}
	
	/**
	 * Compare two Strings, ignoring case
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static int compareToIgnoreCase(String a, String b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return (a.compareToIgnoreCase(b));
		}
	
	/**
	 * Compare two Strings, detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static boolean equals(String a, String b)
		{
		return (compareTo(a, b) == 0);
		}
	
	/**
	 * Compare two Strings, ignoring case and detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static boolean equalsIgnoreCase(String a, String b)
		{
		return (compareToIgnoreCase(a, b) == 0);
		}
	
	/**
	 * Compare two Strings, detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static int compareToNumeric(String a, String b)
		{
		return (compareTo(a, b, false));
		}
	
	/**
	 * Compare two Strings, ignoring case and detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static int compareToNumericIgnoreCase(String a, String b)
		{
		return (compareTo(a, b, true));
		}
	
	/**
	 * Compare two Strings, detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static boolean equalsNumeric(String a, String b)
		{
		return (compareToNumeric(a, b) == 0);
		}
	
	/**
	 * Compare two Strings, ignoring case and detecting number sequences
	 * @param a A String
	 * @param b Another String
	 * @return Result
	 */
	public static boolean equalsNumericIgnoreCase(String a, String b)
		{
		return (compareToNumericIgnoreCase(a, b) == 0);
		}
	
	private static int compareTo(String a, String b, boolean ignoreCase)
		{
		if (a == b)
			return (0);
		if (a == null)
			return (-1);
		if (b == null)
			return (1);
		
		final int al = a.length();
		final int bl = b.length();
		
		int ia, ib;
		for (ia = 0, ib = 0; ia < al && ib < bl; ia++, ib++)
			{
			char ca = a.charAt(ia);
			char cb = b.charAt(ib);
			
			final boolean an = Character.isDigit(ca);
			final boolean bn = Character.isDigit(cb);
			
			if (an && bn)
				{
				int ai = 0;
				int na;
				for (na = 0; na < 9; na++)	// ai can hold at most 9 digits
					{
					ai = ai * 10 + Character.getNumericValue(ca);
					ia++;
					if (ia >= al)
						break;
					ca = a.charAt(ia);
					if (!Character.isDigit(ca))
						break;
					}
				
				int bi = 0;
				int nb;
				for (nb = 0; nb < 9; nb++)	// bi can hold at most 9 digits
					{
					bi = bi * 10 + Character.getNumericValue(cb);
					ib++;
					if (ib >= bl)
						break;
					cb = b.charAt(ib);
					if (!Character.isDigit(cb))
						break;
					}
				
				if (ai != bi)
					return (ai - bi);
				if (na != nb)
					return (nb - na);
				}
			
			if (ca == cb)
				continue;
			
			if (ignoreCase)
				{
				// FIXME: This will not detect equality between "ß" and "SS"
				final char cau = Character.toUpperCase(ca);
				final char cbu = Character.toUpperCase(cb);
				
				if (cau == cbu)
					continue;
				
				// This is needed, see String.compareToIgnoreCase
				if (Character.toLowerCase(ca) == Character.toLowerCase(cb))
					continue;
				
				return (cau - cbu);
				}
			
			return (ca - cb);
			}
		
		if (ia < al)
			return (1);
		if (ib < bl)
			return (-1);
		
		return (0);
		}
	}
