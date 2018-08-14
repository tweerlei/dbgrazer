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
package de.tweerlei.dbgrazer.security.model;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Well known authorities
 * 
 * @author Robert Wruck
 */
public enum Authority implements Named
	{
	/** Log in */
	ROLE_LOGIN("login", false),
	
	/** Reload configuration */
	ROLE_RELOAD("reload", true),
	
	/** Edit queries */
	ROLE_EDIT("edit", false),
	
	/** Submit queries */
	ROLE_SUBMIT("submit", false),
	
	/** Browse the DB schema */
	ROLE_BROWSE("browse", false),
	
	/** Edit links */
	ROLE_LINKS("editLinks", true),
	
	/** Edit users */
	ROLE_USERS("editUsers", true),
	
	/** Call webservices */
	ROLE_WS("webservice", false),
	
	/** Edit records */
	ROLE_DML("dml", false);
	
	private final String shortName;
	private final boolean global;
	
	private Authority(String shortName, boolean global)
		{
		this.shortName = shortName;
		this.global = global;
		}
	
	@Override
	public String getName()
		{
		return (name());
		}
	
	/**
	 * Get the shortName
	 * @return the shortName
	 */
	public String getShortName()
		{
		return shortName;
		}
	
	/**
	 * Get the global
	 * @return the global
	 */
	public boolean isGlobal()
		{
		return global;
		}
	
	/**
	 * Find an authority by short name
	 * @param shortName Short name
	 * @return Authority or null
	 */
	public static Authority forShortName(String shortName)
		{
		for (Authority a : values())
			{
			if (a.getShortName().equals(shortName))
				return (a);
			}
		
		return (null);
		}
	}
