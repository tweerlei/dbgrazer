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
package de.tweerlei.ermtools.schema.matchers;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.ermtools.schema.ObjectMatcher;

/**
 * Strict privilege matching
 * 
 * @author Robert Wruck
 */
public class StrictPrivilegeMatcher implements ObjectMatcher<PrivilegeDescription>
	{
	public boolean equals(PrivilegeDescription a, PrivilegeDescription b)
		{
		if (!StringUtils.equals(a.getGrantor(), b.getGrantor()))
			return false;
		if (!StringUtils.equals(a.getGrantee(), b.getGrantee()))
			return false;
		if (!StringUtils.equals(a.getPrivilege(), b.getPrivilege()))
			return false;
		if (a.isGrantable() != b.isGrantable())
			return false;
		return true;
		}
	}
