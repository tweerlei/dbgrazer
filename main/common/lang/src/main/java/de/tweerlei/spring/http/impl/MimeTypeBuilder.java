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
package de.tweerlei.spring.http.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.spring.http.MimeType;

/**
 * Helper class for parsing MIME types
 * 
 * @author Robert Wruck
 */
public class MimeTypeBuilder implements MimeType, Serializable
	{
	private static final String TYPE_SEPARATOR = "/";
	private static final String PARAM_SEPARATOR = ";";
	private static final String VALUE_SEPARATOR = "=";
	
	private String type;
	private String subtype;
	private final Map<String, String> params;
	private final Map<String, String> paramsView;
	
	private static String normalize(String s)
		{
		return ((s == null) ? null : s.toLowerCase());
		}
	
	/**
	 * Parse a MimeType from a String
	 * @param s String
	 * @return MimeType
	 */
	public static MimeTypeBuilder parse(String s)
		{
		if (s == null)
			throw new IllegalArgumentException("Empty type");
		
		final String[] parts = s.split(PARAM_SEPARATOR);
		final String[] types = parts[0].split(TYPE_SEPARATOR);
		if (types.length != 2)
			throw new IllegalArgumentException("Missing subtype");
		
		final String type = types[0].trim();
		final String subtype = types[1].trim();
		if (type.length() == 0)
			throw new IllegalArgumentException("Empty type");
		if (subtype.length() == 0)
			throw new IllegalArgumentException("Empty subtype");
		
		final Map<String, String> params = new HashMap<String, String>();
		for (int i = 1; i < parts.length; i++)
			{
			final String[] kv = parts[i].split(VALUE_SEPARATOR);
			if (kv.length != 2)
				throw new IllegalArgumentException("Missing parameter value");
			
			final String key = kv[0].trim();
			final String value = kv[1].trim();
			if (key.length() == 0)
				throw new IllegalArgumentException("Empty parameter key");
			if (value.length() == 0)
				throw new IllegalArgumentException("Empty parameter value");
			
			// TODO: Support quoted values
			params.put(key, value);
			}
		
		return (new MimeTypeBuilder(type, subtype, params));
		}
	
	/**
	 * Constructor
	 * @param type Type
	 * @param subtype Subtype
	 * @param params Parameters
	 */
	public MimeTypeBuilder(String type, String subtype, Map<String, String> params)
		{
		this.params = new HashMap<String, String>();
		this.paramsView = Collections.unmodifiableMap(this.params);
		
		setType(type);
		setSubtype(subtype);
		if (params != null)
			{
			for (Map.Entry<String, String> ent : params.entrySet())
				setParam(ent.getKey(), ent.getValue());
			}
		}
	
	/**
	 * Constructor
	 * @param type Type
	 * @param subtype Subtype
	 */
	public MimeTypeBuilder(String type, String subtype)
		{
		this(type, subtype, null);
		}
	
	/**
	 * Constructor
	 */
	public MimeTypeBuilder()
		{
		this(null, null, null);
		}
	
	public String getType()
		{
		return type;
		}
	
	public String getSubtype()
		{
		return subtype;
		}
	
	public String getMediaType()
		{
		return (type + TYPE_SEPARATOR + subtype);
		}
	
	public Map<String, String> getParams()
		{
		return paramsView;
		}
	
	public String getParam(String key)
		{
		return params.get(normalize(key));
		}
	
	/**
	 * Set the type
	 * @param type the type to set
	 * @return this
	 */
	public MimeTypeBuilder setType(String type)
		{
		this.type = normalize(type);
		return (this);
		}
	
	/**
	 * Set the subtype
	 * @param subtype the subtype to set
	 * @return this
	 */
	public MimeTypeBuilder setSubtype(String subtype)
		{
		this.subtype = normalize(subtype);
		return (this);
		}
	
	/**
	 * Set a parameter
	 * @param key Key
	 * @param value Value
	 * @return this
	 */
	public MimeTypeBuilder setParam(String key, String value)
		{
		if (StringUtils.empty(value))
			this.params.remove(normalize(key));
		else
			this.params.put(normalize(key), value);
		return (this);
		}
	
	/**
	 * Get the created MimeType
	 * @return this
	 */
	public MimeType build()
		{
		return (this);
		}
	
	@Override
	public int hashCode()
		{
		return (type.hashCode() ^ subtype.hashCode() ^ params.hashCode());
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == null)
			return (false);
		if (o == this)
			return (true);
		if (!(o instanceof MimeType))
			return (false);
		
		final MimeType casted = (MimeType) o;
		return (type.equals(casted.getType()) && subtype.equals(casted.getSubtype()) && params.equals(casted.getParams()));
		}
	
	@Override
	public String toString()
		{
		final StringBuilder sb = new StringBuilder();
		
		sb.append(type);
		sb.append(TYPE_SEPARATOR);
		sb.append(subtype);
		for (Map.Entry<String, String> ent : params.entrySet())
			{
			sb.append(PARAM_SEPARATOR);
			sb.append(" ");	// pretty print
			sb.append(ent.getKey());
			// TODO: Support quoted values
			sb.append(VALUE_SEPARATOR);
			sb.append(ent.getValue());
			}
		
		return (sb.toString());
		}
	}
