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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import de.tweerlei.common.contract.ContractProof;

/**
 * \addtogroup misc Verschiedenes
 * @{
 */

/**
 * Hilfsroutinen für Klassen
 * 
 * @author Robert Wruck
 */
public final class ClassUtils
	{
	/** Unerreichbarer Konstruktor */
	private ClassUtils()
		{
		// s.o.
		}
	
	/**
	 * Ermittelt, ob die Klasse existiert
	 * @param name Name der Klasse
	 * @return true oder false
	 */
	public static final boolean exists(String name)
		{
		try	{
			Class.forName(name);
			return (true);
			}
		catch (ClassNotFoundException e)
			{
			return (false);
			}
		}
	
	/**
	 * Erzeugt eine Instanz der angegebenen Klasse
	 * @param cl Klasse
	 * @param types Typen der Konstruktorargumente
	 * @param args Argumente
	 * @return Die Instanz
	 * @throws InstantiationException Wenn die Klasse nicht public ist
	 * @throws NoSuchMethodException Wenn kein passender Konstruktor gefunden werden kann
	 * @throws IllegalAccessException Wenn der Konstruktor nicht public ist
	 * @throws InvocationTargetException Wenn der Konstruktor eine Exception wirft
	 */
	public static final Object instantiate(Class cl, Class[] types, Object[] args) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
		{
		if (types != null)
			ContractProof.invariant("types.length == args.length", (args != null) && (types.length == args.length));
		
		if ((types == null) || (types.length == 0))
			return (cl.newInstance());
		
		final Constructor ctor = cl.getConstructor(types);
		return (ctor.newInstance(args));
		}
	
	/**
	 * Erzeugt eine Instanz der angegebenen Klasse
	 * @param name Name der Klasse
	 * @param types Typen der Konstruktorargumente
	 * @param args Argumente
	 * @return Die Instanz
	 * @throws ClassNotFoundException Wenn die Klasse nicht existiert
	 * @throws InstantiationException Wenn die Klasse nicht public ist
	 * @throws NoSuchMethodException Wenn kein passender Konstruktor gefunden werden kann
	 * @throws IllegalAccessException Wenn der Konstruktor nicht public ist
	 * @throws InvocationTargetException Wenn der Konstruktor eine Exception wirft
	 */
	public static final Object instantiate(String name, Class[] types, Object[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException
		{
		return (ClassUtils.instantiate(Class.forName(name), types, args));
		}
	
	/**
	 * Liefert die Instanz eines Singletons durch Aufrufen von getInstance()
	 * @param cl Klasse
	 * @return Die Instanz
	 * @throws SecurityException Wenn die Methode 'getInstance' nicht erreichbar ist
	 * @throws NoSuchMethodException Wenn die Methode 'getInstance' nicht existiert
	 * @throws IllegalAccessException Wenn die Methode 'getInstance' nicht public ist
	 * @throws InvocationTargetException Wenn die Methode 'getInstance' eine Exception wirft
	 * @throws IllegalArgumentException Kommt nicht vor
	 */
	public static final Object instantiateSingleton(Class cl) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
		{
		final Method gi = cl.getMethod("getInstance", null);
		
		return (gi.invoke(null, null));
		}
	
	/**
	 * Liefert die Instanz eines Singletons durch Aufrufen von getInstance()
	 * @param name Klassenname
	 * @return Die Instanz
	 * @throws ClassNotFoundException Wenn die Klasse nicht existiert
	 * @throws SecurityException Wenn die Methode 'getInstance' nicht erreichbar ist
	 * @throws NoSuchMethodException Wenn die Methode 'getInstance' nicht existiert
	 * @throws IllegalAccessException Wenn die Methode 'getInstance' nicht public ist
	 * @throws InvocationTargetException Wenn die Methode 'getInstance' eine Exception wirft
	 * @throws IllegalArgumentException Kommt nicht vor
	 */
	public static final Object instantiateSingleton(String name) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
		{
		return (ClassUtils.instantiateSingleton(Class.forName(name)));
		}
	
	/**
	 * Get the URL a class was loaded from
	 * @param c Class
	 * @return URL
	 */
	public static URL getURLForClass(Class c)
		{
		if (c == null)
			return (null);
		
		return (c.getResource("/" + c.getName().replace('.', '/') + ".class"));
		}
	}

/** @} */
