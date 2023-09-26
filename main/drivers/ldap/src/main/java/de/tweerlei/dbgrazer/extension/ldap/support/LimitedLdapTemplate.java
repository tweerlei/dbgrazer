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
package de.tweerlei.dbgrazer.extension.ldap.support;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.SearchExecutor;

/**
 * LdapTemplate that sets the countLimit and timeLimit search controls for any search
 * 
 * @author Robert Wruck
 */
public class LimitedLdapTemplate extends LdapTemplate
	{
	private int countLimit;
	private int timeLimit;
	private int pageSize;
	
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
	
	/**
	 * Get the pageSize
	 * @return the pageSize
	 */
	public int getPageSize()
		{
		return pageSize;
		}
	
	/**
	 * Set the pageSize
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize)
		{
		this.pageSize = pageSize;
		}
	
	private void prepareSearchControls(SearchControls controls)
		{
		controls.setCountLimit(countLimit);
		controls.setTimeLimit(timeLimit);
		}
	
	@Override
	public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler)
		{
		try	{
			prepareSearchControls(controls);
			super.search(base, filter, controls, handler);
			}
		catch (SizeLimitExceededException e)
			{
			// ignore
			}
		catch (TimeLimitExceededException e)
			{
			// ignore
			}
		}
	
	@Override
	public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler)
		{
		try	{
			prepareSearchControls(controls);
			super.search(base, filter, controls, handler);
			}
		catch (SizeLimitExceededException e)
			{
			// ignore
			}
		catch (TimeLimitExceededException e)
			{
			// ignore
			}
		}
	
	@Override
	public void search(SearchExecutor se, NameClassPairCallbackHandler handler)
		{
		super.search(se, handler, getPagingDirContextProcessor());
		}
	
	@Override
	public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		try	{
			prepareSearchControls(controls);
			super.search(base, filter, controls, handler, new MultiDirContextProcessor(getPagingDirContextProcessor(), processor));
			}
		catch (SizeLimitExceededException e)
			{
			// ignore
			}
		catch (TimeLimitExceededException e)
			{
			// ignore
			}
		}
	
	@Override
	public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		try	{
			prepareSearchControls(controls);
			super.search(base, filter, controls, handler, new MultiDirContextProcessor(getPagingDirContextProcessor(), processor));
			}
		catch (SizeLimitExceededException e)
			{
			// ignore
			}
		catch (TimeLimitExceededException e)
			{
			// ignore
			}
		}
	
	private DirContextProcessor getPagingDirContextProcessor()
		{
		if (pageSize > 0)
			return new PagedResultsDirContextProcessor(pageSize);
		else
			return null;
		}
	}
