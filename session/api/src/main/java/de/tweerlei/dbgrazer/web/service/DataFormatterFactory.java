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

import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Create DataFormatter objects
 * 
 * @author Robert Wruck
 */
public interface DataFormatterFactory
	{
	/**
	 * Create a DataFormatter intended for web output
	 * @return DataFormatter
	 */
	public DataFormatter getWebFormatter();
	
	/**
	 * Create a DataFormatter intended for data export
	 * @return DataFormatter
	 */
	public DataFormatter getExportFormatter();
	
	/**
	 * Create a DataFormatter intended for producing SQL literals
	 * @param dialect SQLDialect
	 * @return DataFormatter
	 */
	public DataFormatter getSQLFormatter(SQLDialect dialect);
	
	/**
	 * Create a DataFormatter intended for producing XML schema literals
	 * @return DataFormatter
	 */
	public DataFormatter getXMLFormatter();
	
	/**
	 * Create an SQLWriterService
	 * @param h Target StatementHandler
	 * @param dialect SQLDialect
	 * @param pretty Insert line breaks for increased readability
	 * @return SQLWriterService
	 */
	public SQLWriter getSQLWriter(StatementHandler h, SQLDialect dialect, boolean pretty);
	
	/**
	 * Get a localized message
	 * @param key Message key
	 * @param args Arguments
	 * @return Message
	 */
	public String getMessage(String key, Object... args);
	
	/**
	 * Execute a Runnable with default request settings
	 * @param r Runnable
	 */
	public void doWithDefaultTheme(Runnable r);
	}
