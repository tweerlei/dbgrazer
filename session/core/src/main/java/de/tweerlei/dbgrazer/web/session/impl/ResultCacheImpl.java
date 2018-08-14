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
package de.tweerlei.dbgrazer.web.session.impl;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.session.ResultCache;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
@Scope("session")
public class ResultCacheImpl implements ResultCache, Serializable
	{
	private final Map<CacheClass, Map<String, Serializable>> cache;
	
	/**
	 * Constructor
	 */
	public ResultCacheImpl()
		{
		this.cache = new EnumMap<CacheClass, Map<String, Serializable>>(CacheClass.class);
		for (CacheClass c : CacheClass.values())
			this.cache.put(c, new ConcurrentHashMap<String, Serializable>());
		}
	
	@Override
	public void addCachedObject(CacheClass cacheClass, String link, String queryName, Map<Integer, String> params, Serializable cachedObject)
		{
		final String key = makeCacheKey(link, queryName, params);
		put(cacheClass, key, cachedObject);
		}
	
	@Override
	public String addCachedObject(CacheClass cacheClass, Serializable cachedObject)
		{
		final String key = UUID.randomUUID().toString();
		put(cacheClass, key, cachedObject);
		return (key);
		}
	
	@Override
	public <T extends Serializable> T getCachedObject(CacheClass cacheClass, String link, String queryName, Map<Integer, String> params, Class<T> type)
		{
		final String key = makeCacheKey(link, queryName, params);
		final Serializable o = get(cacheClass, key);
		if ((o != null) && type.isAssignableFrom(o.getClass()))
			return (type.cast(o));
		
		return (null);
		}
	
	@Override
	public <T extends Serializable> T getCachedObject(CacheClass cacheClass, String key, Class<T> type)
		{
		final Serializable o = get(cacheClass, key);
		if ((o != null) && type.isAssignableFrom(o.getClass()))
			return (type.cast(o));
		
		return (null);
		}
	
	@Override
	public void clearCachedObjects(CacheClass cacheClass)
		{
		cache.get(cacheClass).clear();
		}
	
	private String makeCacheKey(String link, String queryName, Map<Integer, String> params)
		{
		return (link + "/" + queryName + "/" + String.valueOf(params));
		}
	
	private void put(CacheClass cacheClass, String key, Serializable cachedObject)
		{
		cache.get(cacheClass).put(key, cachedObject);
		}
	
	private Serializable get(CacheClass cacheClass, String key)
		{
		return (cache.get(cacheClass).get(key));
		}
	}
