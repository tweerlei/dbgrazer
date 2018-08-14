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
package de.tweerlei.dbgrazer.web.export.dml;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.web.backend.MetadataDownloadSourceCreator;
import de.tweerlei.dbgrazer.web.backend.SchemaDownloadSourceCreator;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.export.dml.download.ERMDownloadSource;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Download ERM XML files
 * 
 * @author Robert Wruck
 */
@Service
public class ERMDownloadSourceCreator extends NamedBase implements MetadataDownloadSourceCreator, SchemaDownloadSourceCreator
	{
	/**
	 * Constructor
	 */
	public ERMDownloadSourceCreator()
		{
		super("ERM");
		}
	
	@Override
	public DownloadSource createDownloadSource(String link, TableDescription info, SQLDialect dialect, Map<String, Object> attributes)
		{
		return (new ERMDownloadSource(dialect.getQualifiedTableName(info.getName()), info.getName().getCatalogName(), info.getName().getSchemaName(), Collections.singleton(info)));
		}
	
	@Override
	public DownloadSource createDownloadSource(String link, String name, Set<TableDescription> info, SQLDialect dialect, Map<String, Object> attributes)
		{
		final Object catalog;
		final Object schema;
		
		if (attributes == null)
			{
			catalog = null;
			schema = null;
			}
		else
			{
			catalog = attributes.get(RowSetConstants.ATTR_TABLE_CATALOG);
			schema = attributes.get(RowSetConstants.ATTR_TABLE_SCHEMA);
			}
		
		return (new ERMDownloadSource(name, (catalog == null) ? "" : catalog.toString(), (schema == null) ? "" : schema.toString(), info));
		}
	}
