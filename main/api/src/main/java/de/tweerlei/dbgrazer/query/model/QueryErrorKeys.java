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
package de.tweerlei.dbgrazer.query.model;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class QueryErrorKeys
	{
	/** Empty name */
	public static final String UNKNOWN_TYPE = "error_unknownQueryType";
	/** Empty name */
	public static final String EMPTY_NAME = "error_emptyName";
	/** Empty SQl statement */
	public static final String EMPTY_STATEMENT = "error_emptyStatement";
	/** View with SQL statement */
	public static final String VIEW_WITH_STATEMENT = "error_viewWithStatement";
	/** View without subqueries */
	public static final String VIEW_WITHOUT_SUBQUERIES = "error_viewWithoutSubqueries";
	/** View with wrong subquery count */
	public static final String VIEW_WITH_WRONG_COUNT = "error_viewWithWrongCount";
	
	private QueryErrorKeys()
		{
		}
	}
