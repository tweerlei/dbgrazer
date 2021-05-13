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

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.dbgrazer.web.backend.FileDownloader;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.export.dbunit.download.DBUnitDownloadSource;
import de.tweerlei.dbgrazer.web.export.dbunit.download.DBUnitMultiStreamDownloadSource;
import de.tweerlei.dbgrazer.web.export.dbunit.download.DBUnitStreamDownloadSource;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * CSV impl.
 * 
 * @author Robert Wruck
 */
@Service
public class DBUnitFileDownloader extends NamedBase implements FileDownloader
	{
	private final DataFormatterFactory factory;
	
	/**
	 * Constructor
	 * @param factory DataFormatterFactory
	 */
	@Autowired
	public DBUnitFileDownloader(DataFormatterFactory factory)
		{
		super("DBUnit");
		this.factory = factory;
		}
	
	@Override
	public DownloadSource getDownloadSource(String link, RowSet rs, String srcName, QualifiedName tableName, Set<Integer> pk, SQLDialect dialect)
		{
		return (new DBUnitDownloadSource(rs, dialect.getQualifiedTableName(tableName), getHeader(link, srcName), factory.getMessage(MessageKeys.NO_DATA_FOUND), factory.getXMLFormatter()));
		}
	
	@Override
	public DownloadSource getStreamDownloadSource(String link, RowProducer p, String srcName, String fileName, QualifiedName tableName, Set<Integer> pk, SQLDialect dialect)
		{
		return (new DBUnitStreamDownloadSource(p, fileName, dialect.getQualifiedTableName(tableName), getHeader(link, srcName), factory.getMessage(MessageKeys.NO_DATA_FOUND), factory.getXMLFormatter()));
		}
	
	@Override
	public DownloadSource getMultiStreamDownloadSource(String link, RowSetProducer p, String srcName, String fileName, SQLDialect dialect)
		{
		return (new DBUnitMultiStreamDownloadSource(p, fileName, getHeader(link, srcName), factory.getMessage(MessageKeys.NO_DATA_FOUND), factory.getXMLFormatter(), dialect));
		}
	
	private String getHeader(String link, String stmt)
		{
		return (factory.getMessage(MessageKeys.DML_HEADER, link) + "\n" + stmt);
		}
	}
