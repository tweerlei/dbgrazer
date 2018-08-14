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
package de.tweerlei.dbgrazer.web.export.dbunit;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.web.backend.MetadataDownloadSourceCreator;
import de.tweerlei.dbgrazer.web.backend.SchemaDownloadSourceCreator;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.export.dbunit.download.DBUnitDtdDownloadSource;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * CSV impl.
 * 
 * @author Robert Wruck
 */
@Service
public class DBUnitDownloadSourceCreator extends NamedBase implements MetadataDownloadSourceCreator, SchemaDownloadSourceCreator
	{
	private final DataFormatterFactory factory;
	
	/**
	 * Constructor
	 * @param factory DataFormatterFactory
	 */
	@Autowired
	public DBUnitDownloadSourceCreator(DataFormatterFactory factory)
		{
		super("DBUnitDTD");
		this.factory = factory;
		}
	
	@Override
	public DownloadSource createDownloadSource(String link, TableDescription info, SQLDialect dialect, Map<String, Object> attributes)
		{
		return (new DBUnitDtdDownloadSource(info.getName().getObjectName(), Collections.singleton(info), dialect, getComment(link)));
		}
	
	@Override
	public DownloadSource createDownloadSource(String link, String name, Set<TableDescription> info, SQLDialect dialect, Map<String, Object> attributes)
		{
		return (new DBUnitDtdDownloadSource(name, info, dialect, getComment(link)));
		}
	
	private String getComment(String link)
		{
		return (factory.getMessage(MessageKeys.DDL_HEADER, link));		
		}
	}
