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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.dbgrazer.web.backend.FileDownloader;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;
import de.tweerlei.spring.web.view.ErrorDownloadSource;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class DownloadServiceImpl implements DownloadService
	{
	private final QueryPerformerService runner;
	private final Logger logger;
	private final Map<String, FileDownloader> formats;
	
	/**
	 * Constructor
	 * @param runner QueryPerformerService
	 * @param formats FileDownloaders
	 */
	@Autowired(required = false)
	public DownloadServiceImpl(QueryPerformerService runner, Set<FileDownloader> formats)
		{
		this.runner = runner;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<FileDownloader>(formats));
		
		this.logger.log(Level.INFO, "File downloaders: " + this.formats);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 * @param runner QueryPerformerService
	 */
	@Autowired(required = false)
	public DownloadServiceImpl(QueryPerformerService runner)
		{
		this(runner, Collections.<FileDownloader>emptySet());
		}
	
	@Override
	public Set<String> getSupportedDownloadFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public DownloadSource getDownloadSource(String link, RowSet rs, String tableName, Set<Integer> pk, SQLDialect dialect, String format)
		{
		final FileDownloader c = getFileDownloader(format);
		if (c != null)
			return (c.getDownloadSource(link, rs, rs.getQuery().getStatement(), tableName, pk, dialect));
		
		return (new ErrorDownloadSource());
		}
	
	@Override
	public DownloadSource getStreamDownloadSource(String link, QueryParameters query, String tableName, Set<Integer> pk, SQLDialect dialect, String format)
		{
		final FileDownloader c = getFileDownloader(format);
		if (c != null)
			return (c.getStreamDownloadSource(link, runner.createRowProducer(link, query), query.getQuery().getStatement(), query.getQuery().getName(), tableName, pk, dialect));
		
		return (new ErrorDownloadSource());
		}
	
	@Override
	public DownloadSource getStreamDownloadSource(String link, RowProducer p, String srcName, String fileName, String tableName, Set<Integer> pk, SQLDialect dialect, String format)
		{
		final FileDownloader c = getFileDownloader(format);
		if (c != null)
			return (c.getStreamDownloadSource(link, p, srcName, fileName, tableName, pk, dialect));
		
		return (new ErrorDownloadSource());
		}
	
	@Override
	public DownloadSource getMultiStreamDownloadSource(String link, RowSetProducer p, String srcName, String fileName, SQLDialect dialect, String format)
		{
		final FileDownloader c = getFileDownloader(format);
		if (c != null)
			return (c.getMultiStreamDownloadSource(link, p, srcName, fileName, dialect));
		
		return (new ErrorDownloadSource());
		}
	
	private FileDownloader getFileDownloader(String format)
		{
		return (formats.get(format));
		}
	}
