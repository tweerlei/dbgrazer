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
package de.tweerlei.ermtools.tns;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.common.util.StringUtils;

/**
 * Helper methods for dealing with TNSNAMES.ORA
 * 
 * @author Robert Wruck
 */
public class TNSNamesHelper
	{
	private static final String THIN_URL_PREFIX = "jdbc:oracle:thin:@";
	private static final Pattern PAT_SERVICE_NAME = Pattern.compile(THIN_URL_PREFIX + "([^:/]+)?(:([^:/]+))?(/([^:/]+))?");
	private static final Pattern PAT_SID = Pattern.compile(THIN_URL_PREFIX + "([^:/]+)?(:([^:/]+))?(:([^:/]+))?");
	
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final String ADDRESS_LIST = "ADDRESS_LIST";
	private static final String ADDRESS = "ADDRESS";
	private static final String CONNECT_DATA = "CONNECT_DATA";
	private static final String PROTOCOL = "PROTOCOL";
	private static final String HOST = "HOST";
	private static final String PORT = "PORT";
	private static final String SERVER = "SERVER";
	private static final String SERVICE_NAME = "SERVICE_NAME";
	private static final String SID = "SID";
	
	/**
	 * Generate a JDBC URL from a TNSNAMES.ORA entry
	 * @param tnsEntry TNSNames entry
	 * @return JDBC URL
	 */
	public static String getJdbcURL(String tnsEntry)
		{
		final TNSNamesParser p = new TNSNamesParser();
		final Map<String, Object> map;
		try	{
			map = p.parse(new StringReader(tnsEntry));
			}
		catch (IOException e)
			{
			throw new IllegalArgumentException(e.getMessage());
			}
		
		if (map.isEmpty())
			throw new IllegalArgumentException("Empty TNS entry");
		
		@SuppressWarnings("unchecked")
		final Map<String, Object> entry = (Map<String, Object>) map.values().iterator().next();
		
		return (getJdbcURL(entry));
		}
	
	/**
	 * Generate a JDBC URL from a TNSNAMES.ORA entry as returned by the TNSNamesParser
	 * @param tnsEntry TNSNames entry
	 * @return JDBC URL
	 */
	@SuppressWarnings("unchecked")
	public static String getJdbcURL(Map<String, Object> tnsEntry)
		{
		if (tnsEntry == null)
			throw new IllegalArgumentException("Empty TNS entry");
		
		final Map<String, Object> description = (Map<String, Object>) tnsEntry.get(DESCRIPTION);
		if (description == null)
			throw new IllegalArgumentException("Missing " + DESCRIPTION);
		
		final Map<String, Object> addressList = (Map<String, Object>) description.get(ADDRESS_LIST);
		final Map<String, Object> address;
		if (addressList == null)
			address = (Map<String, Object>) description.get(ADDRESS);
		else
			address = (Map<String, Object>) addressList.get(ADDRESS);
		if (address == null)
			throw new IllegalArgumentException("Missing " + ADDRESS);
		
		final Map<String, Object> connectData = (Map<String, Object>) description.get(CONNECT_DATA);
		if (connectData == null)
			throw new IllegalArgumentException("Missing " + CONNECT_DATA);
		
		final Object host = address.get(HOST);
		final Object port = address.get(PORT);
		final Object serviceName = connectData.get(SERVICE_NAME);
		final Object sid = connectData.get(SID);
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(THIN_URL_PREFIX);
		if (host != null)
			sb.append(host);
		if (port != null)
			{
			sb.append(":");
			sb.append(port);
			}
		if (serviceName != null)
			{
			sb.append("/");
			sb.append(serviceName);
			}
		else if (sid != null)
			{
			sb.append(":");
			sb.append(sid);
			}
		else
			throw new IllegalArgumentException("Missing " + SERVICE_NAME + " or " + SID);
		
		return (sb.toString());
		}
	
	/**
	 * Generate a TNSNAMES.ORA entry from a JDBC URL
	 * @param url JDBC URL
	 * @return TNSNames entry
	 */
	public static Map<String, Object> getTNSEntry(String url)
		{
		final String host;
		final String port;
		final String serviceName;
		final String sid;
		
		final Matcher m = PAT_SERVICE_NAME.matcher(url);
		if (m.matches())
			{
			host = m.group(1);
			port = m.group(3);
			serviceName = m.group(5);
			sid = null;
			}
		else
			{
			final Matcher m2 = PAT_SID.matcher(url);
			if (m2.matches())
				{
				host = m2.group(1);
				port = m2.group(3);
				serviceName = null;
				sid = m2.group(5);
				}
			else
				throw new IllegalArgumentException("JDBC URL " + url);
			}
		
		final Map<String, Object> connectData = new TreeMap<String, Object>();
		connectData.put(SERVER, "DEDICATED");
		if (!StringUtils.empty(serviceName))
			connectData.put(SERVICE_NAME, serviceName);
		else if (!StringUtils.empty(sid))
			connectData.put(SID, sid);
		
		final Map<String, Object> address = new TreeMap<String, Object>();
		address.put(PROTOCOL, "TCP");
		if (!StringUtils.empty(host))
			address.put(HOST, host);
		if (!StringUtils.empty(port))
		address.put(PORT, port);
		
		final Map<String, Object> description = new TreeMap<String, Object>();
		description.put(ADDRESS, address);
		description.put(CONNECT_DATA, connectData);
		
		final Map<String, Object> entry = new TreeMap<String, Object>();
		entry.put(DESCRIPTION, description);
		
		return (entry);
		}
	
	/**
	 * Generate a TNSNAMES.ORA entry from a JDBC URL
	 * @param name TNS entry name
	 * @param url JDBC URL
	 * @return TNSNames entry
	 */
	public static String getTNSName(String name, String url)
		{
		final StringWriter sw = new StringWriter();
		final TNSNamesWriter w = new TNSNamesWriter(sw, true);
		
		try	{
			w.writeEntry(name, getTNSEntry(url));
			}
		catch (IOException e)
			{
			throw new IllegalArgumentException(e.getMessage());
			}
		
		return (sw.toString());
		}
	}
