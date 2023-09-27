package de.tweerlei.dbgrazer.extension.ldap.support;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.springframework.ldap.core.DirContextProcessor;

/**
 * DirContextProcessor that delegates to a list of DirContextProcessors
 * 
 * @author Robert Wruck
 */
public class MultiDirContextProcessor implements DirContextProcessor
	{
	private final List<DirContextProcessor> processors;
	
	/**
	 * Constructor
	 * @param processors DirContextProcessors
	 */
	public MultiDirContextProcessor(DirContextProcessor... processors)
		{
		this.processors = new ArrayList<DirContextProcessor>();
		if (processors != null)
			{
			for (DirContextProcessor p : processors)
				{
				if (p != null)
					this.processors.add(p);
				}
			}
		}
	
	@Override
	public void preProcess(DirContext ctx) throws NamingException
		{
		for (ListIterator<DirContextProcessor> it = processors.listIterator(); it.hasNext(); )
			{
			final DirContextProcessor p = it.next();
			p.preProcess(ctx);
			}
		}
	
	@Override
	public void postProcess(DirContext ctx) throws NamingException
		{
		for (ListIterator<DirContextProcessor> it = processors.listIterator(processors.size()); it.hasPrevious(); )
			{
			final DirContextProcessor p = it.previous();
			p.postProcess(ctx);
			}
		}
	}
