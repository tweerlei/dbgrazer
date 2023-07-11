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
package de.tweerlei.common5.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Helper methods for Java objects
 * 
 * @author Robert Wruck
 */
public final class ObjectUtils
	{
	// No instances
	private ObjectUtils()
		{
		}
	
	/**
	 * Null-safe equals that works for arrays
	 * @param a An object
	 * @param b Another object
	 * @return true if a is equal to b or both are null
	 */
	public static boolean equals(Object a, Object b)
		{
		if (a == b)
			return (true);
		if (a == null)
			return (false);
		if (b == null)
			return (false);
		if (a.getClass().isArray())
			return (arrayEquals(a, b));
		
		return (a.equals(b));
		}
	
	private static boolean arrayEquals(Object a, Object b)
		{
		final Class<?> t = a.getClass().getComponentType();
		
		if (t != b.getClass().getComponentType())
			return (false);
		
		if (t == byte.class)
			return (Arrays.equals((byte[]) a, (byte[]) b));
		if (t == short.class)
			return (Arrays.equals((short[]) a, (short[]) b));
		if (t == int.class)
			return (Arrays.equals((int[]) a, (int[]) b));
		if (t == long.class)
			return (Arrays.equals((long[]) a, (long[]) b));
		if (t == float.class)
			return (Arrays.equals((float[]) a, (float[]) b));
		if (t == double.class)
			return (Arrays.equals((double[]) a, (double[]) b));
		if (t == boolean.class)
			return (Arrays.equals((boolean[]) a, (boolean[]) b));
		if (t == char.class)
			return (Arrays.equals((char[]) a, (char[]) b));
		
		return (Arrays.deepEquals((Object[]) a, (Object[]) b));
		}
	
	/**
	 * Null-safe hashCode that works for arrays
	 * @param a An object
	 * @return The hashCode, 0 if the object was null
	 */
	public static int hashCode(Object a)
		{
		if (a == null)
			return (0);
		if (a.getClass().isArray())
			return (arrayHashCode(a));
		
		return (a.hashCode());
		}
	
	private static int arrayHashCode(Object a)
		{
		final Class<?> t = a.getClass().getComponentType();
		
		if (t == byte.class)
			return (Arrays.hashCode((byte[]) a));
		if (t == short.class)
			return (Arrays.hashCode((short[]) a));
		if (t == int.class)
			return (Arrays.hashCode((int[]) a));
		if (t == long.class)
			return (Arrays.hashCode((long[]) a));
		if (t == float.class)
			return (Arrays.hashCode((float[]) a));
		if (t == double.class)
			return (Arrays.hashCode((double[]) a));
		if (t == boolean.class)
			return (Arrays.hashCode((boolean[]) a));
		if (t == char.class)
			return (Arrays.hashCode((char[]) a));
		
		return (Arrays.deepHashCode((Object[]) a));
		}
	
	/**
	 * Null-safe toString that works for arrays
	 * @param a An object
	 * @return The string, "null" if the object was null
	 */
	public static String toString(Object a)
		{
		if (a == null)
			return (String.valueOf(a));
		if (a.getClass().isArray())
			return (arrayToString(a));
		
		return (a.toString());
		}
	
	private static String arrayToString(Object a)
		{
		final Class<?> t = a.getClass().getComponentType();
		
		if (t == byte.class)
			return (Arrays.toString((byte[]) a));
		if (t == short.class)
			return (Arrays.toString((short[]) a));
		if (t == int.class)
			return (Arrays.toString((int[]) a));
		if (t == long.class)
			return (Arrays.toString((long[]) a));
		if (t == float.class)
			return (Arrays.toString((float[]) a));
		if (t == double.class)
			return (Arrays.toString((double[]) a));
		if (t == boolean.class)
			return (Arrays.toString((boolean[]) a));
		if (t == char.class)
			return (Arrays.toString((char[]) a));
		
		return (Arrays.deepToString((Object[]) a));
		}
	
	/**
	 * Clone an object by invoking its public clone method.
	 * @param <T> Object type
	 * @param o Source object
	 * @return Cloned object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T o)
		{
		if (o == null)
			return (null);
		
		if (o.getClass().isArray())
			return ((T) cloneArray(o));
		
		try	{
			final Method m = o.getClass().getMethod("clone");
			
			try {
				final T ret = (T) m.invoke(o);
				
				return (ret);
				}
			catch (InvocationTargetException e)
				{
				throw new RuntimeException("Can't invoke clone() on " + o.getClass().getName(), e.getCause());
				}
			catch (IllegalAccessException e)
				{
				throw new RuntimeException("Can't invoke clone() on " + o.getClass().getName(), e);
				}
			}
		catch (NoSuchMethodException e)
			{
			throw new IllegalArgumentException("No public clone() method for " + o.getClass().getName());
			}
		}
	
	private static Object cloneArray(Object a)
		{
		final int l = Array.getLength(a);
		final Object ret = newInstance(a);
		
		System.arraycopy(a, 0, ret, 0, l);
		
		return (ret);
/* Arrays.copyOf() is JDK 1.6
		if (t == byte.class)
			return (Arrays.copyOf((byte[]) a, l));
		if (t == short.class)
			return (Arrays.copyOf((short[]) a, l));
		if (t == int.class)
			return (Arrays.copyOf((int[]) a, l));
		if (t == long.class)
			return (Arrays.copyOf((long[]) a, l));
		if (t == float.class)
			return (Arrays.copyOf((float[]) a, l));
		if (t == double.class)
			return (Arrays.copyOf((double[]) a, l));
		if (t == boolean.class)
			return (Arrays.copyOf((boolean[]) a, l));
		if (t == char.class)
			return (Arrays.copyOf((char[]) a, l));
		
		return (Arrays.copyOf((Object[]) a, l));
*/
		}
	
	/**
	 * Create a new instance of an Object's class
	 * @param <T> Object type
	 * @param o An object
	 * @return The new instance
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T newInstance(T o)
		{
		if (o == null)
			return (null);
		
		if (o.getClass().isArray())
			{
			final int l = Array.getLength(o);
			final Class<?> t = o.getClass().getComponentType();
			return ((T) Array.newInstance(t, l));
			}
		if (o.getClass().isEnum())
			return (o);
		
		// Special case for EnumMap and EnumSet that don't have default constructors
		if (o instanceof EnumMap)
			{
			final EnumMap<?, ?> tmp = ((EnumMap<?, ?>) o).clone();
			tmp.clear();
			return ((T) tmp);
			}
		if (o instanceof EnumSet)
			{
			final EnumSet<?> tmp = ((EnumSet<?>) o).clone();
			tmp.clear();
			return ((T) tmp);
			}
		
		try	{
			return (T) o.getClass().newInstance();
			}
		catch (IllegalAccessException e)
			{
			throw new RuntimeException("Can't create an instance of " + o.getClass().getName(), e);
			}
		catch (InstantiationException e)
			{
			throw new RuntimeException("Can't create an instance of " + o.getClass().getName(), e);
			}
		}
	
	/**
	 * Get the qualified name of an enum constant
	 * @param e Enum constant
	 * @return Name
	 * @throws NullPointerException if e is null
	 */
	public static String getQualifiedName(Enum<?> e)
		{
		return (e.getDeclaringClass().getName() + "." + e.name());
		}
	}
