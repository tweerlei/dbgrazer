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
package de.tweerlei.dbgrazer.extension.ldap;

import org.springframework.ldap.core.LdapTemplate;

/**
 * Manage LDAP connections
 * 
 * @author Robert Wruck
 */
public interface LdapAccessService
	{
	/**
	 * Get the LdapTemplate for a link
	 * @param c Link name
	 * @return LdapTemplate or null
	 */
	public LdapTemplate getLdapTemplate(String c);
	
	/**
	 * Get the LdapTemplate for a link using different credentials
	 * @param c Link name
	 * @param username LDAP bind DN
	 * @param password LDAP password
	 * @return LdapTemplate or null
	 */
	public LdapTemplate getLdapTemplate(String c, String username, String password);
	
	/**
	 * Get the configured max. rows to fetch
	 * @param c Link name
	 * @return Max row count
	 */
	public int getMaxRows(String c);
	}
