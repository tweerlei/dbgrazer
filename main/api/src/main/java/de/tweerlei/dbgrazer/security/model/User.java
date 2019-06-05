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

import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * User details
 * 
 * @author Robert Wruck
 */
public interface User extends Named
	{
	/**
	 * Get the login name
	 * @return Login name
	 */
	public String getLogin();
	
	/**
	 * Get the password
	 * @return Password
	 */
	public String getPassword();
	
	/**
	 * Get the granted authorities
	 * @return Granted authorities
	 */
	public Set<Authority> getGrantedAuthorities();
	
	/**
	 * Get the granted authorities by group
	 * @return Granted authorities per group name
	 */
	public Map<String, Set<Authority>> getGroups();
	
	/**
	 * Check whether an authority is granted to the user, as
	 * shortcut for getGrantedAuthorities().contains(authority)
	 * @param authority Authority
	 * @return true if granted
	 */
	public boolean isGranted(Authority authority);
	
	/**
	 * Check whether an authority is granted to the user, as
	 * shortcut for getGroups().get(groupName).contains(authority)
	 * @param authority Authority
	 * @param groupName Group name
	 * @return true if granted
	 */
	public boolean isGranted(Authority authority, String groupName);
	
	/**
	 * Get additional attributes
	 * @return Map: Key -> Value
	 */
	public Map<String, String> getAttributes();
	}
