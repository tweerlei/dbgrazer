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
package de.tweerlei.dbgrazer.web.constants.mongodb;

/**
 * Constants used by the LDAP plugin
 * 
 * @author Robert Wruck
 */
public final class MongoDBConstants
	{
	/** LdapLinkType.NAME */
	public static final String LINKTYPE_MONGODB = "MONGODB";
	
	/** MongoDBCommandQueryType.NAME */
	public static final String QUERYTYPE_COMMAND = "MONGODB_COMMAND";
	
	/** Database query attribute */
	public static final String ATTR_DATABASE = "mongoDatabase";
	
	private MongoDBConstants()
		{
		}
	}
