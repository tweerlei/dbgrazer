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
package de.tweerlei.dbgrazer.web.controller.jdbc;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class JdbcMessageKeys
	{
	/** JS extension file */
	public static final String EXTENSION_JS = "jdbc.js";
	
	/** Header text for generated DDL compare files */
	public static final String DDL_COMPARE_HEADER = "ddlCompareHeader";
	/** Header text for generated DML compare files */
	public static final String DML_COMPARE_HEADER = "dmlCompareHeader";
	
	/*
	 * Multilevel names for the DB browser
	 */
	
	/** Catalog tab title */
	public static final String CATALOG_LEVEL = "$catalogLevel";
	/** Schema tab title */
	public static final String SCHEMA_LEVEL = "$schemaLevel";
	/** Schema tab title */
	public static final String OBJECT_LEVEL = "$schemaLevel";
	
	/*
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Catalog tab title */
	public static final String CATALOG_TAB = "$catalogTab";
	/** Schema tab title */
	public static final String SCHEMA_TAB = "$schemaTab";
	
	
	private JdbcMessageKeys()
		{
		}
	}
