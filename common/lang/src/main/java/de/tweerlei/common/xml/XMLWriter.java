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
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import de.tweerlei.common.codec.Base64Codec;
import de.tweerlei.common.codec.HexCodec;

/**
 * Schreibt XML-Daten
 * 
 * @author Robert Wruck
 */
public class XMLWriter
	{
	private static final String DATE_FORMAT = "yyyy-MM-dd'Z'";
	private static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private final Writer writer;
	
	/**
	 * Konvertiert ein boolean in xsd:boolean
	 * @param b boolean
	 * @return String
	 */
	public static String printBoolean(boolean b)
		{
		return (b ? "true" : "false");
		}
	
	/**
	 * Konvertiert ein double in xsd:decimal
	 * @param d double
	 * @return String
	 */
	public static String printDecimal(double d)
		{
		return (new Double(d).toString());
		}
	
	/**
	 * Konvertiert ein long in xsd:integer
	 * @param l long
	 * @return String
	 */
	public static String printInteger(long l)
		{
		return (new Long(l).toString());
		}
	
	/**
	 * Konvertiert ein Datum in xsd:date
	 * @param d Date
	 * @return String
	 */
	public static String printDate(Date d)
		{
		final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return (df.format(d));
		}
	
	/**
	 * Konvertiert ein Datum in xsd:dateTime
	 * @param d Date
	 * @return String
	 */
	public static String printDateTime(Date d)
		{
		final DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return (df.format(d));
		}
	
	/**
	 * Konvertiert Bytes in xsd:hexBinary
	 * @param b Bytes
	 * @return String
	 */
	public static String printHexBinary(byte[] b)
		{
		final HexCodec hc = new HexCodec();
		try	{
			return (hc.encode(b));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	/**
	 * Konvertiert Bytes in xsd:base64Binary
	 * @param b Bytes
	 * @return String
	 */
	public static String printBase64Binary(byte[] b)
		{
		final Base64Codec hc = new Base64Codec();
		try	{
			return (hc.encode(b));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	/**
	 * Codiert Sonderzeichen
	 * @param s String
	 * @return XML-codierter String
	 */
	public static String printString(String s)
		{
		return (s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		}
	
	/**
	 * Konstruktor
	 * @param w Writer für Ausgabe
	 */
	public XMLWriter(Writer w)
		{
		writer = w;
		}
	
	/**
	 * Schließt den Writer
	 * @throws IOException bei Fehlern
	 */
	public void close() throws IOException
		{
		writer.close();
		}
	
	/**
	 * Schreibt die XML-Deklaration
	 * @param encoding Encoding
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeXMLDeclaration(String encoding) throws IOException
		{
		return (writeXMLDeclaration(encoding, false));
		}
	
	/**
	 * Schreibt die XML-Deklaration
	 * @param encoding Encoding
	 * @param standalone true, wenn standalone
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeXMLDeclaration(String encoding, boolean standalone) throws IOException
		{
		writer.write("<?xml version=\"1.0\"");
		if (encoding != null)
			{
			writer.write(" encoding=\"");
			writer.write(encoding);
			writer.write("\"");
			}
		if (standalone)
			writer.write(" standalone=\"yes\"");
		writer.write("?>\n");
		
		return (this);
		}
	
	private String attrEncode(String data)
		{
		return (data.replaceAll("&", "&amp;").replaceAll("\"", "&quot;"));
		}
	
	/**
	 * Öffnet ein Element
	 * @param name Elementname
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter startElement(String name) throws IOException
		{
		return (startElement(name, null, false));
		}
	
	/**
	 * Öffnet ein Element
	 * @param name Elementname
	 * @param attrs Attribute
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter startElement(String name, Map attrs) throws IOException
		{
		return (startElement(name, attrs, false));
		}
	
	/**
	 * Öffnet ein Element
	 * @param name Elementname
	 * @param attrs Attribute
	 * @param empty Element ist leer
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter startElement(String name, Map attrs, boolean empty) throws IOException
		{
		writer.write("<");
		writer.write(name);
		if (attrs != null)
			{
			for (Iterator i = attrs.entrySet().iterator(); i.hasNext(); )
				{
				final Map.Entry ent = (Map.Entry) i.next();
				writer.write(" ");
				writer.write(ent.getKey().toString());
				writer.write("=\"");
				writer.write(attrEncode(ent.getValue().toString()));
				writer.write("\"");
				}
			}
		if (empty)
			writer.write("/>");
		else
			writer.write(">");
		
		return (this);
		}
	
	/**
	 * Schließt ein Element
	 * @param name Elementname
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter endElement(String name) throws IOException
		{
		writer.write("</");
		writer.write(name);
		writer.write(">");
		
		return (this);
		}
	
	/**
	 * Schreibt ein vollständiges Element
	 * @param name Elementname
	 * @param attrs Attribute
	 * @param content Inhalt
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeElement(String name, Map attrs, String content) throws IOException
		{
		if (content == null)
			startElement(name, attrs, true);
		else
			{
			startElement(name, attrs, false);
			writeEncoded(content);
			endElement(name);
			}
		
		return (this);
		}
	
	/**
	 * Schreibt ein vollständiges Element
	 * @param name Elementname
	 * @param attrs Attribute
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeElement(String name, Map attrs) throws IOException
		{
		return (writeElement(name, attrs, null));
		}
	
	/**
	 * Schreibt ein vollständiges Element
	 * @param name Elementname
	 * @param content Inhalt
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeElement(String name, String content) throws IOException
		{
		return (writeElement(name, null, content));
		}
	
	/**
	 * Schreibt Text (XML-codiert)
	 * @param s Text
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeEncoded(String s) throws IOException
		{
		writer.write(printString(s));
		
		return (this);
		}
	
	/**
	 * Schreibt Text (nicht XML-codiert)
	 * @param s Text
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeText(String s) throws IOException
		{
		writer.write(s);
		
		return (this);
		}
	
	/**
	 * Schreibt einen Zeilenumbruch
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeln() throws IOException
		{
		writer.write("\n");
		
		return (this);
		}
	
	/**
	 * Schreibt Tabs
	 * @param n Anzahl Tabs
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter indent(int n) throws IOException
		{
		for (int i = 0; i < n; i++)
			writer.write('\t');
		
		return (this);
		}
	
	/**
	 * Schreibt einen Kommentar (nicht XML-codiert)
	 * @param s Kommentar
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeComment(String s) throws IOException
		{
		writer.write("<!--");
		writer.write(s);
		writer.write("-->");
		
		return (this);
		}
	
	/**
	 * Schreibt einen CDATA-Block
	 * @param data Inhalt
	 * @return this
	 * @throws IOException bei Fehlern
	 */
	public XMLWriter writeCDATASection(String data) throws IOException
		{
		writer.write("<![CDATA[");
		writer.write(data);
		writer.write("]]>");
		
		return (this);
		}
	}
