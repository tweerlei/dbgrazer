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
package de.tweerlei.dbgrazer.text.backend;

import java.util.Date;
import java.util.List;

/**
 * Format a diff
 * 
 * @author Robert Wruck
 */
public interface DiffFormatter
	{
	/**
	 * Format a diff
	 * @param l LHS
	 * @param r RHS
	 * @param diff Differences
	 * @param lname Name of LHS
	 * @param rname Name of RHS
	 * @param ldate Date of LHS
	 * @param rdate Date of RHS
	 * @return Formatted diff
	 */
	public String formatDiff(List<String> l, List<String> r, List<Hunk> diff, String lname, String rname, Date ldate, Date rdate);
	}
