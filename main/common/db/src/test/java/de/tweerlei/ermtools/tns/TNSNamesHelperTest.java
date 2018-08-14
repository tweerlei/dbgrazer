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
package de.tweerlei.ermtools.tns;

import de.tweerlei.ermtools.tns.TNSNamesHelper;
import junit.framework.TestCase;

/**
 * Tests for TNSNamesHelper
 * 
 * @author Robert Wruck
 */
public class TNSNamesHelperTest extends TestCase
	{
	/**
	 * Test conversion from TNSName to JDBC URL
	 */
	public void testToURL()
		{
		assertEquals("jdbc:oracle:thin:@dbhost.example.com:1521/MYORADB", TNSNamesHelper.getJdbcURL("# first line\r\n\r\n"
				+"MYORADB = # this comment must be ignored\n"
				+" (DESCRIPTION = \n"
				+"   (ADDRESS = (PROTOCOL = TCP)(HOST = dbhost.example.com)(PORT = 1521)) \n"
				+"   (CONNECT_DATA = \n"
				+"   # this comment must be ignored\n"
				+"     (SERVER = DEDICATED) \n"
				+"     (SERVICE_NAME = MYORADB) \n"
				+"   ) \n"
				+" ) "
				));
		}
	
	/**
	 * Test conversion from JDBC URL to TNSName
	 */
	public void testToTNSName()
		{
		assertEquals("MYORADB = (\n"
				+"\tDESCRIPTION = (\n"
				+"\t\tADDRESS = (\n"
				+"\t\t\tHOST = dbhost.example.com\n"
				+"\t\t)(\n"
				+"\t\t\tPORT = 1521\n"
				+"\t\t)(\n"
				+"\t\t\tPROTOCOL = TCP\n"
				+"\t\t)\n"
				+"\t)(\n"
				+"\t\tCONNECT_DATA = (\n"
				+"\t\t\tSERVER = DEDICATED\n"
				+"\t\t)(\n"
				+"\t\t\tSERVICE_NAME = MYORADB\n"
				+"\t\t)\n"
				+"\t)\n"
				+")\n", TNSNamesHelper.getTNSName("MYORADB", "jdbc:oracle:thin:@dbhost.example.com:1521/MYORADB"));
		}
	
	/**
	 * Test conversion from JDBC URL to TNSName
	 */
	public void testToTNSNameSID()
		{
		assertEquals("MYORADB = (\n"
				+"\tDESCRIPTION = (\n"
				+"\t\tADDRESS = (\n"
				+"\t\t\tHOST = dbhost.example.com\n"
				+"\t\t)(\n"
				+"\t\t\tPORT = 1521\n"
				+"\t\t)(\n"
				+"\t\t\tPROTOCOL = TCP\n"
				+"\t\t)\n"
				+"\t)(\n"
				+"\t\tCONNECT_DATA = (\n"
				+"\t\t\tSERVER = DEDICATED\n"
				+"\t\t)(\n"
				+"\t\t\tSID = COR99TS\n"
				+"\t\t)\n"
				+"\t)\n"
				+")\n", TNSNamesHelper.getTNSName("MYORADB", "jdbc:oracle:thin:@dbhost.example.com:1521:COR99TS"));
		}
	}
