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
package de.tweerlei.dbgrazer.plugins.ldap.types;

import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.plugins.ldap.impl.LdapLinkType;

/**
 * Search LDAP entries
 * 
 * @author Robert Wruck
 */
@Service
@Order(404)
public class LdapSearchObjectQueryType extends LdapSearchQueryType
	{
	private static final String NAME = "LDAP_OBJECT";
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 */
	@Autowired
	public LdapSearchObjectQueryType(LdapLinkType linkType)
		{
		super(NAME, linkType, SearchControls.OBJECT_SCOPE);
		}
	}
