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
package de.tweerlei.dbgrazer.common.file.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;

/**
 * Parser for SCM history outputs
 * 
 * @author Robert Wruck
 */
public class HistoryParser implements StreamReader
	{
	private static final class HistoryHandler extends DefaultHandler
		{
		private final List<HistoryEntry> history;
		private StringBuilder sb;
		private String revision;
		private String date;
		private String msg;
		
		public HistoryHandler(List<HistoryEntry> history)
			{
			this.history = history;
			}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
			{
			if (qName.equals("logentry"))
				revision = attributes.getValue("revision");
			else if (qName.equals("date"))
				sb = new StringBuilder();
			else if (qName.equals("msg"))
				sb = new StringBuilder();
			}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
			{
			if (qName.equals("logentry"))
				{
				if (revision != null && date != null && msg != null)
					{
					final Date d = parseDate(date);
					if (d != null)
						history.add(new HistoryEntry(revision, d, msg));
					}
				revision = null;
				date = null;
				msg = null;
				}
			else if (qName.equals("date"))
				{
				date = sb.toString();
				sb = null;
				}
			else if (qName.equals("msg"))
				{
				msg = sb.toString();
				sb = null;
				}
			}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
			{
			if (sb != null)
				sb.append(ch, start, length);
			}
		
		private Date parseDate(String d)
			{
			if (d == null)
				return (null);
			
			if (d.length() >= 25)
				{
				try	{
					return (parseDate(d.substring(0, 25), "yyyy-MM-dd HH:mm:ss Z", null));
					}
				catch (ParseException e)
					{
					// ignore
					}
				}
			
			if (d.length() >= 23)
				{
				try	{
					return (parseDate(d.substring(0, 23), "yyyy-MM-dd'T'HH:mm:ss.SSS", TimeZone.getTimeZone("GMT")));
					}
				catch (ParseException e)
					{
					// ignore
					}
				}
			
			return (null);
			}
		
		private Date parseDate(String s, String format, TimeZone zone) throws ParseException
			{
			final SimpleDateFormat df = new SimpleDateFormat(format);
			df.setLenient(false);
			if (zone != null)
				df.setTimeZone(zone);
			return (df.parse(s));
			}
		}
	
	private final SAXParserFactory factory;
	private final List<HistoryEntry> history;
	
	/**
	 * Constructor
	 */
	public HistoryParser()
		{
		this.factory = SAXParserFactory.newInstance();
		this.history = new LinkedList<HistoryEntry>();
		}
	
	/**
	 * Get the parsed history
	 * @return History entries
	 */
	public List<HistoryEntry> getHistory()
		{
		return (history);
		}
	
	@Override
	public void read(InputStream stream) throws IOException
		{
		try	{
			final SAXParser parser = factory.newSAXParser();
			parser.parse(stream, new HistoryHandler(history));
			}
		catch (ParserConfigurationException e)
			{
			throw new IOException(e);
			}
		catch (SAXException e)
			{
			throw new IOException(e);
			}
		}
	}
