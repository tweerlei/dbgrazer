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
package de.tweerlei.dbgrazer.extension.sql.handler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Split SQL DML, DDL and PL/SQL statements according to SQL*Plus rules (SQL*Plus User's Guide, Chapter 5)
 * 
 * @author Robert Wruck
 */
public class SQLPlusStatementSplitter extends SimpleSQLHandler
	{
	private static enum Mode
		{
		INITIAL,
		SQLPLUS,
		SQL,
		PLSQL,
		CREATE
		}
	
	private static enum EndMode
		{
		BOL,
		RUN,
		TEXT
		}
	
	private static final Set<String> SQLPLUS_TOKENS;
	static
		{
		SQLPLUS_TOKENS = new HashSet<String>();
		SQLPLUS_TOKENS.add("ACC");
		SQLPLUS_TOKENS.add("ACCEPT");
		SQLPLUS_TOKENS.add("A");
		SQLPLUS_TOKENS.add("APPEND");
		SQLPLUS_TOKENS.add("ARCHIVE");
		SQLPLUS_TOKENS.add("ATTR");
		SQLPLUS_TOKENS.add("ATTRIBUTE");
		SQLPLUS_TOKENS.add("BRE");
		SQLPLUS_TOKENS.add("BREAK");
		SQLPLUS_TOKENS.add("BTI");
		SQLPLUS_TOKENS.add("BTITLE");
		SQLPLUS_TOKENS.add("C");
		SQLPLUS_TOKENS.add("CHANGE");
		SQLPLUS_TOKENS.add("CL");
		SQLPLUS_TOKENS.add("CLEAR");
		SQLPLUS_TOKENS.add("COL");
		SQLPLUS_TOKENS.add("COLUMN");
		SQLPLUS_TOKENS.add("COMP");
		SQLPLUS_TOKENS.add("COMPUTE");
		SQLPLUS_TOKENS.add("CONN");
		SQLPLUS_TOKENS.add("CONNECT");
		SQLPLUS_TOKENS.add("COPY");
		SQLPLUS_TOKENS.add("DEF");
		SQLPLUS_TOKENS.add("DEFINE");
		SQLPLUS_TOKENS.add("DEL");
		SQLPLUS_TOKENS.add("DESC");
		SQLPLUS_TOKENS.add("DESCRIBE");
		SQLPLUS_TOKENS.add("DISC");
		SQLPLUS_TOKENS.add("DISCONNECT");
		SQLPLUS_TOKENS.add("ED");
		SQLPLUS_TOKENS.add("EDIT");
//		SQLPLUS_TOKENS.add("EXEC");
//		SQLPLUS_TOKENS.add("EXECUTE");
		SQLPLUS_TOKENS.add("EXIT");
		SQLPLUS_TOKENS.add("GET");
		SQLPLUS_TOKENS.add("HELP");
		SQLPLUS_TOKENS.add("HO");
		SQLPLUS_TOKENS.add("HOST");
		SQLPLUS_TOKENS.add("I");
		SQLPLUS_TOKENS.add("INPUT");
		SQLPLUS_TOKENS.add("L");
		SQLPLUS_TOKENS.add("LIST");
		SQLPLUS_TOKENS.add("PASSW");
		SQLPLUS_TOKENS.add("PASSWORD");
		SQLPLUS_TOKENS.add("PAU");
		SQLPLUS_TOKENS.add("PAUSE");
		SQLPLUS_TOKENS.add("PRI");
		SQLPLUS_TOKENS.add("PRINT");
		SQLPLUS_TOKENS.add("PRO");
		SQLPLUS_TOKENS.add("PROMPT");
		SQLPLUS_TOKENS.add("QUIT");
		SQLPLUS_TOKENS.add("RECOVER");
		SQLPLUS_TOKENS.add("REM");
		SQLPLUS_TOKENS.add("REMARK");
		SQLPLUS_TOKENS.add("REPF");
		SQLPLUS_TOKENS.add("REPFOOTER");
		SQLPLUS_TOKENS.add("REPH");
		SQLPLUS_TOKENS.add("REPHEADER");
		SQLPLUS_TOKENS.add("R");
		SQLPLUS_TOKENS.add("RUN");
		SQLPLUS_TOKENS.add("SAV");
		SQLPLUS_TOKENS.add("SAVE");
		SQLPLUS_TOKENS.add("SET");
		SQLPLUS_TOKENS.add("SHO");
		SQLPLUS_TOKENS.add("SHOW");
		SQLPLUS_TOKENS.add("SHUTDOWN");
		SQLPLUS_TOKENS.add("SPO");
		SQLPLUS_TOKENS.add("SPOOL");
		SQLPLUS_TOKENS.add("STA");
		SQLPLUS_TOKENS.add("START");
		SQLPLUS_TOKENS.add("STARTUP");
		SQLPLUS_TOKENS.add("STORE");
		SQLPLUS_TOKENS.add("TIMI");
		SQLPLUS_TOKENS.add("TIMING");
		SQLPLUS_TOKENS.add("TTI");
		SQLPLUS_TOKENS.add("TTITLE");
		SQLPLUS_TOKENS.add("UNDEF");
		SQLPLUS_TOKENS.add("UNDEFINE");
		SQLPLUS_TOKENS.add("VAR");
		SQLPLUS_TOKENS.add("VARIABLE");
		SQLPLUS_TOKENS.add("WHENEVER");
		SQLPLUS_TOKENS.add("XQUERY");
		}
	
	private static final Set<String> PLSQL_TOKENS;
	static
		{
		PLSQL_TOKENS = new HashSet<String>();
		PLSQL_TOKENS.add("DECLARE");
		PLSQL_TOKENS.add("BEGIN");
		PLSQL_TOKENS.add("FUNCTION");
		PLSQL_TOKENS.add("LIBRARY");
		PLSQL_TOKENS.add("PACKAGE");
		PLSQL_TOKENS.add("PROCEDURE");
		PLSQL_TOKENS.add("TRIGGER");
		PLSQL_TOKENS.add("TYPE");
		}
	
	private static final Set<String> CREATE_TOKENS;
	static
		{
		CREATE_TOKENS = new HashSet<String>();
		CREATE_TOKENS.add("OR");
		CREATE_TOKENS.add("REPLACE");
		}
	
	private final boolean includeSeparator;
	private final StringBuilder sb;
	private final List<String> statements;
	private Mode mode;
	private EndMode endMode;
	
	/**
	 * Constructor
	 */
	public SQLPlusStatementSplitter()
		{
		this(new StringBuilder(), false);
		}
	
	/**
	 * Constructor
	 * @param includeSeparator Whether to include the statement separator in extracted statements
	 */
	public SQLPlusStatementSplitter(boolean includeSeparator)
		{
		this(new StringBuilder(), includeSeparator);
		}
	
	// hack to make the StringBuilder available as local var
	private SQLPlusStatementSplitter(StringBuilder sb, boolean includeSeparator)
		{
		super(sb);
		this.includeSeparator = includeSeparator;
		this.sb = sb;
		this.statements = new LinkedList<String>();
		this.mode = Mode.INITIAL;
		this.endMode = EndMode.BOL;
		}
	
	private void addStatement(String s)
		{
		final String t = s.trim();
		if (t.length() > 0)
			statements.add(t);
		this.mode = Mode.INITIAL;
		this.endMode = EndMode.BOL;
		}
	
	/**
	 * Get the extracted statements
	 * @return Statements
	 */
	public List<String> getStatements()
		{
		if (sb.length() > 0)
			{
			addStatement(sb.toString());
			sb.setLength(0);
			}
		return (statements);
		}
	
	@Override
	public void handleOperator(String token, int level)
		{
		if (mode == Mode.SQL && token.equals(";"))
			{
			if (includeSeparator)
				sb.append(token);
			addStatement(sb.toString());
			sb.setLength(0);
			}
		else if (mode == Mode.PLSQL && endMode == EndMode.BOL && token.equals("/"))
			{
			endMode = EndMode.RUN;
			}
		else
			{
			super.handleOperator(token, level);
			endMode = EndMode.TEXT;
			}
		}
	
	@Override
	public void handleName(String token, int level)
		{
		if (mode == Mode.INITIAL)
			{
			final String word = token.toUpperCase();
			
			if (SQLPLUS_TOKENS.contains(word))
				mode = Mode.SQLPLUS;
			else if (PLSQL_TOKENS.contains(word))
				mode = Mode.PLSQL;
			else if ("CREATE".equals(word))
				mode = Mode.CREATE;
			else
				mode = Mode.SQL;
			}
		else if (mode == Mode.CREATE)
			{
			final String word = token.toUpperCase();
			
			if (PLSQL_TOKENS.contains(word))
				mode = Mode.PLSQL;
			else if (!CREATE_TOKENS.contains(word))
				mode = Mode.SQL;
			}
		
		endMode = EndMode.TEXT;
		super.handleName(token, level);
		}
	
	@Override
	public void handleSpace(String token, int level)
		{
		if (token.indexOf('\n') >= 0)
			{
			if (mode == Mode.SQLPLUS)
				{
//				addStatement(sb.toString());
				mode = Mode.INITIAL;
				endMode = EndMode.BOL;
				sb.setLength(0);
				return;
				}
			
			switch (endMode)
				{
				case BOL:
					break;
				case TEXT:
					endMode = EndMode.BOL;
					break;
				case RUN:
					addStatement(sb.toString());
					sb.setLength(0);
					return;
				}
			}
		
		super.handleSpace(token, level);
		}
	}
