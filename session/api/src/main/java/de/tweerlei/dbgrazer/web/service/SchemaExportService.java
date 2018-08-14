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

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Create DownloadSource objects
 * 
 * @author Robert Wruck
 */
public interface SchemaExportService
	{
	/**
	 * Get the supported format names for getExportDownloadSource
	 * @return Format names
	 */
	public Set<String> getSupportedExportFormats();
	
	/**
	 * Create a DownloadSource to export a TableDescription
	 * @param link Link name
	 * @param name Schema name
	 * @param info TableDescription
	 * @param dialect SQLDialect
	 * @param attributes Additional attributes
	 * @param format Format tag
	 * @return DownloadSource
	 */
	public DownloadSource getExportDownloadSource(String link, String name, Set<TableDescription> info, SQLDialect dialect, Map<String, Object> attributes, String format);
	}
