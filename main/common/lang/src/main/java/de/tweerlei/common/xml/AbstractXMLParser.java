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
package de.tweerlei.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tweerlei.common.codec.Base64Codec;
import de.tweerlei.common.codec.HexCodec;

/**
 * Basisklasse für SAX-Parser
 * 
 * @author Robert Wruck
 */
public abstract class AbstractXMLParser extends DefaultHandler
	{
	private static final Pattern DATETIME_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})(T([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}))?(Z|[-+][0-9]{1,2}:[0-9]{1,2})?");
	
	private final boolean nsAware;
	private final boolean validating;
	
	/**
	 * Parst ein xsd:boolean
	 * @param s Wert
	 * @return boolean
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static boolean parseBoolean(String s)
		{
		if (s != null)
			{
			if (s.equals("true") || s.equals("1"))
				return (true);
			if (s.equals("false") || s.equals("0"))
				return (false);
			}
		
		throw new RuntimeException("Invalid boolean value: " + s);
		}
	
	/**
	 * Parst ein xsd:decimal
	 * @param s Wert
	 * @return long
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static double parseDecimal(String s)
		{
		if (s != null)
			{
			return (Double.parseDouble(s));
			}
		
		throw new RuntimeException("Invalid decimal value: " + s);
		}
	
	/**
	 * Parst ein xsd:integer
	 * @param s Wert
	 * @return long
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static long parseInteger(String s)
		{
		if (s != null)
			{
			return (Long.parseLong(s));
			}
		
		throw new RuntimeException("Invalid integer value: " + s);
		}
	
	/**
	 * Parst ein xsd:date oder xsd:dateTime
	 * @param s Wert
	 * @return Date
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static Date parseDate(String s)
		{
		if (s != null)
			{
			final Matcher m = DATETIME_PATTERN.matcher(s);
			if (m.matches())
				{
				final Calendar c = Calendar.getInstance();
				c.setLenient(false);
				
				if (m.group(8) != null) {
					if (m.group(8).equals("Z")) {
						c.setTimeZone(TimeZone.getTimeZone("GMT"));
					} else {
						c.setTimeZone(TimeZone.getTimeZone("GMT" + m.group(8)));
					}
				}
				
				c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
				c.set(Calendar.MONTH, Calendar.JANUARY + Integer.parseInt(m.group(2)) - 1);
				c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)));
				if (m.group(4) != null) {
					c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(5)));
					c.set(Calendar.MINUTE, Integer.parseInt(m.group(6)));
					c.set(Calendar.SECOND, Integer.parseInt(m.group(7)));
				} else {
					c.set(Calendar.HOUR_OF_DAY, 0);
					c.set(Calendar.MINUTE, 0);
					c.set(Calendar.SECOND, 0);
				}
				c.set(Calendar.MILLISECOND, 0);
				
				return (c.getTime());
				}
			}
		
		throw new RuntimeException("Invalid date or dateTime value: " + s);
		}
	
	/**
	 * Parst ein xsd:hexBinary
	 * @param s Wert
	 * @return Bytes
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static byte[] parseHexBinary(String s)
		{
		if (s != null)
			{
			final HexCodec hc = new HexCodec();
			try	{
				return (hc.decode(s));
				}
			catch (IOException e)
				{
				// Fall through to throw
				}
			}
		
		throw new RuntimeException("Invalid hexBinary value: " + s);
		}
	
	/**
	 * Parst ein xsd:base64Binary
	 * @param s Wert
	 * @return Bytes
	 * @throws RuntimeException, wenn der Wert ungültig ist
	 */
	public static byte[] parseBase64Binary(String s)
		{
		if (s != null)
			{
			final Base64Codec hc = new Base64Codec();
			try	{
				return (hc.decode(s));
				}
			catch (IOException e)
				{
				// Fall through to throw
				}
			}
		
		throw new RuntimeException("Invalid boolean value: " + s);
		}
	
	/**
	 * Konstruktor
	 * @param useNS Namespaces berücksichtigen
	 * @param validate Validieren
	 */
	public AbstractXMLParser(boolean useNS, boolean validate)
		{
		nsAware = useNS;
		validating = validate;
		}
	
	/**
	 * Parst einen Datenstrom
	 * @param s Datenstrom
	 * @throws SAXException bei Fehlern
	 */
	public void parse(InputStream s) throws SAXException
		{
		try	{
			final SAXParserFactory f = SAXParserFactory.newInstance();
			f.setNamespaceAware(nsAware);
			f.setValidating(validating);
			final SAXParser p = f.newSAXParser();
			p.parse(s, this);
			}
		catch (IOException e)
			{
			throw new SAXException(e);
			}
		catch (ParserConfigurationException e)
			{
			throw new SAXException(e);
			}
		}
	}
