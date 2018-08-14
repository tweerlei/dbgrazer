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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.jdbc.RowCountService;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Count table rows
 * 
 * @author Robert Wruck
 */
@Service
public class RowCountServiceImpl implements RowCountService
	{
	private final QueryPerformerService runner;
	private final SQLGeneratorService sqlGenerator;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param runner QueryPerformerService
	 * @param sqlGenerator SQLGeneratorService
	 */
	@Autowired
	public RowCountServiceImpl(QueryPerformerService runner, SQLGeneratorService sqlGenerator)
		{
		this.runner = runner;
		this.sqlGenerator = sqlGenerator;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public Object countRows(String conn, QualifiedName table, SQLDialect dialect)
		{
		final String statement = sqlGenerator.generateSelectCount(table, Style.INDENTED, null, dialect);
		
		try	{
			final Result r = runner.performCustomQuery(conn, JdbcConstants.QUERYTYPE_MULTIPLE, statement, null, null, table.getObjectName(), false, null);
			return (r.getFirstRowSet().getFirstValue());
			}
		catch (PerformQueryException e)
			{
			logger.log(Level.WARNING, "countRows", e.getCause());
			return (null);
			}
		}
	
	@Override
	public Map<QualifiedName, Object> countRows(String conn, Set<QualifiedName> tables, SQLDialect dialect)
		{
		final Map<QualifiedName, Object> ret = new TreeMap<QualifiedName, Object>();
		
		for (QualifiedName qn : tables)
			ret.put(qn, countRows(conn, qn, dialect));
		
		return (ret);
		}
	
	@Override
	public Map<String, RowCounts> mergeRowCounts(Map<QualifiedName, Object> src, Map<QualifiedName, Object> dst, SQLDialect dialect, boolean qualified)
		{
		final Map<String, RowCounts> ret = new TreeMap<String, RowCounts>();
		
		for (Map.Entry<QualifiedName, Object> ent : src.entrySet())
			{
			final String key = qualified ? dialect.getQualifiedTableName(ent.getKey()) : ent.getKey().getObjectName();
			ret.put(key, new RowCounts(ent.getKey(), ent.getValue(), null, null));
			}
		
		if (dst != null)
			{
			for (Map.Entry<QualifiedName, Object> ent : dst.entrySet())
				{
				final String key = qualified ? dialect.getQualifiedTableName(ent.getKey()) : ent.getKey().getObjectName();
				final RowCounts rc = ret.get(key);
				if (key != null)
					{
					rc.setDstName(ent.getKey());
					rc.setDstCount(ent.getValue());
					}
				else
					ret.put(key, new RowCounts(null, null, ent.getKey(), ent.getValue()));
				}
			}
		
		return (ret);
		}
	}
