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
package de.tweerlei.dbgrazer.text.service;

import java.util.Date;

/**
 * Calculate diffs between multi-line Strings
 * 
 * @author Robert Wruck
 */
public interface TextDiffService
	{
	/**
	 * Generate diff output by comparing two Strings
	 * @param lhs LHS
	 * @param rhs RHS
	 * @param lname LHS name
	 * @param rname RHS name
	 * @param ldate LHS date (optional)
	 * @param rdate RHS date (optional)
	 * @return Formatted diff
	 */
	public String diff(String lhs, String rhs, String lname, String rname, Date ldate, Date rdate);
	}
