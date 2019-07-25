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

import de.tweerlei.dbgrazer.plugins.ldap.impl.LdapLinkType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;

/**
 * Search LDAP entries
 * 
 * @author Robert Wruck
 */
public abstract class LdapSearchQueryType extends AbstractTableQueryType
	{
	private final int scope;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param linkType LinkType
	 * @param scope Search scope
	 */
	public LdapSearchQueryType(String name, LdapLinkType linkType, int scope)
		{
		super(name, linkType, ResultMapMode.SINGLE, null);
		this.scope = scope;
		}
	
	/**
	 * Get the search scope
	 * @return Scope
	 */
	public int getScope()
		{
		return (scope);
		}
	}
