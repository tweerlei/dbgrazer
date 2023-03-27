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

import com.mongodb.client.MongoCollection;

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
import de.tweerlei.dbgrazer.web.service.TextTransformerService.Option;
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
	/** Re-package Document objects as Java Beans */
	public static class DocumentBean implements Comparable<DocumentBean>
		{
		private final String id;
		private final String idFilter;
		private int valueSize;
		private String value;
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 * @param value Formatted value
		 * @param valueSize Original value size
		 * @param idPropertyName Name of the property containing the document's ID
		 */
		public DocumentBean(Document r, String value, int valueSize, String idPropertyName)
			{
			this.id = r.get(idPropertyName).toString();
			this.idFilter = getDocumentIdFilter(r);
			this.value = value;
			this.valueSize = valueSize;
			}
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 * @param value Formatted value
		 * @param valueSize Original value size
		 */
		public DocumentBean(Document r, String value, int valueSize)
			{
			this(r, value, valueSize, MongoDBConstants.ID_PROPERTY);
			}
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 * @param value Formatted value
		 */
		public DocumentBean(Document r, String value)
			{
			this(r, value, value.length());
			}
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 */
		public DocumentBean(Document r)
			{
			this(r, r.toJson());
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
		public int compareTo(DocumentBean o)
			{
			return (o.id.compareTo(id));
			}
		}
	
	private static final String VIEW_COUNT = "count";
	private static final String JSON_FORMAT_NAME = "JSON";
	private static final int MONGODB_FETCH_LIMIT = 100;
	
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
		
		final RowSet cats = buildRowSet(query, topics, MongoDBMessageKeys.DATABASE, topicTime);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(MongoDBMessageKeys.DATABASES_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", MongoDBMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Iterable<String> values, String label, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(MongoDBMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(label, ColumnType.STRING, null, null, null, null));
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
		
		final RowSet cats = buildRowSet(query, partitions, MongoDBMessageKeys.COLLECTION, partTime);
		
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
	 * @param view View mode
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/documents.html", method = RequestMethod.GET)
	public Map<String, Object> showMessages(
			@RequestParam("database") String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "view", required = false) String view
			)
		{
		return (showDocumentsInternal(database, collection, id, value, view));
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message offset
	 * @param value Value search term
	 * @param view View mode
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/documents.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessages(
			@RequestParam("database") String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "view", required = false) String view
			)
		{
		return (showDocumentsInternal(database, collection, id, value, view));
		}
	
	private Map<String, Object> showDocumentsInternal(
			String database,
			String collection,
			String id,
			String value,
			String view
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		model.put("collection", collection);
		model.put("id", id);
		model.put("value", value);
		model.put("view", view);
		
		final MongoCollection<Document> coll = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection);
		final Document filter = buildFilter(value, id);
		final List<DocumentBean> l;
		if (VIEW_COUNT.equals(view))
			{
			l = countDocuments(coll, filter);
			}
		else
			{
			if (filter == null)
				{
				final List<DocumentBean> indexes = findIndexes(coll);
				if (!indexes.isEmpty())
					{
					final Map<String, TabItem<DocumentBean>> mainTabs = new HashMap<String, TabItem<DocumentBean>>(10);
					for (DocumentBean rec : indexes)
						mainTabs.put(rec.getId(), new TabItem<DocumentBean>(rec, 1));
					model.put("mainTabs", mainTabs);
					}
				}
			
			l = findDocuments(coll, filter);
			}
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(MongoDBMessageKeys.DATABASE_LEVEL, null));
		levels.add(new SubQueryDefImpl(MongoDBMessageKeys.COLLECTION_LEVEL, null));
		final Query query = new ViewImpl(MongoDBMessageKeys.DOCUMENT_LEVEL, null, null, null, null, levels, null);
		
		final Map<String, TabItem<List<DocumentBean>>> tabs = new HashMap<String, TabItem<List<DocumentBean>>>(1);
		tabs.put(MongoDBMessageKeys.DOCUMENTS_TAB, new TabItem<List<DocumentBean>>(l, l.size()));
		
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
	
	private List<DocumentBean> countDocuments(MongoCollection<Document> coll, Document filter)
		{
		final long c;
		if (filter == null)
			c = coll.countDocuments();
		else
			c = coll.countDocuments(filter);
		
		final Document doc = new Document(MongoDBConstants.ID_PROPERTY, "count");
		
		return (Collections.singletonList(new DocumentBean(doc, "count", (int) c)));
		}
	
	private List<DocumentBean> findDocuments(MongoCollection<Document> coll, Document filter)
		{
		final Iterable<Document> records;
		if (filter == null)
			records = coll.find().limit(MONGODB_FETCH_LIMIT);
		else
			records = coll.find(filter).limit(MONGODB_FETCH_LIMIT);
		
		final List<DocumentBean> l = new ArrayList<DocumentBean>(100);
		for (Document doc : records)
			{
			// MongoDB magic ID
			l.add(new DocumentBean(doc));
			}
		Collections.sort(l);
		return (l);
		}
	
	private List<DocumentBean> findIndexes(MongoCollection<Document> coll)
		{
		final Iterator<Document> indexes = coll.listIndexes().iterator();
		if (!indexes.hasNext())
			return (Collections.emptyList());
		
		final List<DocumentBean> l = new ArrayList<DocumentBean>(10);
		do	{
			final Document doc = indexes.next();
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS, TextTransformerService.Option.FORMATTING);
			final String rawValue = doc.toJson();
			final DocumentBean rec = new DocumentBean(doc, textFormatterService.format(rawValue, JSON_FORMAT_NAME, options), rawValue.length(), MongoDBConstants.INDEX_NAME_PROPERTY);
			l.add(rec);
			}
		while (indexes.hasNext());
		return (l);
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Starting offset
	 * @param formatting Pretty print
	 * @param coloring Syntax coloring
	 * @param lineno Line numbers
	 * @param struct Structure
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/document.html", method = RequestMethod.GET)
	public Map<String, Object> showMessage(
			@RequestParam("database") String database,
			@RequestParam("collection") String collection,
			@RequestParam("id") String id,
			@RequestParam(value = "formatting", required = false) Boolean formatting,
			@RequestParam(value = "coloring", required = false) Boolean coloring,
			@RequestParam(value = "lineno", required = false) Boolean lineno,
			@RequestParam(value = "struct", required = false) Boolean struct
			)
		{
		return (showDocumentInternal(database, collection, id, formatting, coloring, lineno, struct));
		}
	
	/**
	 * Show the file browser
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Starting offset
	 * @param formatting Pretty print
	 * @param coloring Syntax coloring
	 * @param lineno Line numbers
	 * @param struct Structure
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/document.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessage(
			@RequestParam("database") String database,
			@RequestParam("collection") String collection,
			@RequestParam("id") String id,
			@RequestParam(value = "formatting", required = false) Boolean formatting,
			@RequestParam(value = "coloring", required = false) Boolean coloring,
			@RequestParam(value = "lineno", required = false) Boolean lineno,
			@RequestParam(value = "struct", required = false) Boolean struct
			)
		{
		return (showDocumentInternal(database, collection, id, formatting, coloring, lineno, struct));
		}
	
	private Map<String, Object> showDocumentInternal(String database, String collection, String id,
			Boolean formatting,
			Boolean coloring,
			Boolean lineno,
			Boolean struct
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
//		final boolean formattingActive;
		final boolean coloringActive;
		final boolean linenoActive;
		final boolean structureActive;
		if (formatting != null)
			{
//			formattingActive = formatting;
			coloringActive = (coloring == null) ? false : coloring;
			linenoActive = (lineno == null) ? false : lineno;
			structureActive = (struct == null) ? false : struct;
			querySettingsManager.setSyntaxColoringActive(null, coloringActive);
			querySettingsManager.setLineNumbersActive(null, linenoActive);
			querySettingsManager.setStructureActive(null, structureActive);
			}
		else
			{
//			formattingActive = querySettingsManager.isFormattingActive(null);
			coloringActive = querySettingsManager.isSyntaxColoringActive(null);
			linenoActive = querySettingsManager.isLineNumbersActive(null);
			structureActive = querySettingsManager.isStructureActive(null);
			}
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		model.put("collection", collection);
		model.put("id", id);
		
//		model.put("formats", textFormatterService.getSupportedTextFormats());
		model.put("coloring", coloringActive);
		model.put("lineno", linenoActive);
		model.put("struct", structureActive);
		
		final Iterable<Document> records = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).find(buildFilter(id)).limit(1);
		
		final DocumentBean rec;
		final Iterator<Document> it = records.iterator();
		if (!it.hasNext())
			rec = null;
		else
			{
			final Document doc = it.next();
			final String rawValue = doc.toJson();
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.FORMATTING);
			if (coloringActive)
				options.add(Option.SYNTAX_COLORING);
			if (linenoActive)
				options.add(Option.LINE_NUMBERS);
			if (structureActive)
				options.add(Option.STRUCTURE);
			rec = new DocumentBean(doc, textFormatterService.format(rawValue, JSON_FORMAT_NAME, options), rawValue.length());
			}
		
		final Map<String, TabItem<DocumentBean>> tabs = new HashMap<String, TabItem<DocumentBean>>(1);
		tabs.put(id, new TabItem<DocumentBean>(rec, 1));
		
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
