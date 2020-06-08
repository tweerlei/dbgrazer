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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlProvider;

import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultRowMapper;

/**
 * StatementHandler that executes passed statements
 * 
 * @author Robert Wruck
 */
public class PreparedStatementHandler implements CountingRowHandler
	{
	private final PreparedStatement ps;
	private final PreparedStatementCreatorFactory factory;
	private final ResultRowMapper mapper;
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
	 * @param ps PreparedStatement
	 * @param factory PreparedStatementCreatorFactory
	 * @param mapper ResultRowMapper
	 * @param commitSize Perform a COMMIT after this number of rows (0 = never)
	 * @param monitor DMLProgressMonitor
	 * @param rollback Perform a ROLLBACK instead of COMMIT
	 * @param ignoreErrors Catch any SQLException and save its message
	 */
	public PreparedStatementHandler(PreparedStatement ps, PreparedStatementCreatorFactory factory, ResultRowMapper mapper, int commitSize, DMLProgressMonitor monitor, boolean rollback, boolean ignoreErrors)
		{
		this.ps = ps;
		this.factory = factory;
		this.mapper = mapper;
		this.commitSize = commitSize;
		this.monitor = monitor;
		this.rollback = rollback;
		this.nextCommit = commitSize;
		this.ignoreErrors = ignoreErrors;
		this.errors = new StringBuilder();
		}
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		final ResultRow p = mapper.map(row);
		final PreparedStatementSetter pss = factory.newPreparedStatementSetter(p.getValues());
		
		try	{
			if ((monitor != null) && !monitor.getTotalStatements().progress(1))
				throw new CancelledByUserException();
			
			pss.setValues(ps);
			
			final int rows = ps.executeUpdate();
			
			if (monitor != null)
				monitor.getTotalRows().progress(rows);
			rowCount += rows;
			
			for (SQLWarning w = ps.getWarnings(); w != null; w = w.getNextWarning())
				{
				errors.append(w.getMessage());
				errors.append("\n");
				}
			
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
				throw new BadSqlGrammarException("statement", ((SqlProvider) pss).getSql(), e);
			}
		
		return (true);
		}
	
	@Override
	public void endRows()
		{
		try	{
			ps.close();
			}
		catch (SQLException e)
			{
			// TODO: Log?
			}
		}
	
	@Override
	public void error(RuntimeException e)
		{
		throw e;
		}
	
	@Override
	public int getTotalRowCount()
		{
		return (rowCount);
		}
	
	@Override
	public int getUncommittedRowCount()
		{
		return (rowCount - lastCommit);
		}
	
	@Override
	public String getErrors()
		{
		return (errors.toString());
		}
	}
