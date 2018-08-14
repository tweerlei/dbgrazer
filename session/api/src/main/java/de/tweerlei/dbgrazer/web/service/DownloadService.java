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
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Create DownloadSource objects for query results
 * 
 * @author Robert Wruck
 */
public interface DownloadService
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
	 * @param tableName Table name for INSERT
	 * @param pk Optional PK column indices for MERGE
	 * @param dialect SQLDialect
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getDownloadSource(String link, RowSet rs, String tableName, Set<Integer> pk, SQLDialect dialect, String format);
	
	/**
	 * Create a streamed DownloadSource for a query
	 * @param link Link name
	 * @param query Query
	 * @param params Query parameters
	 * @param tableName Table name for INSERT
	 * @param pk Optional PK column indices for MERGE
	 * @param dialect SQLDialect
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getStreamDownloadSource(String link, Query query, Map<Integer, String> params, String tableName, Set<Integer> pk, SQLDialect dialect, String format);
	
	/**
	 * Create a streamed DownloadSource for a RowProducer
	 * @param link Link name
	 * @param p RowProducer
	 * @param srcName Label for the RowProducer
	 * @param fileName File base name
	 * @param tableName Table name for INSERT
	 * @param pk Optional PK column indices for MERGE
	 * @param dialect SQLDialect
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getStreamDownloadSource(String link, RowProducer p, String srcName, String fileName, String tableName, Set<Integer> pk, SQLDialect dialect, String format);
	
	/**
	 * Create a streamed DownloadSource for a RowSetProducer
	 * @param link Link name
	 * @param p RowSetProducer
	 * @param srcName Label for the RowProducer
	 * @param fileName File base name
	 * @param dialect SQLDialect
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getMultiStreamDownloadSource(String link, RowSetProducer p, String srcName, String fileName, SQLDialect dialect, String format);
	}
