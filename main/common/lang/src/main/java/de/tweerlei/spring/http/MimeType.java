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
package de.tweerlei.spring.http;

import java.util.Map;

/**
 * Helper class for parsing MIME types
 * 
 * @author Robert Wruck
 */
public interface MimeType
	{
	/**
	 * Get the type
	 * @return the type
	 */
	public String getType();
	
	/**
	 * Get the subtype
	 * @return the subtype
	 */
	public String getSubtype();
	
	/**
	 * Get the params
	 * @return the params
	 */
	public Map<String, String> getParams();
	
	/**
	 * Get a param value
	 * @param key Key (case insensitive)
	 * @return the value
	 */
	public String getParam(String key);
	
	/**
	 * Get the media type without parameters
	 * @return Media type
	 */
	public String getMediaType();
	}
