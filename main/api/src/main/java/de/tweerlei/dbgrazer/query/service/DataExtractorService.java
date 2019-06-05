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
package de.tweerlei.dbgrazer.query.service;

import java.io.Reader;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.RowSet;

/**
 * Extract data from a String
 * 
 * @author Robert Wruck
 */
public interface DataExtractorService
	{
	/**
	 * Get the supported format names for extractData
	 * @return Format names
	 */
	public Set<String> getSupportedFormats();
	
	/**
	 * Extract data
	 * @param in Input Reader
	 * @param format Format name
	 * @return Extracted RowSet or null
	 */
	public RowSet extractData(Reader in, String format);
	}
