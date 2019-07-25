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
package de.tweerlei.dbgrazer.plugins.http.types;

/**
 * QueryType attribute names
 * 
 * @author Robert Wruck
 */
public final class QueryTypeAttributes
	{
	/** RowSet attribute for the endpoint name */
	public static final String ATTR_ENDPOINT = "endpoint";
	/** RowSet attribute for the action name */
	public static final String ATTR_ACTION = "soapAction";
	/** RowSet attribute for the content type */
	public static final String ATTR_CONTENT_TYPE = "contentType";
	/** RowSet attribute for the TextFormatter name */
	public static final String ATTR_FORMATTER = "formatter";
	/** RowSet attribute for the formatting flag */
	public static final String ATTR_FORMATTING = "formatting";
	/** RowSet attribute for the syntax coloring flag */
	public static final String ATTR_SYNTAX_COLORING = "syntaxColoring";
	/** RowSet attribute for the line numbers flag */
	public static final String ATTR_LINE_NUMBERS = "lineNumbers";
	/** RowSet attribute for the link name */
	public static final String ATTR_LINK = "connectionName";
	
	private QueryTypeAttributes()
		{
		}
	}
