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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.SearchExecutor;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.transaction.compensating.manager.ContextSourceTransactionManager;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * LdapTemplate that sets the countLimit and timeLimit search controls for any search.
 * 
 * @author Robert Wruck
 */
public class LimitedLdapTemplate extends LdapTemplate
	{
	private final ContextSourceTransactionManager txManager;
	private final DefaultTransactionDefinition def;
	private int countLimit;
	private int timeLimit;
	private int pageSize;
	
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param contextSource ContextSource
	 */
	public LimitedLdapTemplate(LdapContextSource contextSource)
		{
		super(new TransactionAwareContextSourceProxy(contextSource));
		
		txManager = new ContextSourceTransactionManager();
		txManager.setContextSource(contextSource);
		
		def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		logger = Logger.getLogger(getClass().getCanonicalName());
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
	public void search(SearchExecutor se, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		if (pageSize <= 0)
			{
			logger.log(Level.INFO, "Performing search without paging");
			
			super.search(se, handler, processor);
			return;
			}
		
		logger.log(Level.INFO, "Performing search with page size of " + pageSize);
		
		final PagedResultsDirContextProcessor pager = new PagedResultsDirContextProcessor(pageSize);
		final DirContextProcessor multiProcessor = new MultiDirContextProcessor(pager, processor);
		
		// Wrap search calls in a transaction to prevent LdapTemplate from closing the connection
		// after each call.
		
		final TransactionStatus status = txManager.getTransaction(def);
		try	{
			int pageNumber = 1;
			do	{
				logger.log(Level.INFO, "Fetching page " + pageNumber++);
				
				super.search(se, handler, multiProcessor);
				}
			while (pager.getCookie() != null);
			
			logger.log(Level.INFO, "Fetched all pages");
			
			txManager.commit(status);
			}
		catch (Exception ex)
			{
			txManager.rollback(status);
			throw ex;
			}
		}
	
	@Override
	public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor)
		{
		try	{
			prepareSearchControls(controls);
			super.search(base, filter, controls, handler, processor);
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
			super.search(base, filter, controls, handler, processor);
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
	}
