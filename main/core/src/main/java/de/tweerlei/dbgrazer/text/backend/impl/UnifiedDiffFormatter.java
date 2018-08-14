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
package de.tweerlei.dbgrazer.text.backend.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.text.backend.DiffFormatter;
import de.tweerlei.dbgrazer.text.backend.Hunk;

/**
 * Unified diff format:
 * http://www.gnu.org/software/diffutils/manual/html_node/Detailed-Unified.html
 * 
 * @author Robert Wruck
 */
@Service("simpleDiffFormatter")
public class UnifiedDiffFormatter implements DiffFormatter
	{
	private static final String DIFF_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";
	private static final String EOL = "\n";
	private static final int CONTEXT_LINES = 3;
	
	@Override
	public String formatDiff(List<String> l, List<String> r, List<Hunk> diff, String lname, String rname, Date ldate, Date rdate)
		{
		final StringBuilder sb = new StringBuilder();
		final DateFormat df = new SimpleDateFormat(DIFF_DATE_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		sb.append("--- ").append(lname);
		if (ldate != null)
			sb.append(" ").append(df.format(ldate));
		sb.append(EOL);
		
		sb.append("+++ ").append(rname);
		if (rdate != null)
			sb.append(" ").append(df.format(rdate));
		sb.append(EOL);
		
		for (Hunk h : diff)
			{
			final int lstart = Math.max(0, h.getLeftStart() - CONTEXT_LINES);
			final int lend = Math.min(l.size(), h.getLeftEnd() + CONTEXT_LINES);
			final int rstart = Math.max(0, h.getRightStart() - CONTEXT_LINES);
			final int rend = Math.min(l.size(), h.getRightEnd() + CONTEXT_LINES);
			
			sb.append("@@ ").append(lstart + 1).append(",").append(lend - lstart).append(" ").append(rstart + 1).append(",").append(rend - rstart).append(EOL);
			
			// Context before
			for (int i = lstart; i < h.getLeftStart(); i++)
				sb.append(" ").append(l.get(i)).append(EOL);
			
			// Remove lines from LHS
			for (int i = h.getLeftStart(); i < h.getLeftEnd(); i++)
				sb.append("-").append(l.get(i)).append(EOL);
			
			// Add lines in RHS
			for (int i = h.getRightStart(); i < h.getRightEnd(); i++)
				sb.append("+").append(r.get(i)).append(EOL);
			
			// Context after
			for (int i = h.getLeftEnd(); i < lend; i++)
				sb.append(" ").append(l.get(i)).append(EOL);
			}
		
		return (sb.toString());
		}
	}
