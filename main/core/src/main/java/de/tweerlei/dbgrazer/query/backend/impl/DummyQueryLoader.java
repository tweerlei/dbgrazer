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
package de.tweerlei.dbgrazer.query.backend.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.backend.QueryLoader;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;

/**
 * QueryLoader that returns a static set of queries
 * 
 * @author Robert Wruck
 */
@Service("dummyQueryLoader")
public class DummyQueryLoader implements QueryLoader
	{
	private static final SchemaDef DUMMY_SCHEMA = new SchemaDef(null, null);
	private static final SortedMap<String, Query> queries;
	static
		{
		queries = new TreeMap<String, Query>();
/*		queries.put("ImageByID", new QueryImpl(
				"ImageByID",
				DUMMY_SCHEMA,
				"",
				"SELECT i.ID, i.LongName, i.UniqueName, i.ExternalName, i.Description, i.ID_ImageSource, i.ID_ImageLicense, i.ID_Image_Replaced, i.CreationDate, i.ID_Employee_Creator, i.EditDate, i.ID_Employee_Editor, i.DeletionDate, i.ID_Employee_Deleter"
				+" FROM image i"
				+" WHERE i.ID = ?",
				ResultType.ROW,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				Collections.<Integer, TargetDef>singletonMap(9, new QueryTargetImpl("EmployeeView")),
				Collections.<String, String>emptyMap()
				));
		queries.put("ImageByNumber", new QueryImpl(
				"ImageByNumber",
				DUMMY_SCHEMA,
				"",
				"SELECT * FROM image i"
				+" WHERE i.longname = ?",
				ResultType.MULTIPLE,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("Nummer", ColumnType.STRING, null)),
				Collections.<Integer, TargetDef>emptyMap(),
				Collections.<String, String>emptyMap()
				));
		queries.put("EmployeeByID", new QueryImpl(
				"EmployeeByID",
				DUMMY_SCHEMA,
				"",
				"SELECT ID, Login, Password, FirstName, LastName, EMail, Enabled, ID_EmployeeType"
				+" FROM employee"
				+" WHERE id = ?",
				ResultType.ROW,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				Collections.<Integer, TargetDef>emptyMap(),
				Collections.<String, String>emptyMap()
				));
		queries.put("EmployeesByGroup", new QueryImpl(
				"EmployeesByGroup",
				DUMMY_SCHEMA,
				"",
				"SELECT e.ID, e.Login, e.Password, e.FirstName, e.LastName, e.EMail, e.Enabled, e.ID_EmployeeType"
				+" FROM employee e"
				+" INNER JOIN employee_employeegroup eg ON eg.ID_Employee = e.ID"
				+" WHERE eg.ID_EmployeeGroup = ?",
				ResultType.MULTIPLE,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, "Groups")),
				Collections.<Integer, TargetDef>singletonMap(0, new QueryTargetImpl("EmployeeView")),
				Collections.<String, String>emptyMap()
				));
		queries.put("EmployeesByName", new QueryImpl(
				"EmployeesByName",
				DUMMY_SCHEMA,
				"",
				"SELECT e.ID, e.Login, e.Password, e.FirstName, e.LastName, e.EMail, e.Enabled, e.ID_EmployeeType"
				+" FROM employee e"
				+" WHERE e.LastName LIKE ?",
				ResultType.MULTIPLE,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("Name", ColumnType.PATTERN, null)),
				Collections.<Integer, TargetDef>singletonMap(0, new QueryTargetImpl("EmployeeView")),
				Collections.<String, String>emptyMap()
				));
		queries.put("GroupsByEmployee", new QueryImpl(
				"GroupsByEmployee",
				DUMMY_SCHEMA,
				"",
				"SELECT g.ID, g.LongName"
				+" FROM employeegroup g"
				+" INNER JOIN employee_employeegroup eg ON eg.ID_EmployeeGroup = g.ID"
				+" WHERE eg.ID_Employee = ?"
				+" ORDER BY g.LongName",
				ResultType.MULTIPLE,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				Collections.<Integer, TargetDef>singletonMap(0, new QueryTargetImpl("GroupView")),
				Collections.<String, String>emptyMap()
				));
		queries.put("GroupByID", new QueryImpl(
				"GroupByID",
				DUMMY_SCHEMA,
				"",
				"SELECT ID, LongName"
				+" FROM employeegroup"
				+" WHERE id = ?",
				ResultType.ROW,
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				Collections.<Integer, TargetDef>emptyMap(),
				Collections.<String, String>emptyMap()
				));
		queries.put("Groups", new QueryImpl(
				"Groups",
				DUMMY_SCHEMA,
				"",
				"SELECT ID, LongName"
				+" FROM employeegroup"
				+" ORDER BY LongName",
				ResultType.MAP,
				Collections.<ParameterDef>emptyList(),
				Collections.<Integer, TargetDef>singletonMap(0, new QueryTargetImpl("GroupView")),
				Collections.<String, String>emptyMap()
				));*/
		queries.put("EmployeeView", new ViewImpl(
				"EmployeeView",
				DUMMY_SCHEMA,
				"",
				new ViewQueryType(),
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				CollectionUtils.<SubQueryDef>list(new SubQueryDefImpl("EmployeeByID", null), new SubQueryDefImpl("GroupsByEmployee", null)),
				Collections.<String, String>emptyMap()
				));
		queries.put("GroupView", new ViewImpl(
				"GroupView",
				DUMMY_SCHEMA,
				"",
				new ViewQueryType(),
				Collections.<ParameterDef>singletonList(new ParameterDefImpl("ID", ColumnType.INTEGER, null)),
				CollectionUtils.<SubQueryDef>list(new SubQueryDefImpl("GroupByID", null), new SubQueryDefImpl("EmployeesByGroup", null)),
				Collections.<String, String>emptyMap()
				));
/*		queries.put("Error", new QueryImpl(
				"Error",
				DUMMY_SCHEMA,
				"",
				"SELECT FROM WHERE",
				ResultType.MULTIPLE,
				Collections.<ParameterDef>emptyList(),
				Collections.<Integer, TargetDef>emptyMap(),
				Collections.<String, String>emptyMap()
				));
		queries.put("LongText", new QueryImpl(
				"LongText",
				DUMMY_SCHEMA,
				"",
				"SELECT 'Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.'",
				ResultType.SINGLE,
				Collections.<ParameterDef>emptyList(),
				Collections.<Integer, TargetDef>emptyMap(),
				Collections.<String, String>emptyMap()
				));*/
		}
	
	@Override
	public SortedMap<String, Query> loadQueries(SchemaDef schema)
		{
		return (queries);
		}
	
	@Override
	public void createQuery(SchemaDef schema, String user, String name, Query query) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public void updateQuery(SchemaDef schema, String user, String name, String newName, Query query) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public void removeQuery(SchemaDef schema, String user, String query) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public List<HistoryEntry> getHistory(SchemaDef schema, String name, int limit) throws IOException
		{
		return (new ArrayList<HistoryEntry>());
		}
	
	@Override
	public Query getQueryVersion(SchemaDef schema, String name, String version) throws IOException
		{
		return (null);
		}
	
	@Override
	public List<SchemaDef> getSubSchemas(SchemaDef schema)
		{
		return (new ArrayList<SchemaDef>());
		}
	
	@Override
	public void renameSchema(String user, SchemaDef oldName, SchemaDef newName) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public Map<String, String> loadAttributes(SchemaDef schema)
		{
		return (Collections.emptyMap());
		}
	
	@Override
	public void updateAttributes(SchemaDef schema, String user, Map<String, String> attributes) throws IOException
		{
		throw new IOException("Not implemented");
		}
	}
