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
package de.tweerlei.dbgrazer.extension.sql;

import de.tweerlei.dbgrazer.extension.sql.handler.SimpleSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;
import junit.framework.TestCase;

/**
 * Tests for SQLFormatter
 * 
 * @author Robert Wruck
 */
public class SimpleSQLHandlerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testFormatSQL()
		{
		testSQL("");
		
		testSQL("SELECT col2, COUNT(*) FROM table WHERE col1 = 42 GROUP BY col2 HAVING COUNT(*) > 1 ORDER BY col2");
		
		testSQL("SELECT /*+ parallel */ col2, COUNT(*) FROM table WHERE col1 = :value GROUP BY col2 HAVING COUNT(*) > 1 ORDER BY col2");
		
		testSQL("SELECT a, b, c FROM (SELECT d, e, f FROM (SELECT g, h, i FROM schema.table WHERE j = 2) o JOIN (SELECT k, l FROM schema.tbl2, schema.tbl3 WHERE m = 3) n ON (o.g = n.k))");
		
		testSQL("SELECT a, b, -- comment\nc, d FROM t1, t2, -- comment\nt3, t4 WHERE a = 0 AND b = 1 -- comment\n AND (c = 2 OR c = 3) AND (d = 3 OR d = 4) GROUP BY a, b, -- comment\nc, d ORDER BY a, b, -- comment\nc, d HAVING a = 0 AND b = 1 -- comment\n AND (c = 2 OR c = 3) AND (d = 3 OR d = 4)");
		
		testSQL("SELECT\r\n-- column a\r\na,\r\n-- column b\r\nb,-- column c\r\nc\r\nFROM DUAL");
		
		testSQL("SELECT 'first '||col1||', second'||col2||'it''s' FROM DUAL");
		
		testSQL("SELECT a, b, MIN(c) KEEP (DENSE_RANK LAST ORDER BY d) AS c FROM tablename WHERE a<0 ORDER BY b");
		
		testSQL("SELECT a, b, MIN(c) KEEP (DENSE_RANK LAST ORDER BY d) AS c FROM (SELECT a, b, MIN(c) KEEP (DENSE_RANK LAST ORDER BY d) AS c FROM innertable ORDER BY a) t WHERE a<0 ORDER BY b");
		
		testSQL("SELECT a, b, CASE c WHEN 1 THEN 2 ELSE 3 END d FROM tbl");
		}
	
	/**
	 * Test format()
	 */
	public void testFormatPLSQL()
		{
		testSQL("DECLARE n1 NUMBER := 42; t1 VARCHAR2(20) := 'test'; BEGIN SELECT 1 INTO n1 FROM DUAL; COMMIT; EXCEPTION WHEN NO_DATA_FOUND THEN ROLLBACK; WHEN OTHERS THEN ROLLBACK; END;");
		
		testSQL("BEGIN CASE WHEN a THEN NULL; WHEN b THEN NULL; ELSE NULL; END CASE; END;");
		
		testSQL("BEGIN IF test = 1 THEN NULL; ELSIF test = 2 THEN NULL; ELSE NULL; END IF; END;");
		
		testSQL("BEGIN IF test = 1 THEN NULL; ELSIF test = 2 THEN EXIT WHEN tt = 0; IF tt = 3 THEN NULL; END IF; ELSE NULL; END IF; END;");
		
		testSQL("BEGIN FOR i IN 1..3 LOOP NULL; END LOOP; END;");
		
		testSQL("BEGIN WHILE (test = 42) LOOP NULL; END LOOP; END;");
		
		testSQL("BEGIN LOOP NULL; END LOOP; END;");
		
		testSQL("BEGIN proc2(p1 => 'test', p2 => 42, p3 => var1); END;");
		
		testSQL("BEGIN <<label>>LOOP IF (test = 0) THEN EXIT label; END IF; END LOOP; END;");
		
		testSQL("BEGIN IF a THEN null; ELSIF b THEN null; ELSE null; END IF; END;");
		
		testSQL("BEGIN CASE WHEN a THEN null; WHEN b THEN null; ELSE null; END CASE; END;");
		
		testSQL("BEGIN CASE WHEN a THEN null; WHEN b THEN IF x THEN null; END IF; ELSE null; END CASE; END;");
		
		testSQL("BEGIN -- before if\nIF a THEN -- do nothing\nnull; ELSIF -- in if\nb THEN null; ELSE null; END IF; -- after if\nEND;");
		
		testSQL("DECLARE PROCEDURE testproc (p1 NUMBER, p2 IN VARCHAR2, p3 IN OUT NOCOPY tbl%rowtype) IS counter NUMBER := 0; BEGIN -- increment counter\ncounter := counter + 1; END testproc; BEGIN NULL; END;");
		
		testSQL("DECLARE PROCEDURE testproc IS counter NUMBER := 0; BEGIN -- increment counter\ncounter := counter + 1; END testproc; BEGIN NULL; END;");
		
		testSQL("BEGIN /**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\n");
		
		testSQL("DECLARE TYPE r IS RECORD(v1 NUMBER(12,2), v2 VARCHAR2(100), v3 id_table); BEGIN NULL; END;");
		
		testSQL("BEGIN -- anonymous block\nBEGIN -- in block\nNULL; EXCEPTION WHEN OTHERS THEN NULL; END; -- after block\nEND;");
		
		testSQL("BEGIN LOOP /* multi\n   line \n comment   \n*/NULL; END LOOP; END;");
		
		testSQL("BEGIN LOOP -- multi\r\n -- line \r\n-- comment   \r\nNULL; END LOOP; END;");
		
		testSQL("BEGIN FOR i IN (SELECT a, b FROM tbl WHERE a < 0 AND b > 0) LOOP NULL; END LOOP; END;");
		
		testSQL("BEGIN a := CASE c WHEN 1 THEN 2 ELSE 3 END; END;");
		
		testSQL("BEGIN a := CASE WHEN 1 THEN 2 ELSE 3 END; END;");
		
		testSQL("DECLARE CURSOR c IS SELECT a FROM tbl; BEGIN NULL; END;");
		
		testSQL("DECLARE CURSOR c (p IN NUMBER) IS SELECT a FROM tbl; BEGIN NULL; END;");
		
		testSQL("DECLARE FUNCTION a RETURN NUMBER; PROCEDURE p; CURSOR c IS SELECT a FROM tbl; BEGIN NULL; END;");
		
		testSQL("BEGIN IF a IN (1, 2, 3) THEN NULL; END IF; END;");
		
		testSQL("BEGIN FORALL i IN 1..n UPDATE tbl SET col1 = 42 WHERE id = i; END;");
		
		testSQL("CREATE OR REPLACE PACKAGE pkg AS\r\nCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\nEND pkg;\r\n");
		}
	
	private void testSQL(String value)
		{
		final SimpleSQLHandler h = new SimpleSQLHandler();
		new SQLParser(h, true).parse(value);
		assertEquals(value, h.toString());
		}
	}
