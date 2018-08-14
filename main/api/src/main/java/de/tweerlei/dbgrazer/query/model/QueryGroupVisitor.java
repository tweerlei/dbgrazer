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
 * A modifying visitor for QueryGroups
 * 
 * @author Robert Wruck
 */
public interface QueryGroupVisitor
	{
	/**
	 * Visit a list query
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitList(Query q);
	
	/**
	 * Visit a simple query
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitQuery(Query q);
	
	/**
	 * Visit a view query
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitView(Query q);
	
	/**
	 * Visit a listview query
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitListView(Query q);
	
	/**
	 * Visit a subquery
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitSubquery(Query q);
	
	/**
	 * Visit an action query
	 * @param q Query
	 * @return true to remove from group
	 */
	public boolean visitAction(Query q);
	}
