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
package de.tweerlei.dbgrazer.web.backend;

import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Create DownloadSource objects for query results
 * 
 * @author Robert Wruck
 */
public interface ResultDownloader extends Named
	{
	/**
	 * Create a DownloadSource for a query result
	 * @param link Link name
	 * @param rs RowSet
	 * @param srcName Label for the RowSet
	 * @return DownloadSource
	 */
	public DownloadSource getDownloadSource(String link, RowSet rs, String srcName);
	
	/**
	 * Create a streamed DownloadSource for a RowProducer
	 * @param link Link name
	 * @param p RowProducer
	 * @param srcName Label for the RowProducer
	 * @param fileName File base name
	 * @return DownloadSource
	 */
	public DownloadSource getStreamDownloadSource(String link, RowProducer p, String srcName, String fileName);
	}
