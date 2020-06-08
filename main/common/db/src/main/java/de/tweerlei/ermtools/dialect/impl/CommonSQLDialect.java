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
package de.tweerlei.ermtools.dialect.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.impl.JdbcMetadataReader;
import de.tweerlei.common5.jdbc.model.ColumnType;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLObjectDDLWriter;
import de.tweerlei.ermtools.dialect.SQLScriptOutputReader;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;

/**
 * DDL statements common to all known DBMS
 * 
 * @author Robert Wruck
 */
public abstract class CommonSQLDialect implements SQLDialect
	{
	private static final String EOL = ";\n";
	private static final String DATE_FORMAT = "'DATE'''yyyy-MM-dd''";
	private static final String TIME_FORMAT = "'TIME'''HH:mm:ss''";
	private static final String DATETIME_FORMAT = "'TIMESTAMP'''yyyy-MM-dd HH:mm:ss''";
	private static final String TIMESTAMP_FORMAT = "'TIMESTAMP'''yyyy-MM-dd HH:mm:ss.SSS''";
	
	private final Map<Integer, SQLDataType> typeMap;
	
	/**
	 * Constructor
	 * @param typeMap Map java.sql.Types to SQLDataType
	 */
	protected CommonSQLDialect(Map<Integer, SQLDataType> typeMap)
		{
		this.typeMap = typeMap;
		}
	
	public final String dataTypeToString(TypeDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		final SQLDataType t = typeMap.get(c.getType());
		
		if (!StringUtils.empty(c.getName()))
			sb.append(c.getName());
		else if (t != null)
			sb.append(t.getName());
		else
			{
			final String tn = ColumnType.getTypeName(c.getType());
			if (!StringUtils.empty(tn))
				sb.append(tn);
			else
				sb.append("type").append(c.getType());
			}
		
		if ((c.getLength() > 0) && ((t == null) || t.hasLength()))
			{
			sb.append("(");
			sb.append(c.getLength());
			if ((c.getDecimals() > 0) && ((t == null) || t.hasDecimals()))
				{
				sb.append(",");
				sb.append(c.getDecimals());
				}
			sb.append(")");
			}
		
		return (sb.toString());
		}
	
	public SQLDataType getSQLDataType(int type)
		{
		return (typeMap.get(type));
		}
	
	public String getQualifiedTableName(QualifiedName qn)
		{
		final StringBuilder sb = new StringBuilder();
		
		// Apply only one prefix, prefer schema over catalog
		if (!StringUtils.empty(qn.getSchemaName()))
			sb.append(qn.getSchemaName()).append(".");
		else if (!StringUtils.empty(qn.getCatalogName()))
			sb.append(qn.getCatalogName()).append(".");
		
		sb.append(qn.getObjectName());
		
		return (sb.toString());
		}
	
	public String getStatementTerminator()
		{
		return (EOL);
		}
	
	public String getDateFormat()
		{
		return (DATE_FORMAT);
		}
	
	public String getTimeFormat()
		{
		return (TIME_FORMAT);
		}
	
	public String getDatetimeFormat()
		{
		return (DATETIME_FORMAT);
		}
	
	public String getTimestampFormat()
		{
		return (TIMESTAMP_FORMAT);
		}
	
	public boolean supportsBoolean()
		{
		return (false);
		}
	
	public boolean supportsMerge()
		{
		return (false);
		}
	
	public boolean dmlRequiresTerminator()
		{
		return (false);
		}
	
	public String getDefaultTableName()
		{
		return (null);
		}
	
	public String prepareInsert(TableDescription t)
		{
		return (null);
		}
	
	public String finishInsert(TableDescription t)
		{
		return (null);
		}
	
	public String dropTable(TableDescription t)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE ");
		sb.append(t.getName().getObjectName());
		
		return (sb.toString());
		}
	
	public String grantPrivilege(TableDescription t, PrivilegeDescription p)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("GRANT ");
		sb.append(p.getPrivilege());
		sb.append(" ON ");
		sb.append(t.getName().getObjectName());
		sb.append(" TO ");
		sb.append(p.getGrantee());
		
		return (sb.toString());
		}
	
	public String revokePrivilege(TableDescription t, PrivilegeDescription p)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("REVOKE ");
		sb.append(p.getPrivilege());
		sb.append(" ON ");
		sb.append(t.getName().getObjectName());
		sb.append(" FROM ");
		sb.append(p.getGrantee());
		
		return (sb.toString());
		}
	
	public MetadataReader getMetadataReader(Connection c) throws SQLException
		{
		return (new JdbcMetadataReader(c.getMetaData()));
		}
	
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException
		{
		return (new DummySQLStatementAnalyzer());
		}
	
	public SQLScriptOutputReader getScriptOutputReader(Connection c) throws SQLException
		{
		return (new DummySQLScriptOutputReader());
		}
	
	public SQLObjectDDLWriter getObjectDDLWriter()
		{
		return (new DummySQLObjectDDLWriter());
		}
	}
