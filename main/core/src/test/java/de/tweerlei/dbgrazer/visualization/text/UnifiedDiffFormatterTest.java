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
package de.tweerlei.dbgrazer.visualization.text;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import de.tweerlei.dbgrazer.text.backend.Hunk;
import de.tweerlei.dbgrazer.text.backend.impl.UnifiedDiffFormatter;

/**
 * Tests for Unified diff format
 * 
 * @author Robert Wruck
 */
public class UnifiedDiffFormatterTest extends TestCase
	{
	/**
	 * Test formatDiff
	 */
	public void testFormatDiff()
		{
		final UnifiedDiffFormatter fmt = new UnifiedDiffFormatter();
		
		final List<String> lhs = Arrays.asList("first line", "second line", "third line", "fourth line", "fifth line", "sixth line", "seventh line", "eighth line", "ninth line", "tenth line", "eleventh line", "twelwth line");
		final List<String> rhs = Arrays.asList("first line", "2nd line", "third line", "4th line", "sixth line", "seventh line", "eighth line", "ninth line", "tenth line", "10.5th line", "11th line", "twelwth line");
		
		final List<Hunk> diff = Arrays.asList(new Hunk(1, 5, 1, 4), new Hunk(10, 11, 9, 11));
		
		assertEquals(""
				+"--- a.txt 2033-05-18 03:33:20.000 +0000\n"
				+"+++ b.txt 2001-09-09 01:46:40.000 +0000\n"
				+"@@ 1,8 1,7\n"
				+" first line\n"
				+"-second line\n"
				+"-third line\n"
				+"-fourth line\n"
				+"-fifth line\n"
				+"+2nd line\n"
				+"+third line\n"
				+"+4th line\n"
				+" sixth line\n"
				+" seventh line\n"
				+" eighth line\n"
				+"@@ 8,5 7,6\n"
				+" eighth line\n"
				+" ninth line\n"
				+" tenth line\n"
				+"-eleventh line\n"
				+"+10.5th line\n"
				+"+11th line\n"
				+" twelwth line\n",
				fmt.formatDiff(lhs, rhs, diff, "a.txt", "b.txt", new Date(2000000000000L), new Date(1000000000000L)));
		}
	}
