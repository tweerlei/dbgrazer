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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.MapBuilder;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.SubQueryInfo;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.dbgrazer.web.session.impl.SchemaSettingsImpl;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.service.TimeService;

/**
 * Manage query settings
 * 
 * @author Robert Wruck
 */
@Service
public class QuerySettingsManagerImpl implements QuerySettingsManager
	{
	// fallback key for settings not associated with a named query
	private static final String CACHE_KEY = "<custom>";
	
	// Default attribute settings
	private static final Map<String, Boolean> ATTR_DEFAULTS = new MapBuilder<String, Boolean>()
			.put(RowSetConstants.ATTR_TRIM, Boolean.FALSE)
			.put(RowSetConstants.ATTR_FORMATTING, Boolean.FALSE)
			.put(RowSetConstants.ATTR_SYNTAX_COLORING, Boolean.TRUE)
			.put(RowSetConstants.ATTR_LINE_NUMBERS, Boolean.FALSE)
			.buildReadOnly();
	
	private final TimeService timeService;
	private final ConfigAccessor configService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param configService ConfigAccessor
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QuerySettingsManagerImpl(TimeService timeService, ConfigAccessor configService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.timeService = timeService;
		this.configService = configService;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	private SchemaSettings createDefaultSchemaSettings()
		{
		final SchemaSettings s = new SchemaSettingsImpl();
		
		return (s);
		}
	
	@Override
	public SchemaSettings getSchemaSettings(String schema)
		{
		SchemaSettings s = userSettings.getSchemaSettings().get(schema);
		if (s == null)
			{
			s = createDefaultSchemaSettings();
			userSettings.getSchemaSettings().put(schema, s);
			}
		return (s);
		}
	
	private boolean isSettingActive(Query query, String attr)
		{
		final String value = getQuerySettings(query).get(attr);
		if (value == null)
			{
			final Boolean defaultValue = ATTR_DEFAULTS.get(attr);
			if (defaultValue != null)
				return (defaultValue);
			
			return (false);
			}
		
		return (Boolean.valueOf(value));
		}
	
	private void setSetting(Query query, String attr, boolean value)
		{
		getQuerySettings(query).put(attr, Boolean.toString(value));
		}
	
	@Override
	public boolean isTrimColumnsActive(Query query)
		{
		return (isSettingActive(query, RowSetConstants.ATTR_TRIM));
		}
	
	@Override
	public void setTrimColumnsActive(Query query, boolean b)
		{
		setSetting(query, RowSetConstants.ATTR_TRIM, b);
		}
	
	@Override
	public boolean isFormattingActive(Query query)
		{
		return (isSettingActive(query, RowSetConstants.ATTR_FORMATTING));
		}
	
	@Override
	public void setFormattingActive(Query query, boolean b)
		{
		setSetting(query, RowSetConstants.ATTR_FORMATTING, b);
		}
	
	@Override
	public boolean isSyntaxColoringActive(Query query)
		{
		return (isSettingActive(query, RowSetConstants.ATTR_SYNTAX_COLORING));
		}
	
	@Override
	public void setSyntaxColoringActive(Query query, boolean b)
		{
		setSetting(query, RowSetConstants.ATTR_SYNTAX_COLORING, b);
		}
	
	@Override
	public boolean isLineNumbersActive(Query query)
		{
		return (isSettingActive(query, RowSetConstants.ATTR_LINE_NUMBERS));
		}
	
	@Override
	public void setLineNumbersActive(Query query, boolean b)
		{
		setSetting(query, RowSetConstants.ATTR_LINE_NUMBERS, b);
		}
	
	@Override
	public boolean isShowSubqueriesActive(Query query)
		{
		return (Boolean.valueOf(query.getAttributes().get(RowSetConstants.ATTR_SHOW_SUBQUERIES)));
		}
	
	@Override
	public boolean isHistoryEnabled()
		{
		return (configService.get(ConfigKeys.ENABLE_HISTORY));
		}
	
	@Override
	public void addHistoryEntry(Query query, List<String> model)
		{
		if (!isHistoryEnabled())
			return;
		
		if (query == null)
			return;
		
		final int n = query.getParameters().size();
		final int m = model.size();
		final List<String> params = new ArrayList<String>(n);
		
		for (int i = 0; i < n; i++)
			{
			final String v = (i < m) ? model.get(i) : null;
			params.add(v);
			}
		
		final QueryHistoryEntry newent = new QueryHistoryEntry(query.getName(), params);
		final List<QueryHistoryEntry> h = connectionSettings.getQueryHistory();
		final int l = configService.get(ConfigKeys.HISTORY_LIMIT);
		int c = 1;
		for (Iterator<QueryHistoryEntry> it = h.iterator(); it.hasNext(); )
			{
			final QueryHistoryEntry ent = it.next();
			if ((c >= l) || ent.equals(newent))
				it.remove();
			else
				c++;
			}
		h.add(0, newent);
		}
	
	@Override
	public void addCustomHistoryEntry(String statement)
		{
		if (!isHistoryEnabled())
			return;
		
		if (StringUtils.empty(statement))
			return;
		
		final List<String> h = connectionSettings.getCustomQueryHistory();
		final int l = configService.get(ConfigKeys.HISTORY_LIMIT);
		int c = 1;
		for (Iterator<String> it = h.iterator(); it.hasNext(); )
			{
			final String ent = it.next();
			if ((c >= l) || ent.equals(statement))
				it.remove();
			else
				c++;
			}
		h.add(0, statement);
		}
	
	@Override
	public String getCustomHistoryEntry(int index)
		{
		if (!isHistoryEnabled())
			return (null);
		
		final List<String> h = connectionSettings.getCustomQueryHistory();
		if (index >= 0 && index < h.size())
			return h.get(index);
		
		return (null);
		}
	
	private Map<String, String> createDefaultQuerySettings(Query query)
		{
		final Map<String, String> qs = (query == null) ? new HashMap<String, String>() : new HashMap<String, String>(query.getAttributes());
		
		for (Map.Entry<String, Boolean> ent : ATTR_DEFAULTS.entrySet())
			{
			// Add explicit entries for attributes that are active by default
			if (ent.getValue() == Boolean.TRUE)
				qs.put(ent.getKey(), ent.getValue().toString());
			}
		
		return (qs);
		}
	
	@Override
	public Map<String, String> getQuerySettings(Query query)
		{
		final String key = (query == null) ? CACHE_KEY : query.getName();
		
		Map<String, String> qs = connectionSettings.getQuerySettings().get(key);
		if (qs == null)
			{
			qs = createDefaultQuerySettings(query);
			connectionSettings.getQuerySettings().put(key, qs);
			}
		
		return (qs);
		}
	
	@Override
	public String getFormatName(Query query)
		{
		final String value = getQuerySettings(query).get(RowSetConstants.ATTR_FORMATTER);
		if (StringUtils.empty(value) && (query != null))
			return (query.getAttributes().get(RowSetConstants.ATTR_FORMATTER));
		
		return (value);
		}
	
	@Override
	public void setFormatName(Query query, String f)
		{
		getQuerySettings(query).put(RowSetConstants.ATTR_FORMATTER, f);
		}
	
	@Override
	public Set<TextTransformerService.Option> getFormatOptions(Query query)
		{
		final Set<TextTransformerService.Option> ret = EnumSet.noneOf(TextTransformerService.Option.class);
		if (isLineNumbersActive(query))
			ret.add(TextTransformerService.Option.LINE_NUMBERS);
		if (isSyntaxColoringActive(query))
			ret.add(TextTransformerService.Option.SYNTAX_COLORING);
		if (isFormattingActive(query))
			ret.add(TextTransformerService.Option.FORMATTING);
		
		return (ret);
		}
	
	@Override
	public Map<Integer, String> getDefaultParameterValues(Query query, DataFormatter fmt)
		{
		final String dateValue = fmt.format(ColumnType.DATE, timeService.getCurrentDate());
		final String intValue = fmt.format(ColumnType.INTEGER, 0);
		final String floatValue = fmt.format(ColumnType.FLOAT, 0.0);
		
		final Map<Integer, String> ret = new HashMap<Integer, String>();
		
		int i = 0;
		for (ParameterDef p : query.getParameters())
			{
			final String last = userSettings.getParameterHistory().get(p.getName());
			if (last != null)
				{
				ret.put(i++, last);
				continue;
				}
			
			switch (p.getType())
				{
				case BOOLEAN:
					ret.put(i, Boolean.FALSE.toString());
					break;
				case DATE:
					ret.put(i, dateValue);
					break;
				case FLOAT:
					ret.put(i, floatValue);
					break;
				case INTEGER:
					ret.put(i, intValue);
					break;
				default:
					ret.put(i, "");
					break;
				}
			i++;
			}
		
		return (ret);
		}
	
	@Override
	public List<String> getEffectiveParameters(Query query, Map<Integer, String> model)
		{
		final int n = query.getParameters().size();
		final List<String> params = new ArrayList<String>(n);
		final boolean historize = !StringUtils.empty(query.getName());
		
		for (int i = 0; i < n; i++)
			{
			final ParameterDef p = query.getParameters().get(i);
			String v = model.get(i);
			if (v != null)
				{
				if (historize)
					userSettings.getParameterHistory().put(p.getName(), v);
				}
			else if (p.getType() == ColumnType.BOOLEAN)
				{
				// Hack: Since we are using checkboxes for booleans, FALSE will not show up
				v = Boolean.FALSE.toString();
				}
			else
				{
				if (historize)
					v = userSettings.getParameterHistory().get(p.getName());
				}
			params.add(v);
			}
		
		return (params);
		}
	
	@Override
	public List<String> getAdditionalParameters(Query query, Map<Integer, String> model)
		{
		final int n = query.getParameters().size();
		
		int m = 0;
		for (Integer i : model.keySet())
			{
			if (i + 1 > m)
				m = i + 1;
			}
		
		if (m <= n)
			return (Collections.emptyList());
		
		final List<String> params = new ArrayList<String>(m - n);
		
		for (int i = n; i < m; i++)
			params.add(model.get(i));
		
		return (params);
		}
	
	@Override
	public QueryParameters prepareParameters(Query query, Map<Integer, String> model)
		{
		final int n = query.getParameters().size();
		final List<String> params = new ArrayList<String>(n);
		
		for (int i = 0; i < n; i++)
			{
			final String v = model.get(i);
			if (v == null)
				params.add(v);
			else
				{
				final ParameterDef p = query.getParameters().get(i);
				if (isFiltered(p.getType()))
					params.add("");
				else
					params.add(v);
				}
			}
		
		return (new QueryParameters(query, model,
				getEffectiveParameters(query, model),
				params,
				getAdditionalParameters(query, model)
				));
		}
	
	@Override
	public boolean hasFilteredParameters(Query query)
		{
		for (ParameterDef p : query.getParameters())
			{
			if (isFiltered(p.getType()))
				return (true);
			}
		return (false);
		}
	
	private boolean isFiltered(ColumnType t)
		{
		return ((t == ColumnType.PASSWORD) || (t == ColumnType.CLOB));
		}
	
	@Override
	public List<Object> translateParameters(Query query, List<String> model, DataFormatter fmt)
		{
		final int n = query.getParameters().size();
		final int m = model.size();
		final List<Object> params = new ArrayList<Object>(n);
		
		for (int i = 0; i < n; i++)
			{
			final ParameterDef p = query.getParameters().get(i);
			final String v = (i < m) ? model.get(i) : null;
			if (v != null)
				params.add(fmt.parse(p.getType(), v));
			else
				params.add(null);
			}
		
		return (params);
		}
	
	@Override
	public List<Object> translatePartialParameters(Query query, List<String> model, DataFormatter fmt)
		{
		final int n = query.getParameters().size();
		final int m = model.size();
		final List<Object> params = new ArrayList<Object>(n);
		
		for (int i = 0; i < n; i++)
			{
			final ParameterDef p = query.getParameters().get(i);
			final String v = (i < m) ? model.get(i) : null;
			if (v == null)
				return (null);
			
			final Object parsed = fmt.parse(p.getType(), v);
			if (parsed == null)
				params.add(SubQueryInfo.IS_NULL);
			else
				params.add(parsed);
			}
		
		return (params);
		}
	
	@Override
	public Map<Integer, String> buildParameterMap(List<String> params)
		{
		final Map<Integer, String> ret = new TreeMap<Integer, String>();
		
		if (params != null)
			{
			int i = 0;
			for (String s : params)
				ret.put(i++, s);
			}
		
		return (ret);
		}
	}
