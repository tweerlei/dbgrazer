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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBConstants;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.TimeService;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck <wruck@tweerlei.de>
 */
@Controller
public class MongoDBBrowseController
	{
	/** Re-package Kafka objects as Java Beans */
	public static class ConsumerRecordBean implements Comparable<ConsumerRecordBean>
		{
		private final String id;
		private final String idFilter;
		private int valueSize;
		private String value;
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 */
		public ConsumerRecordBean(Document r)
			{
			this.id = r.get(MongoDBConstants.ID_PROPERTY).toString();
			this.idFilter = getDocumentIdFilter(r);
			this.value = r.toJson();
			this.valueSize = this.value.length();
			}
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 * @param value Formatted value
		 * @param valueSize Original value size
		 */
		public ConsumerRecordBean(Document r, String value, int valueSize)
			{
			this.id = r.get(MongoDBConstants.ID_PROPERTY).toString();
			this.idFilter = getDocumentIdFilter(r);
			this.value = value;
			this.valueSize = valueSize;
			}
		
		private static String getDocumentIdFilter(Document r)
			{
			final Object id = r.get(MongoDBConstants.ID_PROPERTY);
			final Document doc = (id == null) ? new Document() : new Document(MongoDBConstants.ID_PROPERTY, id);
			return (doc.toJson());
			}
		
		/**
		 * @return the id
		 */
		public String getId()
			{
			return id;
			}
		
		/**
		 * @return the idFilter
		 */
		public String getIdFilter()
			{
			return idFilter;
			}
		
		/**
		 * @return the offset
		 */
		public int getValueSize()
			{
			return valueSize;
			}
		
		/**
		 * @return the offset
		 */
		public String getValue()
			{
			return value;
			}
		
		@Override
		public int compareTo(ConsumerRecordBean o)
			{
			return (o.id.compareTo(id));
			}
		}
	
	private final MongoDBClientService mongoClientService;
	private final TextTransformerService textFormatterService;
	private final QuerySettingsManager querySettingsManager;
	private final TimeService timeService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param mongoClientService MongoDBClientService
	 * @param textFormatterService TextFormatterService
	 * @param querySettingsManager QuerySettingsManager
	 * @param timeService TimeService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public MongoDBBrowseController(MongoDBClientService mongoClientService, TextTransformerService textFormatterService,
			QuerySettingsManager querySettingsManager,
			TimeService timeService,
			ConnectionSettings connectionSettings)
		{
		this.mongoClientService = mongoClientService;
		this.textFormatterService = textFormatterService;
		this.querySettingsManager = querySettingsManager;
		this.timeService = timeService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/databases.html", method = RequestMethod.GET)
	public Map<String, Object> showDatabases()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final long start = timeService.getCurrentTime();
		
		final Iterable<String> topics = mongoClientService.getMongoClient(connectionSettings.getLinkName()).listDatabaseNames();
		final long topicTime = timeService.getCurrentTime() - start;
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(MongoDBMessageKeys.DATABASE_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, topics, topicTime);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(MongoDBMessageKeys.DATABASES_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", MongoDBMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Iterable<String> values, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(MongoDBMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(MongoDBMessageKeys.TOPIC, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		final Set<String> sortedTopics = new TreeSet<String>();
		for (String s : values)
			sortedTopics.add(s);
		for (String value : sortedTopics)
			rs.getRows().add(new DefaultResultRow(value, value));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, true);
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/collections.html", method = RequestMethod.GET)
	public Map<String, Object> showCollections(
			@RequestParam("database") String database
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		
		final long start = timeService.getCurrentTime();
		
		final Iterable<String> partitions = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).listCollectionNames();
		final long partTime = timeService.getCurrentTime() - start;
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(MongoDBMessageKeys.DATABASE_LEVEL, null));
		final Query query = new ViewImpl(MongoDBMessageKeys.COLLECTION_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, partitions, partTime);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(MongoDBMessageKeys.COLLECTIONS_TAB, new TabItem<RowSet>(cats, cats.getRows().size() - 1));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(database)));
		model.put("extensionJS", MongoDBMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message offset
	 * @param value Value search term
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/documents.html", method = RequestMethod.GET)
	public Map<String, Object> showMessages(
			@RequestParam("database") String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "value", required = false) String value
			)
		{
		return (showDocumentsInternal(database, collection, id, value));
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message offset
	 * @param value Value search term
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/documents.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessages(
			@RequestParam("database") String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "value", required = false) String value
			)
		{
		return (showDocumentsInternal(database, collection, id, value));
		}
	
	private Map<String, Object> showDocumentsInternal(
			String database,
			String collection,
			String id,
			String value
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		model.put("collection", collection);
		model.put("id", id);
		model.put("value", value);
		
		final Document filter = buildFilter(value, id);
		final Iterable<Document> records;
		if (filter != null)
			records = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).find(filter).limit(100);
		else
			records = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).find().limit(100);
		
		final List<ConsumerRecordBean> l = new ArrayList<ConsumerRecordBean>(100);
		for (Document doc : records)
			{
			// MongoDB magic ID
			l.add(new ConsumerRecordBean(doc));
			}
		Collections.sort(l);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(MongoDBMessageKeys.DATABASE_LEVEL, null));
		levels.add(new SubQueryDefImpl(MongoDBMessageKeys.COLLECTION_LEVEL, null));
		final Query query = new ViewImpl(MongoDBMessageKeys.DOCUMENT_LEVEL, null, null, null, null, levels, null);
		
		final Map<String, TabItem<List<ConsumerRecordBean>>> tabs = new HashMap<String, TabItem<List<ConsumerRecordBean>>>(1);
		tabs.put(MongoDBMessageKeys.DOCUMENTS_TAB, new TabItem<List<ConsumerRecordBean>>(l, l.size()));
		
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(database, (collection == null) ? "" : collection)));
		model.put("extensionJS", MongoDBMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private Document buildFilter(String value, String id)
		{
		if (!StringUtils.empty(value))
			{
			try	{
				final Document filter = Document.parse(value);
				return (filter);
				}
			catch (RuntimeException e)
				{
				// use id
				}
			}
		
		if (!StringUtils.empty(id))
			{
			try	{
				final Document filter = Document.parse(id);
				return (new Document(MongoDBConstants.ID_PROPERTY,
						new Document("$gte", filter.get(MongoDBConstants.ID_PROPERTY))
						));
				}
			catch (RuntimeException e)
				{
				// skip
				}
			}
		
		return (null);
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Starting offset
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/document.html", method = RequestMethod.GET)
	public Map<String, Object> showMessage(
			@RequestParam("database") String database,
			@RequestParam("collection") String collection,
			@RequestParam("id") String id,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showDocumentInternal(database, collection, id, format, formatting));
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Starting offset
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/document.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessage(
			@RequestParam("database") String database,
			@RequestParam("collection") String collection,
			@RequestParam("id") String id,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showDocumentInternal(database, collection, id, format, formatting));
		}
	
	private Map<String, Object> showDocumentInternal(String database, String collection, String id, String format, Boolean formatting)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final String formatName;
		final boolean formattingActive;
		if (format == null)
			{
			formatName = querySettingsManager.getFormatName(null);
			formattingActive = querySettingsManager.isFormattingActive(null);
			}
		else
			{
			formatName = format;
			formattingActive = (formatting == null) ? false : formatting;
			querySettingsManager.setFormatName(null, formatName);
			querySettingsManager.setFormattingActive(null, formattingActive);
			}
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		model.put("collection", collection);
		model.put("id", id);
		model.put("format", formatName);
		model.put("formatting", formattingActive);
		
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final Iterable<Document> records = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).find(buildFilter(id)).limit(1);
		
		final ConsumerRecordBean rec;
		final Iterator<Document> it = records.iterator();
		if (!it.hasNext())
			rec = null;
		else
			{
			final Document doc = it.next();
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
			if (formattingActive)
				options.add(TextTransformerService.Option.FORMATTING);
			final String rawValue = doc.toJson();
			rec = new ConsumerRecordBean(doc, textFormatterService.format(rawValue, formatName, options), rawValue.length());
			}
		
		final Map<String, TabItem<ConsumerRecordBean>> tabs = new HashMap<String, TabItem<ConsumerRecordBean>>(1);
		tabs.put(id, new TabItem<ConsumerRecordBean>(rec, 1));
		
		model.put("tabs", tabs);
		model.put("extensionJS", MongoDBMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private Document buildFilter(String id)
		{
		try	{
			return (Document.parse(id));
			}
		catch (RuntimeException e)
			{
			return (new Document());
			}
		}
	}
