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
package de.tweerlei.dbgrazer.extension.sql.printer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tweerlei.dbgrazer.extension.sql.handler.SQLPrinter;

/**
 * Consume SQL language tokens, counting brace levels
 * 
 * @author Robert Wruck
 */
public class SyntaxHighlightSQLPrinter implements SQLPrinter
	{
	private static final String STYLE_KEYWORD = "sql-word";
	private static final String STYLE_LITERAL = "sql-literal";
	private static final String STYLE_COMMENT = "sql-comment";
	private static final String STYLE_IDENTIFIER = "identifier";
	
	private static final Set<String> SQL_KEYWORDS;
	static
		{
		SQL_KEYWORDS = new HashSet<String>();
		
		// All PL/SQL reserved words (PL/SQL Language Reference, Table D-1)
		SQL_KEYWORDS.add("ALL");
		SQL_KEYWORDS.add("ALTER");
		SQL_KEYWORDS.add("AND");
		SQL_KEYWORDS.add("ANY");
		SQL_KEYWORDS.add("AS");
		SQL_KEYWORDS.add("ASC");
		SQL_KEYWORDS.add("AT");
		SQL_KEYWORDS.add("BEGIN");
		SQL_KEYWORDS.add("BETWEEN");
		SQL_KEYWORDS.add("BY");
		SQL_KEYWORDS.add("CASE");
		SQL_KEYWORDS.add("CHECK");
		SQL_KEYWORDS.add("CLUSTERS");
		SQL_KEYWORDS.add("CLUSTER");
		SQL_KEYWORDS.add("COLAUTH");
		SQL_KEYWORDS.add("COLUMNS");
		SQL_KEYWORDS.add("COMPRESS");
		SQL_KEYWORDS.add("CONNECT");
		SQL_KEYWORDS.add("CRASH");
		SQL_KEYWORDS.add("CREATE");
		SQL_KEYWORDS.add("CURSOR");
		SQL_KEYWORDS.add("DECLARE");
		SQL_KEYWORDS.add("DEFAULT");
		SQL_KEYWORDS.add("DESC");
		SQL_KEYWORDS.add("DISTINCT");
		SQL_KEYWORDS.add("DROP");
		SQL_KEYWORDS.add("ELSE");
		SQL_KEYWORDS.add("END");
		SQL_KEYWORDS.add("EXCEPTION");
		SQL_KEYWORDS.add("EXCLUSIVE");
		SQL_KEYWORDS.add("FETCH");
		SQL_KEYWORDS.add("FOR");
		SQL_KEYWORDS.add("FROM");
		SQL_KEYWORDS.add("FUNCTION");
		SQL_KEYWORDS.add("GOTO");
		SQL_KEYWORDS.add("GRANT");
		SQL_KEYWORDS.add("GROUP");
		SQL_KEYWORDS.add("HAVING");
		SQL_KEYWORDS.add("IDENTIFIED");
		SQL_KEYWORDS.add("IF");
		SQL_KEYWORDS.add("IN");
		SQL_KEYWORDS.add("INDEX");
		SQL_KEYWORDS.add("INDEXES");
		SQL_KEYWORDS.add("INSERT");
		SQL_KEYWORDS.add("INTERSECT");
		SQL_KEYWORDS.add("INTO");
		SQL_KEYWORDS.add("IS");
		SQL_KEYWORDS.add("LIKE");
		SQL_KEYWORDS.add("LOCK");
		SQL_KEYWORDS.add("MINUS");
		SQL_KEYWORDS.add("MODE");
		SQL_KEYWORDS.add("NOCOMPRESS");
		SQL_KEYWORDS.add("NOT");
		SQL_KEYWORDS.add("NOWAIT");
		SQL_KEYWORDS.add("NULL");
		SQL_KEYWORDS.add("OF");
		SQL_KEYWORDS.add("ON");
		SQL_KEYWORDS.add("OPTION");
		SQL_KEYWORDS.add("OR");
		SQL_KEYWORDS.add("ORDER");
		SQL_KEYWORDS.add("OVERLAPS");
		SQL_KEYWORDS.add("PROCEDURE");
		SQL_KEYWORDS.add("PUBLIC");
		SQL_KEYWORDS.add("RESOURCE");
		SQL_KEYWORDS.add("REVOKE");
		SQL_KEYWORDS.add("SELECT");
		SQL_KEYWORDS.add("SHARE");
		SQL_KEYWORDS.add("SIZE");
		SQL_KEYWORDS.add("SQL");
		SQL_KEYWORDS.add("START");
		SQL_KEYWORDS.add("SUBTYPE");
		SQL_KEYWORDS.add("TABAUTH");
		SQL_KEYWORDS.add("TABLE");
		SQL_KEYWORDS.add("THEN");
		SQL_KEYWORDS.add("TO");
		SQL_KEYWORDS.add("TYPE");
		SQL_KEYWORDS.add("UNION");
		SQL_KEYWORDS.add("UNIQUE");
		SQL_KEYWORDS.add("UPDATE");
		SQL_KEYWORDS.add("VALUES");
		SQL_KEYWORDS.add("VIEW");
		SQL_KEYWORDS.add("VIEWS");
		SQL_KEYWORDS.add("WHEN");
		SQL_KEYWORDS.add("WHERE");
		SQL_KEYWORDS.add("WITH");
		
		// Selected PL/SQL keywords (PL/SQL Language Reference, Table D-2)
		SQL_KEYWORDS.add("BODY");
		SQL_KEYWORDS.add("BULK");
		SQL_KEYWORDS.add("CLOSE");
		SQL_KEYWORDS.add("COLLECT");
		SQL_KEYWORDS.add("COMMENT");
		SQL_KEYWORDS.add("COMMIT");
		SQL_KEYWORDS.add("CONSTANT");
		SQL_KEYWORDS.add("CONTINUE");
		SQL_KEYWORDS.add("DELETE");
		SQL_KEYWORDS.add("DETERMINISTIC");
		SQL_KEYWORDS.add("ELSIF");
		SQL_KEYWORDS.add("EXCEPTIONS");
		SQL_KEYWORDS.add("EXECUTE");
		SQL_KEYWORDS.add("EXISTS");
		SQL_KEYWORDS.add("EXIT");
		SQL_KEYWORDS.add("FORALL");
		SQL_KEYWORDS.add("IMMEDIATE");
		SQL_KEYWORDS.add("LIMIT");
		SQL_KEYWORDS.add("LOOP");
		SQL_KEYWORDS.add("MODIFY");
		SQL_KEYWORDS.add("NOCOPY");
		SQL_KEYWORDS.add("OPEN");
		SQL_KEYWORDS.add("OTHERS");
		SQL_KEYWORDS.add("OUT");
		SQL_KEYWORDS.add("PACKAGE");
		SQL_KEYWORDS.add("PARALLEL_ENABLE");
		SQL_KEYWORDS.add("PARTITION");
		SQL_KEYWORDS.add("PIPELINED");
		SQL_KEYWORDS.add("PRAGMA");
		SQL_KEYWORDS.add("PRIOR");
		SQL_KEYWORDS.add("RAISE");
		SQL_KEYWORDS.add("RECORD");
		SQL_KEYWORDS.add("REF");
		SQL_KEYWORDS.add("RELIES_ON");
		SQL_KEYWORDS.add("RENAME");
		SQL_KEYWORDS.add("RESULT_CACHE");
		SQL_KEYWORDS.add("RETURN");
		SQL_KEYWORDS.add("RETURNING");
		SQL_KEYWORDS.add("ROLLBACK");
		SQL_KEYWORDS.add("SAVE");
		SQL_KEYWORDS.add("SAVEPOINT");
		SQL_KEYWORDS.add("SEQUENCE");
		SQL_KEYWORDS.add("SET");
		SQL_KEYWORDS.add("SOME");
		SQL_KEYWORDS.add("SUBPARTITION");
		SQL_KEYWORDS.add("SYNONYM");
		SQL_KEYWORDS.add("USING");
		SQL_KEYWORDS.add("WHILE");
		
		// Additional SQL reserved words (from V$RESERVED_WORDS)
		SQL_KEYWORDS.add("PCTFREE");
		SQL_KEYWORDS.add("TRIGGER");
		
		// Keywords commonly used in DML
		SQL_KEYWORDS.add("MERGE");
		SQL_KEYWORDS.add("LEFT");
		SQL_KEYWORDS.add("INNER");
		SQL_KEYWORDS.add("OUTER");
		SQL_KEYWORDS.add("RIGHT");
		SQL_KEYWORDS.add("JOIN");
		
		// Keywords commonly used in DDL
		SQL_KEYWORDS.add("REPLACE");
		
		// Keywords commonly used in PL/SQL
		SQL_KEYWORDS.add("TRUE");
		SQL_KEYWORDS.add("FALSE");
		SQL_KEYWORDS.add("ROWTYPE");
		SQL_KEYWORDS.add("NOTFOUND");
		}
	
	private final boolean uppercaseKeywords;
	
	/**
	 * Constructor
	 * @param uppercaseKeywords Uppercase keywords
	 */
	public SyntaxHighlightSQLPrinter(boolean uppercaseKeywords)
		{
		this.uppercaseKeywords = uppercaseKeywords;
		}
	
	@Override
	public String printName(String t)
		{
		final String keyword = t.toUpperCase();
		
		if (SQL_KEYWORDS.contains(keyword))
			return ("<span class=\"" + STYLE_KEYWORD + "\">" + (uppercaseKeywords ? keyword : t) + "</span>");
		
		return (t);
		}
	
	@Override
	public String printIdentifier(String t)
		{
		return ("<span class=\"" + STYLE_IDENTIFIER + "\">" + t + "</span>");
		}
	
	@Override
	public String printString(String t)
		{
		return ("<span class=\"" + STYLE_LITERAL + "\">'" + t.replace("'", "''") + "'</span>");
		}
	
	@Override
	public String printNumber(String t)
		{
		return ("<span class=\"" + STYLE_LITERAL + "\">" + t + "</span>");
		}
	
	@Override
	public String printOperator(String t)
		{
		return (textEncode(t));
		}
	
	@Override
	public List<String> printComment(List<String> lines)
		{
		if (lines.size() == 1)
			return (Collections.singletonList("<span class=\"" + STYLE_COMMENT + "\">/*" + textEncode(lines.get(0)) + "*/</span>"));
		
		final List<String> ret = new ArrayList<String>(lines.size());
		for (Iterator<String> i = lines.iterator(); i.hasNext(); )
			{
			final String line = i.next();
			if (ret.isEmpty())
				ret.add("<span class=\"" + STYLE_COMMENT + "\">/*" + textEncode(line) + "</span>");
			else if (i.hasNext())
				ret.add("<span class=\"" + STYLE_COMMENT + "\">" + textEncode(line) + "</span>");
			else
				ret.add("<span class=\"" + STYLE_COMMENT + "\">" + textEncode(line) + "*/</span>");
			}
		
		return (ret);
		}
	
	@Override
	public String printEOLComment(String comment)
		{
		return ("<span class=\"" + STYLE_COMMENT + "\">--" + textEncode(comment) + "</span>");
		}
	
	private String textEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
		}
	}
