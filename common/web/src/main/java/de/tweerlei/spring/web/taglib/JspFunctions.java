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
package de.tweerlei.spring.web.taglib;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.spring.service.StringTransformerService;

/**
 * Custom JSP tags
 * 
 * @author Robert Wruck
 */
@Service("springToolsJspFunctions")
public class JspFunctions
	{
	private static final String HTML_BR = "<br/>";
	
	// Hack to make the StringTransformerService available to static JSP calls
	private static StringTransformerService stringTransformerService;
	
	/**
	 * Constructor
	 * @param stf StringTransformerService
	 */
	@Autowired
	public JspFunctions(StringTransformerService stf)
		{
		stringTransformerService = stf;
		}
	
	/**
	 * Get the type name of an object
	 * @param o Object
	 * @return Type name
	 */
	public static String getTypeName(Object o)
		{
		if (o == null)
			return ("null");
		
		return (o.getClass().getName());
		}
	
	/**
	 * Get the qualified name of an enum constant
	 * @param e Enum constant
	 * @return Name
	 * @throws NullPointerException if e is null
	 */
	public static String getQualifiedName(Enum<?> e)
		{
		if (e == null)
			return (null);
		
		return (e.getDeclaringClass().getName().replace('$', '.') + "." + e.name());
		}
	
	/**
	 * toString that never throws a RuntimeException
	 * @param o Object
	 * @return String representation
	 */
	public static String toString(Object o)
		{
		if (o == null)
			return (null);
		
		try	{
			return (o.toString());
			}
		catch (RuntimeException e)
			{
			return (null);
			}
		}
	
	/**
	 * Convert a Number to an Integer
	 * @param n Number
	 * @return Integer
	 */
	public static Integer toInt(Number n)
		{
		if (n == null)
			return (null);
		
		return (n.intValue());
		}
	
    /**
     * Check whether a collection contains an element
     * @param c Collection
     * @param o Element
     * @return true if so
     */
    public static boolean contains(Collection<?> c, Object o)
    	{
    	if (c == null)
    		return (false);
    	
    	return (c.contains(o));
    	}
    
    /**
     * Check whether a map contains a key
     * @param c Map
     * @param o Element
     * @return true if so
     */
    public static boolean containsKey(Map<?, ?> c, Object o)
    	{
    	if (c == null)
    		return (false);
    	
    	return (c.containsKey(o));
    	}
    
    /**
     * Check whether a map contains a value
     * @param c Map
     * @param o Element
     * @return true if so
     */
    public static boolean containsValue(Map<?, ?> c, Object o)
    	{
    	if (c == null)
    		return (false);
    	
    	return (c.containsValue(o));
    	}
    
	/**
	 * Replace newlines with a break tag
	 * @param s String
	 * @param br Break tag
	 * @return Wrapped string
	 */
	public static String nlEncode(String s, String br)
		{
		if (s == null)
			return (null);
		
		return (s.replaceAll("\\r?\\n", br));
		}
	
	/**
	 * Replace newlines with a break tag
	 * @param s String
	 * @return Wrapped string
	 */
	public static String jsEncode(String s)
		{
		if (s == null)
			return (null);
		
		final StringBuilder sb = new StringBuilder();
		for (int i = 0, n = s.length(); i < n; i++)
			{
			final char c = s.charAt(i);
			switch (c)
				{
				case '\\':
					sb.append("\\\\");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '"':
					sb.append("\\\"");
					break;
				case '\'':
					sb.append("\\'");
					break;
				default:
					sb.append(c);
					break;
				}
			}
		
		return (sb.toString());
		}
	
	/**
	 * Encode a String for usage as URL parameter.
	 * Not suitable for encoding whole URLs.
	 * @param s Parameter string
	 * @return Encoded String
	 */
	public static String urlEncode(String s)
		{
		return (stringTransformerService.toURL(s));
		}
	
	/**
	 * Replace newlines with br tags
	 * @param s String
	 * @return Wrapped string
	 */
	public static String nlEncode(String s)
		{
		return (nlEncode(s, HTML_BR));
		}
	
	/**
	 * Wrap a String to a given maximum width
	 * @param s String
	 * @param width Max. width
	 * @param maxLines Max. number of lines
	 * @param eol End of line marker to break lines
	 * @return Wrapped string
	 */
	public static String wordWrap(String s, int width, int maxLines, String eol)
		{
		if ((s == null) || (width <= 0))
			return (s);
		
		final StringBuilder sb = new StringBuilder();
		int col = 0;
		int row = 0;
		
		outer: for (String word : s.split("\\s+"))
			{
			final int l = word.length();
			if (col > 0)
				{
				if (col + 1 + l <= width)
					{
					sb.append(" ");
					sb.append(word);
					col += l + 1;
					continue;
					}
				if (++row >= maxLines)
					break outer;
				sb.append(eol);
				col = 0;
				}
			
			int pos = 0;
			while (l - pos > width)
				{
				sb.append(word.substring(pos, pos + width));
				if (++row >= maxLines)
					break outer;
				sb.append(eol);
				pos += width;
				}
			sb.append(word.substring(pos));
			col = l - pos;
			}
		
		return (sb.toString());
		}
	
	/**
	 * Wrap a String to a given maximum width
	 * @param s String
	 * @param width Max. width
	 * @param maxLines Max. number of lines
	 * @return Wrapped string
	 */
	public static String wordWrap(String s, int width, int maxLines)
		{
		return (wordWrap(s, width, maxLines, HTML_BR));
		}
	
	/**
	 * Wrap a String to a given maximum width
	 * @param s String
	 * @param width Max. width
	 * @return Wrapped string
	 */
	public static String wordWrap(String s, int width)
		{
		return (wordWrap(s, width, Integer.MAX_VALUE, HTML_BR));
		}
	}
