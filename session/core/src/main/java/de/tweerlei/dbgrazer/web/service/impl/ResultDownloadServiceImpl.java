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
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.backend.ResultDownloader;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.spring.web.view.DownloadSource;
import de.tweerlei.spring.web.view.ErrorDownloadSource;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class ResultDownloadServiceImpl implements ResultDownloadService
	{
	private final QueryPerformerService runner;
	private final Logger logger;
	private final Map<String, ResultDownloader> formats;
	
	/**
	 * Constructor
	 * @param runner QueryPerformerService
	 * @param formats ResultDownloaders
	 */
	@Autowired(required = false)
	public ResultDownloadServiceImpl(QueryPerformerService runner, Set<ResultDownloader> formats)
		{
		this.runner = runner;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<ResultDownloader>(formats));
		
		this.logger.log(Level.INFO, "Result downloaders: " + this.formats);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 * @param runner QueryPerformerService
	 */
	@Autowired(required = false)
	public ResultDownloadServiceImpl(QueryPerformerService runner)
		{
		this(runner, Collections.<ResultDownloader>emptySet());
		}
	
	@Override
	public Set<String> getSupportedDownloadFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public DownloadSource getDownloadSource(String link, RowSet rs, String format)
		{
		final ResultDownloader c = getResultDownloader(format);
		if (c != null)
			return (c.getDownloadSource(link, rs, rs.getQuery().getName()));
		
		return (new ErrorDownloadSource());
		}
	
	@Override
	public DownloadSource getStreamDownloadSource(String link, Query query, Map<Integer, String> params, String format)
		{
		final ResultDownloader c = getResultDownloader(format);
		if (c != null)
			return (c.getStreamDownloadSource(link, runner.createRowProducer(link, query, params), query.getName(), query.getName()));
		
		return (new ErrorDownloadSource());
		}
	
	private ResultDownloader getResultDownloader(String format)
		{
		return (formats.get(format));
		}
	}
