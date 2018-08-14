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
package de.tweerlei.dbgrazer.web.model;

import java.io.Serializable;

/**
 * Components extracted from a request path
 * 
 * @author Robert Wruck
 */
public class PathInfo implements Serializable
	{
	private final String category;
	private final String subcategory;
	private final String page;
	private final String viewName;
	private final String query;
	
	/**
	 * Constructor
	 * @param category Category
	 * @param subcategory Subcategory
	 * @param page Page name
	 * @param viewName View name
	 * @param query Query string
	 */
	public PathInfo(String category, String subcategory, String page, String viewName, String query)
		{
		this.category = category;
		this.subcategory = subcategory;
		this.page = page;
		this.viewName = viewName;
		this.query = query;
		}
	
	/**
	 * Get the category
	 * @return the category
	 */
	public String getCategory()
		{
		return category;
		}
	
	/**
	 * Get the subcategory
	 * @return the subcategory
	 */
	public String getSubcategory()
		{
		return subcategory;
		}
	
	/**
	 * Get the page name
	 * @return the page
	 */
	public String getPage()
		{
		return page;
		}
	
	/**
	 * Get the view name
	 * @return View name
	 */
	public String getViewName()
		{
		return (viewName);
		}
	
	/**
	 * Get the query
	 * @return the query
	 */
	public String getQuery()
		{
		return query;
		}
	}
