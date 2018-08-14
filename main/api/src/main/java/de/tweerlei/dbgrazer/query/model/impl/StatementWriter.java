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

import java.io.IOException;
import java.io.Writer;

import de.tweerlei.dbgrazer.query.model.StatementHandler;

/**
 * Write to a StringBuilder
 * 
 * @author Robert Wruck
 */
public class StatementWriter implements StatementHandler
	{
	private final Writer w;
	private final String eol;
	
	/**
	 * Constructor
	 * @param w Writer
	 * @param eol Statement terminator
	 */
	public StatementWriter(Writer w, String eol)
		{
		this.w = w;
		this.eol = eol;
		}
	
	/**
	 * Constructor
	 * @param w Writer
	 */
	public StatementWriter(Writer w)
		{
		this(w, "");
		}
	
	@Override
	public void startStatements()
		{
		}
	
	@Override
	public void statement(String stmt)
		{
		try	{
			w.write(stmt);
			w.write(eol);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void comment(String comment)
		{
		if (comment == null)
			return;
		
		final StringBuilder sb = new StringBuilder();
		
		for (String line : comment.split("\n"))
			{
			sb.append("-- ");
			sb.append(line);
			sb.append("\n");
			}
		
		try	{
			w.write(sb.toString());
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endStatements()
		{
		}
	
	@Override
	public void error(RuntimeException e)
		{
		comment(e.getMessage());
		}
	}
