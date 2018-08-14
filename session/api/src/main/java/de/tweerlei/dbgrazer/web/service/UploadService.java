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

import java.io.InputStream;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.RowProducer;

/**
 * Create DownloadSource objects for uploaded files
 * 
 * @author Robert Wruck
 */
public interface UploadService
	{
	/**
	 * Get the supported format names for getExportDownloadSource
	 * @return Format names
	 */
	public Set<String> getSupportedUploadFormats();
	
	/**
	 * Create a RowProducer for table data supplied as InputStream
	 * @param info TableDescription
	 * @param input InputStream
	 * @param format Format tag
	 * @return RowProducer or null
	 */
	public RowProducer createRowProducer(TableDescription info, InputStream input, String format);
	}
