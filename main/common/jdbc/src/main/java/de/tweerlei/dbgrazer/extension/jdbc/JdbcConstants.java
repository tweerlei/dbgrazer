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
package de.tweerlei.dbgrazer.extension.jdbc;

/**
 * Constants used by the JDBC plugin
 * 
 * @author Robert Wruck
 */
public final class JdbcConstants
	{
	/** JdbcLinkType.NAME */
	public static final String LINKTYPE_JDBC = "JDBC";
	
	/** MultipleQueryType.NAME */
	public static final String QUERYTYPE_MULTIPLE = "MULTIPLE";
	/** SplitQueryType.NAME */
	public static final String QUERYTYPE_SPLIT = "SPLIT";
	/** FaultTolerantScriptQueryType.NAME */
	public static final String QUERYTYPE_TOLERANT_SCRIPT = "TOLERANT_SCRIPT";
	/** CustomQueryType.NAME */
	public static final String QUERYTYPE_CUSTOM = "CUSTOM";
	/** DMLQueryType.NAME */
	public static final String QUERYTYPE_DML = "DML";
	/** DMLKeyQueryType.NAME */
	public static final String QUERYTYPE_DML_KEY = "DML_KEY";
	
	private JdbcConstants()
		{
		}
	}
