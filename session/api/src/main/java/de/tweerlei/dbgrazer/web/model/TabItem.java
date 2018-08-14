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
package de.tweerlei.dbgrazer.web.model;

import java.util.Collections;
import java.util.Map;

/**
 * Data object for creating a tab item, usually used as a Map value associated with a labelling key.
 * @param <T> Payload object type
 *
 * @author Robert Wruck
 */
public class TabItem<T>
	{
	private final T payload;
	private final int count;
	private final String name;
	private final Map<Integer, String> params;
	private final String paramString;
	
	/**
	 * Constructor
	 * @param payload Payload object
	 * @param count Item count or -1 if not applicable
	 * @param name Object name (may differ from the "label" used as map key)
	 * @param params Object parameters
	 * @param paramString Object parameters pre-formatted as URL query parameters
	 */
	public TabItem(T payload, int count, String name, Map<Integer, String> params, String paramString)
		{
		this.payload = payload;
		this.count = count;
		this.name = name;
		this.params = (params == null) ? Collections.<Integer, String>emptyMap() : params;
		this.paramString = paramString;
		}

	/**
	 * Constructor
	 * @param payload Payload object
	 * @param count Item count or -1 if not applicable
	 * @param name Object name (may differ from the "label" used as map key)
	 */
	public TabItem(T payload, int count, String name)
		{
		this(payload, count, name, null, null);
		}

	/**
	 * Constructor
	 * @param payload Payload object
	 * @param count Item count or -1 if not applicable
	 */
	public TabItem(T payload, int count)
		{
		this(payload, count, null, null, null);
		}

	/**
	 * Constructor
	 * @param payload Payload object
	 */
	public TabItem(T payload)
		{
		this(payload, -1, null, null, null);
		}

	/**
	 * Get the name
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the params
	 * @return the params
	 */
	public Map<Integer, String> getParams()
		{
		return params;
		}

	/**
	 * Get the paramString
	 * @return the paramString
	 */
	public String getParamString()
		{
		return paramString;
		}

	/**
	 * Get the payload
	 * @return the payload
	 */
	public T getPayload()
		{
		return payload;
		}

	/**
	 * Get the item count
	 * @return the count, -1 if not applicable
	 */
	public int getCount()
		{
		return count;
		}
	}
