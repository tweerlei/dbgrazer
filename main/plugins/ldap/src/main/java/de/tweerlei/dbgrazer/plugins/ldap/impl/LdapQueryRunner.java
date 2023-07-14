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
package de.tweerlei.dbgrazer.plugins.ldap.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringJoiner;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.util.impl.NamedComparators;
import de.tweerlei.dbgrazer.extension.ldap.LdapAccessService;
import de.tweerlei.dbgrazer.plugins.ldap.types.LdapAttributesQueryType;
import de.tweerlei.dbgrazer.plugins.ldap.types.LdapListQueryType;
import de.tweerlei.dbgrazer.plugins.ldap.types.LdapSearchQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.spring.service.TimeService;

/**
 * Run filesystem queries
 * 
 * @author Robert Wruck
 */
@Service
public class LdapQueryRunner extends BaseQueryRunner
	{
	private static final String DN_ATTRIBUTE = "dn";
	private static final String RDN_ATTRIBUTE = "rdn";
	private static final String RDN_NAME_ATTRIBUTE = "rdn.name";
	private static final String RDN_PARENT_ATTRIBUTE = "rdn.parent";
	private static final String RDN_LINK_ATTRIBUTE = "rdn.link";
	private static final String ALL_ATTRIBUTES = "*";
	
	private static abstract class RowMapper implements ContextMapper
		{
		private final Query query;
		
		public RowMapper(Query query)
			{
			this.query = query;
			}
		
		public Query getQuery()
			{
			return (query);
			}
		
		public abstract List<ColumnDef> getColumns();
		
		public abstract List<ResultRow> getRows();
		
		public abstract boolean isMoreAvailable();
		
		@Override
		public Object mapFromContext(Object ctx)
			{
			final DirContextOperations adapter = (DirContextOperations) ctx;
			
			try	{
				return (mapFromDirContext(adapter));
				}
			catch (NamingException e)
				{
				throw LdapUtils.convertLdapException(e);
				}
			}
		
		protected abstract Object mapFromDirContext(DirContextOperations ctx) throws NamingException;
		}
	
	private static class RowListMapper extends RowMapper
		{
		private static final String VALUE_SEPARATOR = "\n";
		
		private final String[] attrIds;
		private final int limit;
		private DistinguishedName prefix;
		private DistinguishedName suffix;
		private final List<ResultRow> rows;
		private List<ColumnDef> columns;
		private int count;
		
		public RowListMapper(Query query, String[] attrIds, int limit, DistinguishedName prefix, DistinguishedName suffix)
			{
			super(query);
			this.attrIds = attrIds;
			this.limit = limit;
			this.prefix = prefix;
			this.suffix = suffix;
			this.rows = new LinkedList<ResultRow>();
			this.columns = null;
			this.count = 0;
			}
		
		@Override
		public List<ColumnDef> getColumns()
			{
			return (columns);
			}
		
		@Override
		public List<ResultRow> getRows()
			{
			return (rows);
			}
		
		@Override
		public boolean isMoreAvailable()
			{
			return (count > limit);
			}
		
		@Override
		protected Object mapFromDirContext(DirContextOperations ctx) throws NamingException
			{
			count++;
			if (isMoreAvailable())
				return (null);
			
			final Attributes attributes = ctx.getAttributes();
			
			if (columns == null)
				{
				columns = new ArrayList<ColumnDef>(attrIds.length);
				for (String attrId : attrIds)
					{
					if (DN_ATTRIBUTE.equalsIgnoreCase(attrId))
						columns.add(new ColumnDefImpl(DN_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
					else if (RDN_ATTRIBUTE.equalsIgnoreCase(attrId))
						columns.add(new ColumnDefImpl(RDN_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
					else if (RDN_NAME_ATTRIBUTE.equalsIgnoreCase(attrId))
						columns.add(new ColumnDefImpl(RDN_NAME_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
					else if (RDN_PARENT_ATTRIBUTE.equalsIgnoreCase(attrId))
						columns.add(new ColumnDefImpl(RDN_PARENT_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
					else if (RDN_LINK_ATTRIBUTE.equalsIgnoreCase(attrId))
						columns.add(new ColumnDefImpl(RDN_LINK_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
					else if (ALL_ATTRIBUTES.equals(attrId))
					{
//					columns.add(new ColumnDefImpl(DN_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(0), null, null));
//					columns.add(new ColumnDefImpl(RDN_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(1), null, null));
//					columns.add(new ColumnDefImpl(RDN_NAME_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(2), null, null));
//					columns.add(new ColumnDefImpl(RDN_PARENT_ATTRIBUTE, ColumnType.STRING, null, getQuery().getTargetQueries().get(3), null, null));
						final List<ColumnDef> allColumns = new ArrayList<ColumnDef>(attributes.size());
					final NamingEnumeration<? extends Attribute> all = attributes.getAll();
					try	{
						while (all.hasMore())
							{
							final Attribute a = all.next();
								allColumns.add(new ColumnDefImpl(a.getID(), ColumnType.forObject(a.get()), null, null, null, null));
							}
						}
					finally
						{
						all.close();
						}
						Collections.sort(allColumns, NamedComparators.BY_NAME);
						columns.addAll(allColumns);
					}
				else
					{
							final Attribute a = attributes.get(attrId);
							if (a == null)
								columns.add(new ColumnDefImpl(attrId, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
							else
								columns.add(new ColumnDefImpl(attrId, ColumnType.forObject(a.get()), null, getQuery().getTargetQueries().get(columns.size()), null, null));
							}
						}
					}
/* TODO: Include columns not present on first entry
			else if ((attrIds == null) && (attributes.size() + 1 > columns.size()))
				{
				
				}*/
			
			final ResultRow row = new DefaultResultRow(columns.size());
			
			for (ColumnDef c : columns)
				{
				if (DN_ATTRIBUTE.equals(c.getName()))
					{
					if (suffix != null)
						{
						final Name rdn = ctx.getDn();
						final DistinguishedName dn = new DistinguishedName(ctx.getNameInNamespace());
						dn.addAll(dn.size() - rdn.size(), suffix);
						row.getValues().add(dn.toString());
						}
					else
						row.getValues().add(ctx.getNameInNamespace());
					}
				else if (RDN_ATTRIBUTE.equals(c.getName()))
					{
					row.getValues().add(getRdn(ctx).toString());
					}
				else if (RDN_NAME_ATTRIBUTE.equals(c.getName()))
					{
					final Name rdn = getRdn(ctx);
					if (rdn.isEmpty())
						row.getValues().add("");
					else
						row.getValues().add(rdn.getSuffix(rdn.size() - 1).toString());
					}
				else if (RDN_PARENT_ATTRIBUTE.equals(c.getName()))
					{
					final Name rdn = getRdn(ctx);
					if (rdn.isEmpty())
						row.getValues().add("");
					else
						row.getValues().add(rdn.getPrefix(rdn.size() - 1).toString());
					}
				else if (RDN_LINK_ATTRIBUTE.equals(c.getName()))
					{
					final Name rdn = getRdn(ctx);
					if (rdn.isEmpty())
						row.getValues().add("");
					else
						row.getValues().add(getRdn(ctx).toString() + "  " + rdn.getSuffix(rdn.size() - 1).toString());
					}
				else
					{
					final Attribute a = attributes.get(c.getName());
					if (a == null)
						row.getValues().add(null);
					else if ((c.getType() == ColumnType.STRING) && (a.size() > 1))
						{
						final StringJoiner sb = new StringJoiner(VALUE_SEPARATOR);
						final NamingEnumeration<?> all = a.getAll();
						try	{
							while (all.hasMore())
								{
								final Object value = all.next();
								sb.append(String.valueOf(value));
								}
							}
						finally
							{
							all.close();
							}
						row.getValues().add(sb.toString());
						}
					else
						row.getValues().add(a.get());
					}
				}
			
			rows.add(row);
			
			return (null);
			}
		
		private Name getRdn(DirContextOperations ctx)
			{
			if (prefix != null)
				{
				final DistinguishedName rdn = new DistinguishedName(ctx.getDn());
				rdn.prepend(prefix);
				return (rdn);
				}
			else
				return (ctx.getDn());
			}
		}
	
	private static class AttributeListMapper extends RowMapper
		{
		private final String attrId;
		private final List<ResultRow> rows;
		private List<ColumnDef> columns;
		private int count;
		
		public AttributeListMapper(Query query, String attrId)
			{
			super(query);
			this.attrId = attrId;
			this.rows = new LinkedList<ResultRow>();
			this.columns = null;
			}
		
		@Override
		public List<ColumnDef> getColumns()
			{
			return (columns);
			}
		
		@Override
		public List<ResultRow> getRows()
			{
			return (rows);
			}
		
		@Override
		public boolean isMoreAvailable()
			{
			return (count > 1);
			}
		
		@Override
		protected Object mapFromDirContext(DirContextOperations ctx) throws NamingException
			{
			count++;
			if (isMoreAvailable())
				return (null);
			
			final Attributes attributes = ctx.getAttributes();
			
			if (columns == null)
				{
				columns = new ArrayList<ColumnDef>(1);
				final Attribute a = attributes.get(attrId);
				if (a == null)
					columns.add(new ColumnDefImpl(attrId, ColumnType.STRING, null, getQuery().getTargetQueries().get(columns.size()), null, null));
				else
					columns.add(new ColumnDefImpl(attrId, ColumnType.forObject(a.get()), null, getQuery().getTargetQueries().get(columns.size()), null, null));
				}
			
			for (ColumnDef c : columns)
				{
				final Attribute a = attributes.get(c.getName());
				final NamingEnumeration<?> all = a.getAll();
				try	{
					while (all.hasMore())
						{
						final Object value = all.next();
						
						final ResultRow row = new DefaultResultRow(columns.size());
						row.getValues().add(value);
						rows.add(row);
						}
					}
				finally
					{
					all.close();
					}
				}
			
			return (null);
			}
		}
	
	private final TimeService timeService;
	private final LdapAccessService ldapAccessService;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param ldapAccessService LdapAccessService
	 */
	@Autowired
	public LdapQueryRunner(TimeService timeService, LdapAccessService ldapAccessService)
		{
		super("LDAP");
		this.timeService = timeService;
		this.ldapAccessService = ldapAccessService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return (t.getLinkType() instanceof LdapLinkType);
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final LdapTemplate ldap = ldapAccessService.getLdapTemplate(link);
		if (ldap == null)
			throw new PerformQueryException(query.getName(), new RuntimeException("Unknown link " + link));
		
		final int maxRows = Math.min(limit, ldapAccessService.getMaxRows(link));
		final Result res = new ResultImpl(query);
		
		try	{
			final String stmt = new ParamReplacer(params).replaceAll(query.getStatement());
			performLdapQuery(ldap, stmt, query, subQueryIndex, res, maxRows);
			}
		catch (RuntimeException e)
			{
			throw new PerformQueryException(query.getName(), e);
			}
		
		return (res);
		}
	
	private void performLdapQuery(LdapTemplate ldap, String statement, Query query, int subQueryIndex, Result res, int limit)
		{
		final long start = timeService.getCurrentTime();
		
		final LdapQueryParser p = new LdapQueryParser(statement);
		
		final RowMapper mapper;
		
		if (res.getQuery().getType() instanceof LdapSearchQueryType)
			{
			final LdapSearchQueryType sqt = (LdapSearchQueryType) res.getQuery().getType();
			mapper = new RowListMapper(query, p.getAttributes(), limit, null, null);
			if (p.getAttributes() != null)
				ldap.search(p.getBaseDN(), p.getFilter(), sqt.getScope(), p.getAttributes(), mapper);
			else
				ldap.search(p.getBaseDN(), p.getFilter(), sqt.getScope(), mapper);
			}
		else if (res.getQuery().getType() instanceof LdapListQueryType)
			{
			final DistinguishedName base;
			if (!StringUtils.empty(p.getBaseDN()))
				{
				// Spring-LDAP bug:
				// For listBindings(), the RDN returned from DirContextOperations.getDn() is incomplete
				// and thus the DN returned from getNameInNamespace() is invalid.
				// To fix this, we prepend the base DN passed to listBindings().
				base = new DistinguishedName(p.getBaseDN());
				}
			else
				base = null;
			mapper = new RowListMapper(query, p.getAttributes(), limit, base, base);
			ldap.listBindings(p.getBaseDN(), mapper);
			}
		else if ((res.getQuery().getType() instanceof LdapAttributesQueryType) && (p.getAttributes() != null) && (p.getAttributes().length == 1))
			{
			mapper = new AttributeListMapper(query, p.getAttributes()[0]);
			if (p.getAttributes() != null)
				ldap.lookup(p.getBaseDN(), p.getAttributes(), mapper);
			else
				ldap.lookup(p.getBaseDN(), mapper);
			}
		else
			{
			// Spring-LDAP bug:
			// lookup returns a stub DirContextAdapter that will just echo the passed in base DN
			// which is not absolute, so we have to append the root DN
			mapper = new RowListMapper(query, p.getAttributes(), limit, null, getRootDN(ldap));
			if (p.getAttributes() != null)
				ldap.lookup(p.getBaseDN(), p.getAttributes(), mapper);
			else
				ldap.lookup(p.getBaseDN(), mapper);
			}
		
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, mapper.getColumns());
		rs.getRows().addAll(mapper.getRows());
		rs.setMoreAvailable(mapper.isMoreAvailable());
		rs.setQueryTime(end - start);
		
		res.getRowSets().put(res.getQuery().getName(), rs);
		}
	
	private DistinguishedName getRootDN(LdapTemplate ldap)
		{
		return (((LdapContextSource) ldap.getContextSource()).getBaseLdapPath());
		}
	}
