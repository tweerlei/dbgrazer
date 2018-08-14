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
package de.tweerlei.dbgrazer.web.service;

import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Create DownloadSource objects for query results
 * 
 * @author Robert Wruck
 */
public interface ResultDownloadService
	{
	/**
	 * Get the supported format names for getExportDownloadSource
	 * @return Format names
	 */
	public Set<String> getSupportedDownloadFormats();
	
	/**
	 * Create a DownloadSource for a query result
	 * @param link Link name
	 * @param rs RowSet
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getDownloadSource(String link, RowSet rs, String format);
	
	/**
	 * Create a streamed DownloadSource for a query
	 * @param link Link name
	 * @param query Query
	 * @param params Query parameters
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getStreamDownloadSource(String link, Query query, Map<Integer, String> params, String format);
	}
