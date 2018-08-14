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
package de.tweerlei.dbgrazer.query.model.impl;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.model.StatementHandler;

/**
 * StatementHandler that notifies a ProgressMonitor about processed statements
 * 
 * @author Robert Wruck
 */
public class MonitoringStatementHandler implements StatementHandler
	{
	private final StatementHandler h;
	private final ProgressMonitor p;
	
	/**
	 * Constructor
	 * @param h Delegate StatementHandler
	 * @param p ProgressMonitor
	 */
	public MonitoringStatementHandler(StatementHandler h, ProgressMonitor p)
		{
		this.h = h;
		this.p = p;
		}
	
	@Override
	public void startStatements()
		{
		h.startStatements();
		}
	
	@Override
	public void statement(String stmt)
		{
		if (!p.progress(1))
			throw new CancelledByUserException();
		h.statement(stmt);
		}
	
	@Override
	public void comment(String comment)
		{
		h.comment(comment);
		}
	
	@Override
	public void endStatements()
		{
		h.endStatements();
		}
	
	@Override
	public void error(RuntimeException e)
		{
		h.error(e);
		}
	}
