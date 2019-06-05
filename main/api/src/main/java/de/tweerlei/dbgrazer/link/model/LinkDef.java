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
package de.tweerlei.dbgrazer.link.model;

import java.io.Serializable;
import java.util.Properties;
import java.util.Set;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Link definition
 * 
 * @author Robert Wruck
 */
public interface LinkDef extends Named, Serializable
	{
	/** Special login group name */
	public static final String LOGIN_GROUP = "*";
	
	/**
	 * Get the type
	 * @return the type
	 */
	public LinkType getType();
	
	/**
	 * Get the description
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Get the full description, including schema name
	 * @return Full description
	 */
	public String getFullDescription();
	
	/**
	 * Get the full description, including schema name
	 * @return Full description
	 */
	public String getSetDescription();
	
	/**
	 * Get the driver
	 * @return the driver
	 */
	public String getDriver();

	/**
	 * Get the url
	 * @return the url
	 */
	public String getUrl();

	/**
	 * Get the username
	 * @return the username
	 */
	public String getUsername();

	/**
	 * Get the password
	 * @return the password
	 */
	public String getPassword();

	/**
	 * Get the schemaName
	 * @return the schemaName
	 */
	public SchemaDef getSchema();

	/**
	 * Get the query set names
	 * @return query set names
	 */
	public Set<String> getQuerySetNames();
	
	/**
	 * Get the groupName
	 * @return groupName
	 */
	public String getGroupName();

	/**
	 * Get the setName
	 * @return setName
	 */
	public String getSetName();

	/**
	 * Get the dialect name
	 * @return dialect name
	 */
	public String getDialectName();
	
	/**
	 * Get a statement to execute before any DML operation
	 * @return Statement
	 */
	public String getPreDMLStatement();
	
	/**
	 * Get a statement to execute after any DML operation
	 * @return Statement
	 */
	public String getPostDMLStatement();
	
	/**
	 * Get the properties
	 * @return the properties
	 */
	public Properties getProperties();
	
	/**
	 * Get the writable flag
	 * @return writable flag
	 */
	public boolean isWritable();
	}
