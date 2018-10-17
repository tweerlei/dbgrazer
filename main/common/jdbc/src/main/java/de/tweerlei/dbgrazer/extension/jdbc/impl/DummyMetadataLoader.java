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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataLoader;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
@Service("dummyMetadataLoader")
@Scope("prototype")
public class DummyMetadataLoader implements MetadataLoader
	{
	// Collections is missing emptySortedSet() and emptySortedMap()
	private static final SortedSet<String> EMPTY_SORTED_SET = Collections.unmodifiableSortedSet(new TreeSet<String>());
	private static final SortedMap<QualifiedName, String> EMPTY_SORTED_MAP = Collections.unmodifiableSortedMap(new TreeMap<QualifiedName, String>());
	
	private String link;
	
	@Override
	public void setLink(String link)
		{
		this.link = link;
		}
	
	@Override
	public String getLink()
		{
		return (link);
		}
	
	@Override
	public Map<String, String> getDBInfo()
		{
		return (Collections.singletonMap("notice", "No metadata loaded (dummy)"));
		}
	
	@Override
	public SortedSet<String> getCatalogs()
		{
		return (EMPTY_SORTED_SET);
		}
	
	@Override
	public SortedSet<String> getSchemas()
		{
		return (EMPTY_SORTED_SET);
		}
	
	@Override
	public SortedMap<QualifiedName, String> getTables(String catalog, String schema)
		{
		return (EMPTY_SORTED_MAP);
		}
	
	@Override
	public TableDescription getTableInfo(QualifiedName table)
		{
		throw new RuntimeException("Not implemented");
		}
	
	@Override
	public Set<TableDescription> getTableInfos(Set<QualifiedName> tables, Set<QualifiedName> missing, ProgressMonitor p)
		{
		return (Collections.emptySet());
		}
	
	@Override
	public Set<TableDescription> getTableInfoRecursive(QualifiedName table, int depth, boolean all, boolean toplevel, ProgressMonitor p)
		{
		return (Collections.emptySet());
		}
	
	@Override
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params)
		{
		throw new RuntimeException("Not implemented");
		}
	}
