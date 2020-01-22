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
package de.tweerlei.dbgrazer.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;

/**
 * Manage query settings
 * 
 * @author Robert Wruck
 */
public interface QuerySettingsManager
	{
	/**
	 * Get the raw settings for a Query
	 * @param query Query
	 * @return Settings
	 */
	public Map<String, String> getQuerySettings(Query query);
	
	/**
	 * Check whether trim columns is active
	 * @param query Query
	 * @return Trim columns active
	 */
	public boolean isTrimColumnsActive(Query query);
	
	/**
	 * Set trim columns active
	 * @param query Query
	 * @param b Trim columns active
	 */
	public void setTrimColumnsActive(Query query, boolean b);
	
	/**
	 * Check whether text formatting is active
	 * @param query Query
	 * @return Text formatting active
	 */
	public boolean isFormattingActive(Query query);
	
	/**
	 * Set text formatting active
	 * @param query Query
	 * @param b Text formatting active
	 */
	public void setFormattingActive(Query query, boolean b);
	
	/**
	 * Check whether coloring is active
	 * @param query Query
	 * @return Coloring active
	 */
	public boolean isSyntaxColoringActive(Query query);
	
	/**
	 * Set coloring active
	 * @param query Query
	 * @param b Coloring active
	 */
	public void setSyntaxColoringActive(Query query, boolean b);
	
	/**
	 * Check whether line numbering is active
	 * @param query Query
	 * @return Line numbering active
	 */
	public boolean isLineNumbersActive(Query query);
	
	/**
	 * Set line numbering active
	 * @param query Query
	 * @param b Line numbering active
	 */
	public void setLineNumbersActive(Query query, boolean b);
	
	/**
	 * Get the format name to use for result text formatting
	 * @param query Query
	 * @return Format name
	 */
	public String getFormatName(Query query);
	
	/**
	 * Set text format name
	 * @param query Query
	 * @param f Text format name
	 */
	public void setFormatName(Query query, String f);
	
	/**
	 * Check whether subqueries should be shown
	 * @param query Query
	 * @return Show subqueries active
	 */
	public boolean isShowSubqueriesActive(Query query);
	
	/**
	 * Get the format options to use for result text formatting
	 * @param query Query
	 * @return Options
	 */
	public Set<TextTransformerService.Option> getFormatOptions(Query query);
	
	/**
	 * Get the SchemaSettings for a schema
	 * @param schema Schema name
	 * @return SchemaSettings (created on demand)
	 */
	public SchemaSettings getSchemaSettings(String schema);
	
	/**
	 * Get whether the query history is enabled
	 * @return true if enabled
	 */
	public boolean isHistoryEnabled();
	
	/**
	 * Add an entry to the query history
	 * @param query Query
	 * @param params Parameters
	 */
	public void addHistoryEntry(Query query, List<String> params);
	
	/**
	 * Add an entry to the custom query history
	 * @param statement Statement
	 */
	public void addCustomHistoryEntry(String statement);
	
	/**
	 * Get an entry from the custom query history
	 * @param index Index
	 * @return Statement or null
	 */
	public String getCustomHistoryEntry(int index);
	
	/**
	 * Get default parameter values for a Query
	 * @param query Query
	 * @param fmt DataFormatter
	 * @return Default parameter values
	 */
	public Map<Integer, String> getDefaultParameterValues(Query query, DataFormatter fmt);
	
	/**
	 * Get the effective parameters to use for a Query
	 * @param query Query
	 * @param model Raw parameters
	 * @return Effective parameters
	 */
	public List<String> getEffectiveParameters(Query query, Map<Integer, String> model);
	
	/**
	 * Get the additional parameters from a model, not used by the given Query
	 * @param query Query
	 * @param model Raw parameters
	 * @return Additional parameters
	 */
	public List<String> getAdditionalParameters(Query query, Map<Integer, String> model);
	
	/**
	 * Normalize query parameters
	 * @param query Query
	 * @param model Raw parameters
	 * @return QueryParameters
	 */
	public QueryParameters prepareParameters(Query query, Map<Integer, String> model);
	
	/**
	 * Check whether the given query has filtered parameters, e.g. PASSWORD
	 * @param query Query
	 * @return true if filtered
	 */
	public boolean hasFilteredParameters(Query query);
	
	/**
	 * Build a parameter map
	 * @param params Parameter values
	 * @return Parameter map
	 */
	public Map<Integer, String> buildParameterMap(List<String> params);
	
	/**
	 * Translate parameters for a Query
	 * @param query Query
	 * @param params Raw parameters
	 * @param fmt DataFormatter
	 * @return Translated parameters
	 */
	public List<Object> translateParameters(Query query, List<String> params, DataFormatter fmt);
	
	/**
	 * Translate parameters for a Query that might be partially specified.
	 * If there are not enough elements in params of if any element is null, returns null.
	 * If any element in params parses as null, SubQueryInfo.IS_NULL will be placed in the returned list instead.
	 * @param query Query
	 * @param params Raw parameters
	 * @param fmt DataFormatter
	 * @return Translated parameters
	 */
	public List<Object> translatePartialParameters(Query query, List<String> params, DataFormatter fmt);
	}
