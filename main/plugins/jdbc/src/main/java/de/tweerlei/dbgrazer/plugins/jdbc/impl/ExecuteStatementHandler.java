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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.BadSqlGrammarException;

import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.StatementHandler;

/**
 * StatementHandler that executes passed statements
 * 
 * @author Robert Wruck
 */
public class ExecuteStatementHandler implements StatementHandler
	{
	private final Statement ps;
	private final String eol;
	private final int commitSize;
	private final DMLProgressMonitor monitor;
	private final boolean rollback;
	private final boolean ignoreErrors;
	private final StringBuilder errors;
	private int rowCount;
	private int lastCommit;
	private int nextCommit;
	
	/**
	 * Constructor
	 * @param ps Statement
	 * @param eol Statement terminator
	 * @param commitSize Perform a COMMIT after this number of rows (0 = never)
	 * @param monitor DMLProgressMonitor
	 * @param rollback Perform a ROLLBACK instead of COMMIT
	 * @param ignoreErrors Catch any SQLException and save its message
	 */
	public ExecuteStatementHandler(Statement ps, String eol, int commitSize, DMLProgressMonitor monitor, boolean rollback, boolean ignoreErrors)
		{
		this.ps = ps;
		this.eol = eol;
		this.commitSize = commitSize;
		this.monitor = monitor;
		this.rollback = rollback;
		this.nextCommit = commitSize;
		this.ignoreErrors = ignoreErrors;
		this.errors = new StringBuilder();
		}
	
	@Override
	public void startStatements()
		{
		}
	
	@Override
	public void statement(String stmt)
		{
		try	{
			if ((monitor != null) && !monitor.getTotalStatements().progress(1))
				throw new CancelledByUserException();
			
			final int rows;
			if (eol != null)
				rows = ps.executeUpdate(stmt + eol);
			else
				rows = ps.executeUpdate(stmt);
			if (monitor != null)
				monitor.getTotalRows().progress(rows);
			rowCount += rows;
			
			if ((nextCommit > 0) && (rowCount >= nextCommit))
				{
				if (rollback)
					ps.getConnection().rollback();
				else
					ps.getConnection().commit();
				
				if (monitor != null)
					monitor.getCommittedRows().progress(rowCount - lastCommit);
				lastCommit = rowCount;
				nextCommit = rowCount + commitSize;
				}
			}
		catch (SQLException e)
			{
			if (monitor != null)
				monitor.getFailedStatements().progress(1);
			
			if (ignoreErrors)
				{
				errors.append(e.getMessage());
				errors.append("\n");
				}
			else
				throw new BadSqlGrammarException("statement", stmt, e);
			}
		}
	
	@Override
	public void comment(String comment)
		{
		errors.append(comment);
		errors.append("\n");
		}
	
	@Override
	public void endStatements()
		{
		}
	
	@Override
	public void error(RuntimeException e)
		{
		throw e;
		}
	
	/**
	 * Get the total number of rows affected by all statements so far
	 * @return Row count
	 */
	public int getTotalRowCount()
		{
		return (rowCount);
		}
	
	/**
	 * Get the number of uncommitted rows
	 * @return Row count
	 */
	public int getUncommittedRowCount()
		{
		return (rowCount - lastCommit);
		}
	
	/**
	 * Get any error messages
	 * @return Errors
	 */
	public String getErrors()
		{
		return (errors.toString());
		}
	}
