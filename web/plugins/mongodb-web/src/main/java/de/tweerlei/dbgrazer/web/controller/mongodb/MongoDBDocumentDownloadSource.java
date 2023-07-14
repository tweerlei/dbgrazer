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
package de.tweerlei.dbgrazer.web.controller.mongodb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.bson.Document;

import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBConstants;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * DownloadSource that sends the contents of an InputStream
 * 
 * @author Robert Wruck
 */
public class MongoDBDocumentDownloadSource extends AbstractDownloadSource
	{
	private static final String OUTPUT_CHARSET = "UTF-8";
	
	private final MongoDBClientService mongoClientService;
	private final String link;
	private final String database;
	private final String collection;
	private final String id;
	
	/**
	 * Constructor
	 * @param mongoClientService MongoDBClientService
	 * @param link Link name
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message offset
	 */
	public MongoDBDocumentDownloadSource(MongoDBClientService mongoClientService,
			String link, String database, String collection, String id)
		{
		this.mongoClientService = mongoClientService;
		this.link = link;
		this.database = database;
		this.collection = collection;
		this.id = id;
		
		this.setAttachment(true);
		this.setExpireTime(DownloadSource.ALWAYS);
		this.setFileName(id + ".json");
		}
	
	@Override
	public String getContentType()
		{
		return ("application/json");
		}
	
	@Override
	public void write(OutputStream stream) throws IOException
		{
		final Iterable<Document> records = mongoClientService.getMongoClient(link).getDatabase(database).getCollection(collection).find(new Document(MongoDBConstants.ID_PROPERTY, id)).limit(1);
		final Iterator<Document> it = records.iterator();
		if (it.hasNext())
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, OUTPUT_CHARSET);
			w.write(it.next().toJson());
			w.flush();
			}
		}
	}
