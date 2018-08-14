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

import java.util.Collection;
import java.util.Map;

import de.tweerlei.common.math.Rational;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.web.model.PathInfo;

/**
 * Helper service for front controllers
 * 
 * @author Robert Wruck
 */
public interface FrontendHelperService
	{
	/**
	 * Split a path into components.
	 * The patterns understood by this method are (leading slash is optional):
	 * /category/subcategory/path/to/page.ext
	 * /page.ext
	 * Category and subcategory will only be extracted if both are present.
	 * @param path Path
	 * @param query Query string
	 * @return PathInfo
	 */
	public PathInfo parsePath(String path, String query);
	
	/**
	 * Build a path from a PathInfo object
	 * @param pi PathInfo
	 * @return Path
	 */
	public String buildPath(PathInfo pi);
	
	/**
	 * Build a path from components
	 * @param category Category
	 * @param subcategory Subcategory
	 * @param page Page path
	 * @param query Query string
	 * @return Path
	 */
	public String buildPath(String category, String subcategory, String page, String query);
	
	/**
	 * Encode a String for passing as query parameter.
	 * The String "123  456" is split into multiple parameters like:
	 * &amp;params[0]=123&amp;params[1]=456
	 * @param s Parameter string
	 * @param htmlEncode HTML-encode special characters
	 * @return Query String
	 */
	public String paramEncode(String s, boolean htmlEncode);
	
	/**
	 * Format a String as link title.
	 * Only the last component is returned:
	 * "123  456" will yield "456".
	 * @param s Parameter string
	 * @return Link title
	 */
	public String getLinkTitle(String s);
	
	/**
	 * Get the number of menu items per column for a total number of items
	 * @param n Item count
	 * @param r Aspect ratio
	 * @return Row count
	 */
	public int getMenuRows(int n, Rational r);
	
	/**
	 * Build query link URL parameters from parameter values
	 * @param params Parameter values
	 * @param htmlEncode HTML-encode special characters
	 * @return Query title
	 */
	public String getQueryParams(Collection<?> params, boolean htmlEncode);
	
	/**
	 * Build query link URL parameters from parameter values
	 * @param params Parameter values
	 * @param htmlEncode HTML-encode special characters
	 * @return Query title
	 */
	public String getQueryParams(Map<Integer, ?> params, boolean htmlEncode);
	
	/**
	 * Build a query title from query name and parameter values
	 * @param queryName Query name
	 * @param params Parameter values
	 * @return Query title
	 */
	public String getQueryTitle(String queryName, Collection<?> params);
	
	/**
	 * Build a query subtitle from parameter values
	 * @param params Parameter values
	 * @return Query subtitle
	 */
	public String getQuerySubtitle(Collection<?> params);
	
	/**
	 * Build a query subtitle from parameter names and values
	 * @param query Query definition
	 * @param params Parameter values
	 * @return Query subtitle
	 */
	public String getQuerySubtitle(Query query, Collection<?> params);
	
	/**
	 * Get the base name of a path (the part after the last '/', if any)
	 * @param path Path
	 * @return Base name
	 */
	public String basename(String path);
	
	/**
	 * Get the dir name of a path (the part before and including the last '/')
	 * @param path Path
	 * @return Dir name
	 */
	public String dirname(String path);
	
	/**
	 * Join a dirname and a basename
	 * @param dir dirname
	 * @param base basename
	 * @return File name
	 */
	public String filename(String dir, String base);
	
	/**
	 * Convert a String to JSON notation (including quotes)
	 * @param s String
	 * @return JSON string
	 */
	public String toJSONString(String s);
	}
