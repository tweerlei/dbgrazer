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
package de.tweerlei.dbgrazer.extension.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Manage database connections
 * 
 * @author Robert Wruck
 */
public interface DataAccessService
	{
	/**
	 * Get the JdbcTemplate for a link
	 * @param c Link name
	 * @return JdbcTemplate or null
	 */
	public JdbcTemplate getJdbcTemplate(String c);
	
	/**
	 * Get a JdbcTemplate for fetching an unlimited number of result rows
	 * @param c Link name
	 * @return JdbcTemplate or null
	 */
	public JdbcTemplate getUnlimitedJdbcTemplate(String c);
	
	/**
	 * Get a TransactionTemplate for a link
	 * @param c Link name
	 * @return TransactionTemplate or null
	 */
	public TransactionTemplate getTransactionTemplate(String c);
	
	/**
	 * Get a TransactionTemplate for a link that does not commit
	 * @param c Link name
	 * @return TransactionTemplate or null
	 */
	public TransactionTemplate getTestTransactionTemplate(String c);
	
	/**
	 * Get the SQLDialect for a link
	 * @param c Link name
	 * @return SQLDialect or null
	 */
	public SQLDialect getSQLDialect(String c);
	
	/**
	 * Get a statement to execute before each DML operation
	 * @param c Link name
	 * @return Statement
	 */
	public String getPreDMLStatement(String c);
	
	/**
	 * Get a statement to execute after each DML operation
	 * @param c Link name
	 * @return Statement
	 */
	public String getPostDMLStatement(String c);
	}
