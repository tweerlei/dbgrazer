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
package de.tweerlei.ermtools.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.tweerlei.common.state.FiniteStateMachine;
import de.tweerlei.common.state.FiniteStateMachineHistory;
import de.tweerlei.common.xml.AbstractXMLParser;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ColumnType;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.model.SQLSchema;

/**
 * Parser für Schemadefinitionen
 * 
 * @author Robert Wruck
 */
public class DBSchemaParser extends AbstractXMLParser
	{
	private final FiniteStateMachineHistory state;
	private int ignore;
	
	private Pattern pattern;
	private SQLSchema schema;
	
	private String tableName;
	private final List<ColumnDescription> columns;
	
	private String name;
	private final List<String> columnNames;
	
	private PrimaryKeyDescription pk;
	
	private boolean unique;
	private final List<IndexDescription> indices;
	
	private String refCatalog;
	private String refSchema;
	private String refTable;
	private final Map<String, String> columnJoins;
	private final List<ForeignKeyDescription> fks;
	
	private final List<PrivilegeDescription> privs;
	
	private static final int STATE_UNDEF = 0;
	private static final int STATE_DBSCHEMA = 1;
	private static final int STATE_TABLE = 2;
	private static final int STATE_COLUMNS = 3;
	private static final int STATE_COLUMN = 4;
	private static final int STATE_PRIMARYKEY = 5;
	private static final int STATE_COLUMNREF = 6;
	private static final int STATE_INDICES = 7;
	private static final int STATE_INDEX = 8;
	private static final int STATE_FOREIGNKEYS = 9;
	private static final int STATE_FOREIGNKEY = 10;
	private static final int STATE_COLUMNJOIN = 11;
	private static final int STATE_PRIVILEGES = 12;
	private static final int STATE_PRIVILEGE = 13;
	private static final int STATE_MAX = 14;
	
	/** Konstruktor */
	public DBSchemaParser()
		{
		super(true, true);
		final FiniteStateMachine fsm = new FiniteStateMachine(STATE_MAX);
		fsm.enableTransitions(STATE_UNDEF, STATE_DBSCHEMA);
		fsm.enableTransitions(STATE_DBSCHEMA, STATE_TABLE);
		fsm.enableTransitions(STATE_TABLE, STATE_COLUMNS);
		fsm.enableTransitions(STATE_TABLE, STATE_PRIMARYKEY);
		fsm.enableTransitions(STATE_TABLE, STATE_INDICES);
		fsm.enableTransitions(STATE_TABLE, STATE_FOREIGNKEYS);
		fsm.enableTransitions(STATE_TABLE, STATE_PRIVILEGES);
		fsm.enableTransitions(STATE_COLUMNS, STATE_COLUMN);
		fsm.enableTransitions(STATE_PRIMARYKEY, STATE_COLUMNREF);
		fsm.enableTransitions(STATE_INDICES, STATE_INDEX);
		fsm.enableTransitions(STATE_INDEX, STATE_COLUMNREF);
		fsm.enableTransitions(STATE_FOREIGNKEYS, STATE_FOREIGNKEY);
		fsm.enableTransitions(STATE_FOREIGNKEY, STATE_COLUMNJOIN);
		fsm.enableTransitions(STATE_PRIVILEGES, STATE_PRIVILEGE);
		state = new FiniteStateMachineHistory(fsm);
		ignore = 0;
		columns = new LinkedList<ColumnDescription>();
		columnNames = new LinkedList<String>();
		indices = new LinkedList<IndexDescription>();
		columnJoins = new LinkedHashMap<String, String>();
		fks = new LinkedList<ForeignKeyDescription>();
		privs = new LinkedList<PrivilegeDescription>();
		}
	
	private boolean parseDBSchema(Attributes attr)
		{
		final String c = attr.getValue("catalog");
		final String s = attr.getValue("schema");
		
		if ((c != null) && (s != null))
			{
			schema = new SQLSchema(c, s);
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseTable(Attributes attr)
		{
		final String n = attr.getValue("name");
		
		if (n != null)
			{
			tableName = n;
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseColumn(Attributes attr)
		{
		final String n = attr.getValue("name");
		final String t = attr.getValue("type");
		final String w = attr.getValue("width");
		final String p = attr.getValue("precision");
		final String u = attr.getValue("nullable");
		final String d = attr.getValue("default");
		
		if ((n != null) && (t != null) && (w != null) && (u != null))
			{
			final int width;
			try	{
				width = Integer.parseInt(w);
				}
			catch (NumberFormatException e)
				{
				return (false);
				}
			final int prec;
			if (p == null)
				prec = 0;
			else
				{
				try	{
					prec = Integer.parseInt(p);
					}
				catch (NumberFormatException e)
					{
					return (false);
					}
				}
			final Integer type = ColumnType.parseTypeName(t);
			if (type == null)
				return (false);
			final ColumnDescription c = new ColumnDescription(n, "", type.intValue(), "", width, prec, parseBoolean(u), d);
			columns.add(c);
			return (true);
			}
		
		return (false);
		}
	
	private boolean parsePrimaryKey(Attributes attr)
		{
		final String n = attr.getValue("name");
		
		if (n != null)
			{
			name = n;
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseIndex(Attributes attr)
		{
		final String n = attr.getValue("name");
		final String u = attr.getValue("unique");
		
		if ((n != null) && (u != null))
			{
			name = n;
			unique = parseBoolean(u);
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseColumnRef(Attributes attr)
		{
		final String n = attr.getValue("name");
		
		if (n != null)
			{
			columnNames.add(n);
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseForeignKey(Attributes attr)
		{
		final String n = attr.getValue("name");
		final String c = attr.getValue("catalog");
		final String s = attr.getValue("schema");
		final String t = attr.getValue("table");
		
		if ((n != null) /*&& (c != null) && (s != null)*/ && (t != null))
			{
			name = n;
			refCatalog = c;
			refSchema = s;
			refTable = t;
			return (true);
			}
		
		return (false);
		}
	
	private boolean parseColumnJoin(Attributes attr)
		{
		final String s = attr.getValue("source");
		final String t = attr.getValue("target");
		
		if ((s != null) && (t != null))
			{
			columnJoins.put(s, t);
			return (true);
			}
		
		return (false);
		}
	
	private boolean parsePrivilege(Attributes attr)
		{
		final String n = attr.getValue("privilege");
		final String c = attr.getValue("grantor");
		final String s = attr.getValue("grantee");
		final String u = attr.getValue("grantable");
		
		if ((n != null) /*&& (c != null)*/ && (s != null) && (u != null))
			{
			name = n;
			refCatalog = c;
			refSchema = s;
			unique = parseBoolean(u);
			return (true);
			}
		
		return (false);
		}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attr)
		{
		switch (state.getState())
			{
			case STATE_UNDEF:
				if (localName.equals("DBSchema"))
					{
					if (parseDBSchema(attr))
						state.transit(STATE_DBSCHEMA);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_DBSCHEMA:
				if (localName.equals("Table"))
					{
					if (parseTable(attr))
						{
						state.transit(STATE_TABLE);
						pk = null;
						columns.clear();
						indices.clear();
						fks.clear();
						privs.clear();
						}
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_TABLE:
				if (localName.equals("Columns"))
					state.transit(STATE_COLUMNS);
				else if (localName.equals("PrimaryKey"))
					{
					if (parsePrimaryKey(attr))
						{
						state.transit(STATE_PRIMARYKEY);
						columnNames.clear();
						}
					else
						ignore++;
					}
				else if (localName.equals("Indices"))
					state.transit(STATE_INDICES);
				else if (localName.equals("ForeignKeys"))
					state.transit(STATE_FOREIGNKEYS);
				else if (localName.equals("Privileges"))
					state.transit(STATE_PRIVILEGES);
				else
					ignore++;
				break;
			case STATE_COLUMNS:
				if (localName.equals("Column"))
					{
					if (parseColumn(attr))
						state.transit(STATE_COLUMN);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_PRIMARYKEY:
				if (localName.equals("ColumnRef"))
					{
					if (parseColumnRef(attr))
						state.transit(STATE_COLUMNREF);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_INDICES:
				if (localName.equals("Index"))
					{
					if (parseIndex(attr))
						{
						state.transit(STATE_INDEX);
						columnNames.clear();
						}
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_INDEX:
				if (localName.equals("ColumnRef"))
					{
					if (parseColumnRef(attr))
						state.transit(STATE_COLUMNREF);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_FOREIGNKEYS:
				if (localName.equals("ForeignKey"))
					{
					if (parseForeignKey(attr))
						{
						state.transit(STATE_FOREIGNKEY);
						columnJoins.clear();
						}
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_FOREIGNKEY:
				if (localName.equals("ColumnJoin"))
					{
					if (parseColumnJoin(attr))
						state.transit(STATE_COLUMNJOIN);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_PRIVILEGES:
				if (localName.equals("Privilege"))
					{
					if (parsePrivilege(attr))
						state.transit(STATE_PRIVILEGE);
					else
						ignore++;
					}
				else
					ignore++;
				break;
			case STATE_COLUMN:
			case STATE_COLUMNREF:
			case STATE_COLUMNJOIN:
			case STATE_PRIVILEGE:
				ignore++;
				break;
			}
		}
	
	@Override
	public void endElement(String uri, String localName, String qName)
		{
		if (ignore > 0)
			{
			ignore--;
			return;
			}
		
		switch (state.getState())
			{
			case STATE_TABLE:
				if (pattern.matcher(name).matches())
					schema.addTable(new TableDescription(schema.getCatalog(), schema.getSchema(), tableName, "", TableDescription.TABLE, pk, columns, indices, fks, null, privs));
				break;
			case STATE_PRIMARYKEY:
				pk = new PrimaryKeyDescription(name, columnNames);
				break;
			case STATE_INDEX:
				indices.add(new IndexDescription(name, unique, columnNames));
				break;
			case STATE_FOREIGNKEY:
				fks.add(new ForeignKeyDescription(name, refCatalog, refSchema, refTable, columnJoins));
				break;
			case STATE_PRIVILEGE:
				privs.add(new PrivilegeDescription(refCatalog, refSchema, name, unique));
				break;
			}
		
		state.back();
		}
	
	private void rebuildReferences()
		{
		for (TableDescription t : new ArrayList<TableDescription>(schema.getTables().values()))
			{
			for (ForeignKeyDescription fk : t.getReferencedKeys())
				{
				final TableDescription t2 = schema.getTable(fk.getTableName());
				if (t2 != null)
					{
					final Map<String, String> revColumns = new LinkedHashMap<String, String>();
					for (Map.Entry<String, String> ent : fk.getColumns().entrySet())
						revColumns.put(ent.getValue(), ent.getKey());
					
					final List<ForeignKeyDescription> rk = new ArrayList<ForeignKeyDescription>(t2.getReferencingKeys());
					rk.add(new ForeignKeyDescription(fk.getName(), t.getName().getCatalogName(), t.getName().getSchemaName(), t.getName().getObjectName(), revColumns));
					schema.replaceTable(new TableDescription(t2.getName().getCatalogName(), t2.getName().getSchemaName(), t2.getName().getObjectName(), t2.getComment(), t2.getType(), t2.getPrimaryKey(), t2.getColumns(), t2.getIndices(), t2.getReferencedKeys(), rk, t2.getPrivileges()));
					}
				}
			}
		}
	
	/**
	 * Parst eine Schemadatei
	 * @param is InputStream
	 * @param include Muster für Tabellennamen
	 * @return true bei Erfolg
	 * @throws SAXException Bei Parser-Fehlern
	 */
	public SQLSchema parseSchema(InputStream is, String include) throws SAXException
		{
		if (include == null)
			pattern = Pattern.compile(".*");
		else
			pattern = Pattern.compile(include.replace("_", ".").replace("%", ".*"));
		
		schema = null;
		
		parse(is);
		
		if (schema == null)
			throw new RuntimeException("invalid schema");
		
		rebuildReferences();
		
		return (schema);
		}
	
	/**
	 * Parst eine Schemadatei
	 * @param f Datei
	 * @param include Muster für Tabellennamen
	 * @return true bei Erfolg
	 * @throws IOException Bei IO-Fehlern
	 * @throws SAXException Bei Parser-Fehlern
	 */
	public SQLSchema parseSchema(File f, String include) throws IOException, SAXException
		{
		final FileInputStream fis = new FileInputStream(f);
		try	{
			return (parseSchema(fis, include));
			}
		finally
			{
			fis.close();
			}
		}
	}
