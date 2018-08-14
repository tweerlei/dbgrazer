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
package de.tweerlei.ermtools.dialect.oracle;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import de.tweerlei.ermtools.dialect.SQLScriptOutputReader;

/**
 * Oracle DBMS_OUTPUT based impl.
 * 
 * @author Robert Wruck
 */
public class OracleScriptOutputReader implements SQLScriptOutputReader
	{
	private final Connection conn;
	private CallableStatement enableStmt;
	private CallableStatement disableStmt;
	private CallableStatement readStmt;
	
	/**
	 * Constructor
	 * @param conn Connection to use
	 */
	public OracleScriptOutputReader(Connection conn)
		{
		this.conn = conn;
		}
	
	public void enable() throws SQLException
		{
		if (enableStmt == null)
			enableStmt = conn.prepareCall("BEGIN DBMS_OUTPUT.ENABLE(NULL); END;");
		
		enableStmt.executeUpdate();
		}
	
	public void disable() throws SQLException
		{
		if (disableStmt == null)
			disableStmt = conn.prepareCall("BEGIN DBMS_OUTPUT.DISABLE; END;");
		
		disableStmt.executeUpdate();
		}
	
	public String readOutput() throws SQLException
		{
		if (readStmt == null)
			{
			readStmt = conn.prepareCall( 
					 "DECLARE\n"
					+"  l_line VARCHAR2(32767);\n"
					+"  l_done INTEGER;\n"
					+"  l_buffer LONG;\n"	// TODO: Use CLOB?
					+"BEGIN\n"
					+"  LOOP\n"
					+"    DBMS_OUTPUT.GET_LINE(l_line, l_done);\n"
					+"    EXIT WHEN l_done = 1;\n"
					+"    l_buffer := l_buffer || l_line || chr(10);\n"
					+"  END LOOP;\n"
					+"  :buffer := l_buffer;\n"
					+"END;");
			readStmt.registerOutParameter(1, Types.VARCHAR);
			}
		
		readStmt.executeUpdate();
		return (readStmt.getString(1));
		}
	
	public void close() throws SQLException
		{
		if (enableStmt != null)
			enableStmt.close();
		if (disableStmt != null)
			disableStmt.close();
		if (readStmt != null)
			readStmt.close();
		}
	}
