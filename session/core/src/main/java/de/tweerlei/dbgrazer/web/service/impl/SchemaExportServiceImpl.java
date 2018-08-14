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

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.web.backend.SchemaDownloadSourceCreator;
import de.tweerlei.dbgrazer.web.service.SchemaExportService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;
import de.tweerlei.spring.web.view.ErrorDownloadSource;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class SchemaExportServiceImpl implements SchemaExportService
	{
	private final Logger logger;
	private final Map<String, SchemaDownloadSourceCreator> formats;
	
	/**
	 * Constructor
	 * @param formats DownloadSourceCreators
	 */
	@Autowired(required = false)
	public SchemaExportServiceImpl(Set<SchemaDownloadSourceCreator> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<SchemaDownloadSourceCreator>(formats));
		
		this.logger.log(Level.INFO, "Schema download source creators: " + this.formats);
		}
	
	/**
	 * Constructor used when no DownloadSourceCreator instances are available
	 */
	public SchemaExportServiceImpl()
		{
		this(Collections.<SchemaDownloadSourceCreator>emptySet());
		}
	
	@Override
	public Set<String> getSupportedExportFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public DownloadSource getExportDownloadSource(String link, String name, Set<TableDescription> info, SQLDialect dialect, Map<String, Object> attributes, String format)
		{
		final SchemaDownloadSourceCreator c = getDownloadSourceCreator(format);
		if (c != null)
			return (c.createDownloadSource(link, name, info, dialect, attributes));
		
		return (new ErrorDownloadSource());
		}
	
	private SchemaDownloadSourceCreator getDownloadSourceCreator(String format)
		{
		return (formats.get(format));
		}
	}
