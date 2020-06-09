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
package de.tweerlei.dbgrazer.link.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.springframework.validation.BindException;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;

/**
 * Manage database linkss
 * 
 * @author Robert Wruck
 */
public interface LinkService
	{
	/**
	 * Get all known link types
	 * @return Link types
	 */
	public Set<LinkType> findAllLinkTypes();
	
	/**
	 * Find a link type
	 * @param name Type name
	 * @return Link type or null
	 */
	public LinkType findLinkType(String name);
	
	/**
	 * Get all known link names sorted by description
	 * @param groups Group names to restrict links to
	 * @param schema Schema to restrict links to
	 * @param version Schema version to restrict links to
	 * @return Link descriptions -> Link names
	 */
	public SortedMap<String, String> findAllLinkNames(Set<String> groups, String schema, String version);
	
	/**
	 * Get all known link sets sorted by description
	 * @param groups Group names to restrict links to
	 * @param schema Schema to restrict links to
	 * @param version Schema version to restrict links to
	 * @return Set descriptions -> (Map: Link description -> Link name)
	 */
	public SortedMap<String, Map<String, String>> findAllLinkSets(Set<String> groups, String schema, String version);
	
	/**
	 * Get a link by name
	 * @param name Link name
	 * @param groups Group names to restrict links to
	 * @return Link
	 */
	public LinkDef getLink(String name, Set<String> groups);
	
	/**
	 * Get link data for creating a connection
	 * @param name Link name
	 * @return Link
	 */
	public LinkDef getLinkData(String name);
	
	/**
	 * Get all known link group names
	 * @return Link group names
	 */
	public SortedSet<String> findAllLinkGroups();
	
	/**
	 * Get all known link set names
	 * @return Link set names
	 */
	public SortedSet<String> findAllLinkSets();
	
	/**
	 * Get all known query set names
	 * @return Query set names
	 */
	public SortedSet<String> findAllQuerySets();
	
	/**
	 * Get the schema name for a link
	 * @param c Link name
	 * @return Schema name or null
	 */
	public SchemaDef getSchema(String c);
	
	/**
	 * Get all known link definitions, grouped by schema
	 * @return Link definitions
	 */
	public SortedMap<SchemaDef, List<LinkDef>> groupAllLinks();
	
	/**
	 * Find all links for a given link type
	 * @param type Link type name
	 * @return Links
	 */
	public List<LinkDef> findLinksByType(String type);
	
	/**
	 * Find all links for a given link type
	 * @param type Link type name
	 * @param url URL
	 * @return Links
	 */
	public List<LinkDef> findLinksByUrl(String type, String url);
	
	/**
	 * Find all links for a given link type
	 * @param type Link type name
	 * @param name Set name
	 * @return Links
	 */
	public List<LinkDef> findLinksBySetName(String type, String name);
	
	/**
	 * Find all links for a given link type
	 * @param type Link type name
	 * @param name Group name
	 * @return Links
	 */
	public List<LinkDef> findLinksByGroupName(String type, String name);
	
	/**
	 * Find a link definition by name
	 * @param name Link name
	 * @return LinkDef or null
	 */
	public LinkDef findLinkByName(String name);
	
	/**
	 * Create a link definition
	 * @param user User name
	 * @param conn Link definition
	 * @return Created link name
	 * @throws BindException on validation errors
	 */
	public String createLink(String user, LinkDef conn) throws BindException;
	
	/**
	 * Update a query
	 * @param user User name
	 * @param name Link name
	 * @param conn Link definition
	 * @return Updated link name
	 * @throws BindException on validation errors
	 */
	public String updateLink(String user, String name, LinkDef conn) throws BindException;
	
	/**
	 * Remove a link definition
	 * @param user User name
	 * @param name Link name
	 * @return true on success
	 */
	public boolean removeLink(String user, String name);
	
	/**
	 * Get a link definition's modification history
	 * @param name Link name
	 * @param limit Limit number of returned entries
	 * @return History
	 */
	public List<HistoryEntry> getHistory(String name, int limit);
	
	/**
	 * Register a listener for being notified upon link changes
	 * @param listener LinkListener
	 */
	public void addListener(LinkListener listener);
	
	/**
	 * Register a manager for providing statistical data
	 * @param manager LinkManager
	 */
	public void addManager(LinkManager manager);
	
	/**
	 * Get active link statistics
	 * @return Map: Link name -> Physical connection count
	 */
	public Map<String, Integer> getLinkStats();
	
	/**
	 * Reload link definitions
	 */
	public void reloadLinks();
	}
