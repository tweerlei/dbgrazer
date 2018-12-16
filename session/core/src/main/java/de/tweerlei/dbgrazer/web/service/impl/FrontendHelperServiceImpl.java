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
package de.tweerlei.dbgrazer.web.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.math.Rational;
import de.tweerlei.common.textdata.JSONWriter;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.spring.service.StringTransformerService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class FrontendHelperServiceImpl implements FrontendHelperService
	{
	private static final String PATH_SEPARATOR = "/";
	private static final String QUERY_SEPARATOR = "?";
	private static final Pattern PATH_PATTERN = Pattern.compile("/?([^/]+)/([^/]+)/(.*)");
	
	private static final String PARAM_URL_PARAMETER = "params";
	
	private static final String PARAM_SEPARATOR = "  ";
	
	// Avg. menu item aspect ratio: Width 100px, height 10px
	private static final double MENU_ASPECT = 100.0 / 10.0;
	
	private final StringTransformerService stringTransformerService;
	private final DataFormatterFactory factory;
	
	/**
	 * Constructor
	 * @param stringTransformerService StringTransformerService
	 * @param factory DataFormatterFactory
	 */
	@Autowired
	public FrontendHelperServiceImpl(StringTransformerService stringTransformerService, DataFormatterFactory factory)
		{
		this.stringTransformerService = stringTransformerService;
		this.factory = factory;
		}
	
	@Override
	public PathInfo parsePath(String path, String query)
		{
		final Matcher m = PATH_PATTERN.matcher(path);
		if (m.matches())
			return (new PathInfo(
					m.group(1),
					m.group(2),
					m.group(3),
					m.group(1) + PATH_SEPARATOR + m.group(3),
					query
					));
		else if (path.startsWith(PATH_SEPARATOR))
			return (new PathInfo(
					null,
					null,
					path.substring(1),
					path.substring(1),
					query
					));
		else
			return (new PathInfo(
					null,
					null,
					path,
					path,
					query
					));
		}
	
	@Override
	public String buildPath(PathInfo pi)
		{
		return (buildPath(pi.getCategory(), pi.getSubcategory(), pi.getPage(), pi.getQuery()));
		}
	
	@Override
	public String buildPath(String category, String subcategory, String page, String query)
		{
		final StringBuilder sb = new StringBuilder();
		
		if (!StringUtils.empty(category) && !StringUtils.empty(subcategory))
			sb.append(category).append(PATH_SEPARATOR).append(subcategory).append(PATH_SEPARATOR).append(StringUtils.notNull(page));
		else
			sb.append(StringUtils.notNull(page));
		
		if (!StringUtils.empty(query))
			sb.append(QUERY_SEPARATOR).append(query);
		
		return (sb.toString());
		}
	
	@Override
	public String paramEncode(String s, boolean htmlEncode)
		{
		if (s == null)
			return (null);
		
		final StringBuilder sb = new StringBuilder();
		final List<String> params = Arrays.asList(s.split(PARAM_SEPARATOR, -1));
		appendParams(sb, params, htmlEncode, true);
		return (sb.toString());
		}
	
	@Override
	public String paramExtract(String s)
		{
		if (s == null)
			return (null);
		
		final int i = s.indexOf(' ');
		if (i < 0)
			return (StringUtils.trim(s));
		else
			return (StringUtils.trim(s.substring(0, i)));
		}
	
	@Override
	public String getLinkTitle(String s)
		{
		final String ret;
		if (s == null)
			ret = null;
		else
			{
			final String[] parts = s.split(PARAM_SEPARATOR, -1);
			ret = parts[parts.length - 1];
			}
		
		if (StringUtils.empty(ret))
			return ("\u2205");
		
		return (ret);
		}
	
	@Override
	public int getMenuRows(int n, Rational r)
		{
		final double aspect = r.doubleValue() / MENU_ASPECT;
		final double rmax = Math.sqrt(n / aspect);
		final double columns = Math.ceil(rmax * aspect);
		final double rows = Math.ceil(n / columns);
		
		return ((int) rows);
		}
	
	@Override
	public String getQueryParams(Collection<?> params, boolean htmlEncode)
		{
		final StringBuilder title = new StringBuilder();
		if (params != null)
			appendParams(title, params, htmlEncode, false);
		return (title.toString());
		}
	
	@Override
	public String getQueryParams(Map<Integer, ?> params, boolean htmlEncode)
		{
		final StringBuilder title = new StringBuilder();
		if (params != null)
			{
			for (Map.Entry<Integer, ?> ent : params.entrySet())
				{
				final String s;
				if (ent.getValue() == null)
					s = "";
				else
					s = String.valueOf(ent.getValue());
				
				if (htmlEncode)
					title.append("&amp;");
				else
					title.append("&");
				title.append(PARAM_URL_PARAMETER);
				title.append("%5B");
				title.append(ent.getKey());
				title.append("%5D=");
				title.append(stringTransformerService.toURL(s));
				}
			}
		return (title.toString());
		}
	
	private void appendParams(StringBuilder sb, Collection<?> params, boolean htmlEncode, boolean trim)
		{
		int i = 0;
		for (Object p : params)
			{
			final String s;
			if (p == null)
				s = "";
			else if (trim)
				s = StringUtils.trim(String.valueOf(p));
			else
				s = String.valueOf(p);
			
			if (htmlEncode)
				sb.append("&amp;");
			else
				sb.append("&");
			sb.append(PARAM_URL_PARAMETER);
			sb.append("%5B");
			sb.append(i++);
			sb.append("%5D=");
			sb.append(stringTransformerService.toURL(s));
			}
		}
	
	@Override
	public String getQueryTitle(String queryName, Collection<?> params)
		{
		final StringBuilder title = new StringBuilder();
		if (queryName.startsWith("$"))
			title.append(factory.getMessage(queryName.substring(1)));
		else
			title.append(queryName);
		if (!params.isEmpty())
			{
			title.append(": ");
			appendParams(title, params);
			}
		return (title.toString());
		}
	
	@Override
	public String getQuerySubtitle(Collection<?> params)
		{
		final StringBuilder title = new StringBuilder();
		if (params != null)
			appendParams(title, params);
		return (title.toString());
		}
	
	private void appendParams(StringBuilder sb, Collection<?> params)
		{
		int i = 0;
		for (Object p : params)
			{
			if (i > 0)
				sb.append(", ");
			
			final String s;
			if (p == null)
				s = "";
			else
				s = String.valueOf(p);
			
			sb.append(s);
			i++;
			}
		}
	
	@Override
	public String getQuerySubtitle(Query query, Collection<?> params)
		{
		final StringBuilder title = new StringBuilder();
		if (params != null)
			{
			final Iterator<ParameterDef> pi = query.getParameters().iterator();
			final Iterator<?> vi = params.iterator();
			int i = 0;
			while (pi.hasNext())
				{
				if (i > 0)
					title.append(", ");
				
				final ParameterDef p = pi.next();
				title.append(p.getName());
				title.append(": ");
				
				final String s;
				if (!vi.hasNext())
					s = "";
				else
					{
					final Object v = vi.next();
					if (v == null)
						s = "";
					else
						s = String.valueOf(v);
					}
				
				title.append(s);
				i++;
				}
			}
		return (title.toString());
		}
	
	@Override
	public String basename(String path)
		{
		if (path == null)
			return ("");
		
		final int index = path.lastIndexOf(PATH_SEPARATOR);
		if (index < 0)
			return (path);
		else
			return (path.substring(index + 1));
		}
	
	@Override
	public String dirname(String path)
		{
		if (path == null)
			return ("");
		
		final int index = path.lastIndexOf(PATH_SEPARATOR);
		if (index < 0)
			return ("");
		else
			return (path.substring(0, index + 1));
		}
	
	@Override
	public String filename(String dir, String base)
		{
		final StringBuilder sb = new StringBuilder();
		if (dir != null)
			sb.append(dir);
		if ((sb.length() > 0) && (sb.charAt(sb.length() - 1) != PATH_SEPARATOR.charAt(0)))
			sb.append(PATH_SEPARATOR);
		if (base != null)
			sb.append(base);
		return (sb.toString());
		}
	
	@Override
	public String toJSONString(String s)
		{
		final StringWriter sw = new StringWriter();
		final JSONWriter w = new JSONWriter(sw);
		try	{
			w.write(s);
			}
		catch (IOException ioe)
			{
			// unlikely for StringWriter
			}
		return (sw.toString());
		}
	}
