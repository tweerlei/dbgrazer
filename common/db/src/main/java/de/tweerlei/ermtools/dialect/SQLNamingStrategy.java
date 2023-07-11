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
package de.tweerlei.ermtools.dialect;

import de.tweerlei.common5.jdbc.model.QualifiedName;

/**
 * Compare schema objects
 * 
 * @author Robert Wruck
 */
public interface SQLNamingStrategy
	{
	/**
	 * Get the name to use for matching tables
	 * @param qn Table name
	 * @return Table name
	 */
	public String getQualifiedTableName(QualifiedName qn);
	
	/**
	 * Get the name to use for matching columns
	 * @param c Column name
	 * @return Column name
	 */
	public String quoteIdentifier(String c);
	}
