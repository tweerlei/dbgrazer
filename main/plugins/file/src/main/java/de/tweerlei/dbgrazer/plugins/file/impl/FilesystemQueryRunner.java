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
package de.tweerlei.dbgrazer.plugins.file.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.FileUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.plugins.file.ConfigKeys;
import de.tweerlei.dbgrazer.plugins.file.types.DirsQueryType;
import de.tweerlei.dbgrazer.plugins.file.types.FilesQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.service.TimeService;

/**
 * Run filesystem queries
 * 
 * @author Robert Wruck
 */
@Service
public class FilesystemQueryRunner extends BaseQueryRunner
	{
	private final TimeService timeService;
	private final ConfigAccessor configService;
	private final KeywordService keywordService;
	private final LinkService linkService;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param configService ConfigAccessor
	 * @param keywordService KeywordService
	 * @param linkService LinkService
	 */
	@Autowired
	public FilesystemQueryRunner(TimeService timeService, ConfigAccessor configService,
			KeywordService keywordService, LinkService linkService)
		{
		super("Filesystem");
		this.timeService = timeService;
		this.configService = configService;
		this.keywordService = keywordService;
		this.linkService = linkService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return (t.getLinkType() instanceof FilesystemLinkType);
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final LinkDef def = linkService.getLinkData(link);
		if ((def == null) || !(def.getType() instanceof FilesystemLinkType))
			throw new PerformQueryException(query.getName(), new RuntimeException("Unknown link " + link));
		
		final Result res = new ResultImpl(query);
		
		final File base = new File(def.getUrl());
		if (!base.isDirectory())
			return (res);
		
		final String statement;
		try	{
			statement = keywordService.normalizePath(new ParamReplacer(params).replaceAll(query.getStatement()));
			}
		catch (RuntimeException e)
			{
			throw new PerformQueryException(query.getName(), e);
			}
		
		if (query.getType() instanceof DirsQueryType)
			readDir(res, base, query, subQueryIndex, statement, false);
		else if (query.getType() instanceof FilesQueryType)
			readDir(res, base, query, subQueryIndex, statement, true);
		else //if (query.getType() instanceof FileQueryType)
			readFile(res, base, query, subQueryIndex, statement);
		
		return (res);
		}
	
	private void readDir(Result res, File base, Query query, int subQueryIndex, String statement, boolean filesOnly) throws PerformQueryException
		{
		final boolean listHidden = configService.get(ConfigKeys.FILE_LIST_HIDDEN);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl("Name", ColumnType.STRING, null, query.getTargetQueries().get(0), null, null));
		columns.add(new ColumnDefImpl("Size", ColumnType.INTEGER, null, query.getTargetQueries().get(1), null, null));
		columns.add(new ColumnDefImpl("Last_Modified", ColumnType.DATE, null, query.getTargetQueries().get(2), null, null));
		columns.add(new ColumnDefImpl("Directory", ColumnType.BOOLEAN, null, query.getTargetQueries().get(3), null, null));
		columns.add(new ColumnDefImpl("Readable", ColumnType.BOOLEAN, null, query.getTargetQueries().get(4), null, null));
		columns.add(new ColumnDefImpl("Writable", ColumnType.BOOLEAN, null, query.getTargetQueries().get(5), null, null));
		columns.add(new ColumnDefImpl("Executable", ColumnType.BOOLEAN, null, query.getTargetQueries().get(6), null, null));
		columns.add(new ColumnDefImpl("Hidden", ColumnType.BOOLEAN, null, query.getTargetQueries().get(7), null, null));
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		
		final long start = timeService.getCurrentTime();
		
		final File dir = new File(base, statement);
		final int prefix = base.getAbsolutePath().length();
		try	{
			if (dir.isDirectory())
				{
				final File[] files = dir.listFiles();
				if (files == null)
					throw new IOException("Can't read directory " + statement);
				
				for (File f : dir.listFiles())
					{
					if (f.getName().equals(".") || f.getName().equals(".."))
						continue;
					
					if (!listHidden && f.isHidden())
						continue;
					
					if (f.isFile() != filesOnly)
						continue;
					
					rs.getRows().add(new DefaultResultRow(
							f.getAbsolutePath().substring(prefix).replace(File.separatorChar, '/'),
							f.length(),
							new Date(f.lastModified()),
							f.isDirectory(),
							f.canRead(),
							f.canWrite(),
							f.canExecute(),
							f.isHidden()));
					}
				}
			}
		catch (IOException e)
			{
			throw new PerformQueryException(query.getName(), new RuntimeException("readDir", e));
			}
		
		final long end = timeService.getCurrentTime();
		rs.setQueryTime(end - start);
		
		res.getRowSets().put(query.getName(), rs);
		}
	
	private void readFile(Result res, File base, Query query, int subQueryIndex, String statement) throws PerformQueryException
		{
		final String charset = configService.get(ConfigKeys.FILE_CHARSET);
		final int maxSize = configService.get(ConfigKeys.FILE_MAX_SIZE);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl("Content", ColumnType.STRING, null, query.getTargetQueries().get(0), null, null));
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		
		final long start = timeService.getCurrentTime();
		
		final File file = new File(base, statement);
		try	{
			if (file.isFile() && file.canRead())
				{
				final String content = FileUtils.readFile(file, charset, maxSize);
				
				rs.getRows().add(new DefaultResultRow(content));
				}
			}
		catch (IOException e)
			{
			throw new PerformQueryException(query.getName(), new RuntimeException("readFile", e));
			}
		
		final long end = timeService.getCurrentTime();
		rs.setQueryTime(end - start);
		if (file.length() > maxSize)
			rs.setMoreAvailable(true);
		
		res.getRowSets().put(query.getName(), rs);
		}
	}
