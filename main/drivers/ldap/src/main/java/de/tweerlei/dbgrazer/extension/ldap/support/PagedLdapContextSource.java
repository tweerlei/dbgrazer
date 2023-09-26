package de.tweerlei.dbgrazer.extension.ldap.support;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.PagedResultsControl;

import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LdapContextSource that uses result paging via PagedResultsControl
 * 
 * @author Robert Wruck
 */
public class PagedLdapContextSource extends LdapContextSource
	{
	private int pageSize;

	/**
	 * @return the pageSize
	 */
	public int getPageSize()
		{
		return pageSize;
		}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize)
		{
		this.pageSize = pageSize;
		}
	
	private Control[] buildControls() throws NamingException
		{
		try	{
			if (pageSize > 0)
				return (new Control[] { new PagedResultsControl(pageSize, false) });
			else
				return (null);
			}
		catch (IOException e)
			{
			throw new NamingException(e.getMessage());
			}
		}
	
	@Override
	protected DirContext getDirContextInstance(Hashtable environment) throws NamingException
		{
		return new InitialLdapContext(environment, buildControls());
		}
	}
