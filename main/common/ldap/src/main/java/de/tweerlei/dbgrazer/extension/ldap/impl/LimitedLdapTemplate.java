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
package de.tweerlei.dbgrazer.extension.ldap.impl;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairCallbackHandler;

/**
 * LdapTemplate that sets the countLimit and timeLimit search controls for any search
 * 
 * @author Robert Wruck
 */
public class LimitedLdapTemplate extends LdapTemplate
	{
	private int countLimit;
	private int timeLimit;
	
	/**
	 * Constructor
	 * @param contextSource ContextSource
	 */
	public LimitedLdapTemplate(ContextSource contextSource)
		{
		super(contextSource);
		}
	
	/**
	 * Get the countLimit
	 * @return the countLimit
	 */
	public int getCountLimit()
		{
		return countLimit;
		}
	
	/**
	 * Set the countLimit
	 * @param countLimit the countLimit to set
	 */
	public void setCountLimit(int countLimit)
		{
		this.countLimit = countLimit;
		}
	
	/**
	 * Get the timeLimit
	 * @return the timeLimit
	 */
	public int getTimeLimit()
		{
		return timeLimit;
		}
	
	/**
	 * Set the timeLimit
	 * @param timeLimit the timeLimit to set
	 */
	public void setTimeLimit(int timeLimit)
		{
		this.timeLimit = timeLimit;
		}
	
	private void prepareSearchControls(SearchControls controls)
		{
		controls.setCountLimit(countLimit);
		controls.setTimeLimit(timeLimit);
		}
	
	@Override
	public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler)
		{
		prepareSearchControls(controls);
		super.search(base, filter, controls, handler);
		}
	
	@Override
	public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler)
		{
		prepareSearchControls(controls);
		super.search(base, filter, controls, handler);
		}
	
	@Override
	public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		prepareSearchControls(controls);
		super.search(base, filter, controls, handler, processor);
		}
	
	@Override
	public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		prepareSearchControls(controls);
		super.search(base, filter, controls, handler, processor);
		}
	}
