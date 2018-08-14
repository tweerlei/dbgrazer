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
package de.tweerlei.dbgrazer.web.session;

import java.io.Serializable;
import java.util.Map;

import de.tweerlei.dbgrazer.web.constant.CacheClass;

/**
 * Cache query results
 * 
 * @author Robert Wruck
 */
public interface ResultCache
	{
	/**
	 * Cache an object
	 * @param cacheClass Cache class
	 * @param link Link name
	 * @param queryName Query name
	 * @param params Parameters
	 * @param cachedObject Object to cache
	 */
	public void addCachedObject(CacheClass cacheClass, String link, String queryName, Map<Integer, String> params, Serializable cachedObject);
	
	/**
	 * Cache an object
	 * @param cacheClass Cache class
	 * @param cachedObject Object to cache
	 * @return Cache key
	 */
	public String addCachedObject(CacheClass cacheClass, Serializable cachedObject);
	
	/**
	 * Get the cached object
	 * @param <T> Object type
	 * @param cacheClass Cache class
	 * @param link Link name
	 * @param queryName Query name
	 * @param params Parameters
	 * @param type Object class
	 * @return Cached object
	 */
	public <T extends Serializable> T getCachedObject(CacheClass cacheClass, String link, String queryName, Map<Integer, String> params, Class<T> type);
	
	/**
	 * Get the cached object
	 * @param <T> Object type
	 * @param cacheClass Cache class
	 * @param key Cache key
	 * @param type Object class
	 * @return Cached object
	 */
	public <T extends Serializable> T getCachedObject(CacheClass cacheClass, String key, Class<T> type);
	
	/**
	 * Clear the object cache
	 * @param cacheClass Cache class
	 */
	public void clearCachedObjects(CacheClass cacheClass);
	}
