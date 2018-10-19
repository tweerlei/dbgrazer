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
package de.tweerlei.dbgrazer.plugins.kafka.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.kafka.KafkaClientService;
import de.tweerlei.dbgrazer.plugins.kafka.types.MessageHeadersQueryType;
import de.tweerlei.dbgrazer.plugins.kafka.types.MessageQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.spring.service.TimeService;

/**
 * Run webservice queries
 * 
 * @author Robert Wruck
 */
@Service
public class KafkaQueryRunner extends BaseQueryRunner
	{
	private static final String HEADER_CHARSET = "UTF-8";
	
	private static final String BODY_TAB = "$bodyTab";
	private static final String HEADER_TAB = "$headerTab";
	private static final String ID_HEADER = "ID";
	private static final String KEY_HEADER = "Key";
	private static final String TIMESTAMP_HEADER = "Timestamp";
	private static final String SIZE_HEADER = "Size";
	
	private final TimeService timeService;
	private final KafkaClientService kafkaClient;
	private final ResultBuilderService resultBuilder;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param kafkaClient KafkaClientService
	 * @param resultBuilder ResultBuilderService
	 */
	@Autowired
	public KafkaQueryRunner(TimeService timeService, KafkaClientService kafkaClient,
			ResultBuilderService resultBuilder)
		{
		super("Kafka");
		this.timeService = timeService;
		this.kafkaClient = kafkaClient;
		this.resultBuilder = resultBuilder;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return (t.getLinkType() instanceof KafkaLinkType);
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (query.getType() instanceof MessageQueryType)
			fetchSingleMessage(res, link, query, subQueryIndex, params);
		else
			fetchMessages(res, link, query, subQueryIndex, params);
		
		return (res);
		}
	
	private void fetchSingleMessage(Result res, String link, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final String q = buildQuery(query, params);
			
			final String[] fields = q.split(":");
			final String topic = fields[0];
			final int partition;
			final long offset;
			if (fields.length > 2)
				{
				partition = Integer.parseInt(fields[1]);
				offset = Long.parseLong(fields[2]);
				}
			else if (fields.length > 1)
				{
				partition = 0;
				offset = Long.parseLong(fields[1]);
				}
			else
				{
				partition = 0;
				offset = 0;
				}
			
			final long start = timeService.getCurrentTime();
			final ConsumerRecord<String, String> rec = kafkaClient.fetchRecord(link, topic, partition, offset);
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(BODY_TAB, createBodyRowSet(query, subQueryIndex, rec, end - start, link));
			res.getRowSets().put(HEADER_TAB, createHeaderRowSet(query, subQueryIndex + 1, rec, end - start));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "fetchSingleMessage", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("fetchSingleMessage: " + e.getMessage(), e));
			}
		}
	
	private void fetchMessages(Result res, String link, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final String q = buildQuery(query, params);
			
			final String[] fields = q.split(":");
			final String topic = fields[0];
			final int partition;
			final long startOffset;
			final Long endOffset;
			if (fields.length > 2)
				{
				partition = Integer.parseInt(fields[1]);
				final String[] offsets = fields[2].split("-");
				startOffset = Long.parseLong(offsets[0]);
				if (offsets.length > 1)
					endOffset = Long.parseLong(offsets[1]);
				else
					endOffset = null;
				}
			else if (fields.length > 1)
				{
				partition = 0;
				final String[] offsets = fields[1].split("-");
				startOffset = Long.parseLong(offsets[0]);
				if (offsets.length > 1)
					endOffset = Long.parseLong(offsets[1]);
				else
					endOffset = null;
				}
			else
				{
				partition = 0;
				startOffset = 0;
				endOffset = null;
				}
			
			final long start = timeService.getCurrentTime();
			final List<ConsumerRecord<String, String>> recs = kafkaClient.fetchRecords(link, topic, partition, startOffset, endOffset);
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(query.getName(), createListRowSet(query, subQueryIndex, recs, end - start));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "fetchMessages", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("fetchMessages: " + e.getMessage(), e));
			}
		}
	
	private String buildQuery(Query query, List<Object> params) throws PerformQueryException
		{
		try	{
			return (new ParamReplacer(params).replaceAll(query.getStatement()));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "buildQuery", e);
			throw new PerformQueryException(query.getName(), e);
			}
		}
	
	private RowSet createBodyRowSet(Query query, int subQueryIndex, ConsumerRecord<String, String> rec, long time, String link)
		{
		final RowSetImpl rs;
		if (rec == null)
			rs = resultBuilder.createEmptyRowSet(query, subQueryIndex, time);
		else
			rs = resultBuilder.createSingletonRowSet(query, subQueryIndex, "body", rec.value(), time);
		
//		rs.getAttributes().put(QueryTypeAttributes.ATTR_LINK, link);
		
		return (rs);
		}
	
	private RowSet createHeaderRowSet(Query query, int subQueryIndex, ConsumerRecord<String, String> rec, long time)
		{
		final Query q = new QueryImpl(query.getName(), query.getSourceSchema(), query.getGroupName(), query.getStatement(), new MessageHeadersQueryType(null), query.getParameters(), null, query.getAttributes());
		final RowSetImpl rs;
		if (rec == null)
			rs = resultBuilder.createEmptyRowSet(q, subQueryIndex, time);
		else
			{
			final Map<String, Object> headers = new TreeMap<String, Object>();
			headers.put(KEY_HEADER, rec.key());
			headers.put(TIMESTAMP_HEADER, new Date(rec.timestamp()));
			try	{
				for (Header header : rec.headers())
					headers.put(header.key(), new String(header.value(), HEADER_CHARSET));
				}
			catch (UnsupportedEncodingException e)
				{
				// unlikely
				}
			rs = resultBuilder.createMapRowSet(q, subQueryIndex, headers, time);
			}
		return (rs);
		}
	
	private RowSet createListRowSet(Query query, int subQueryIndex, List<ConsumerRecord<String, String>> recs, long time)
		{
		final RowSetImpl rs;
		if (recs == null || recs.isEmpty())
			rs = resultBuilder.createEmptyRowSet(query, subQueryIndex, time);
		else
			{
			final List<ColumnDef> columns = new ArrayList<ColumnDef>();
			columns.add(new ColumnDefImpl(ID_HEADER, ColumnType.INTEGER, null, query.getTargetQueries().get(0), null, null));
			columns.add(new ColumnDefImpl(KEY_HEADER, ColumnType.STRING, null, query.getTargetQueries().get(1), null, null));
			columns.add(new ColumnDefImpl(TIMESTAMP_HEADER, ColumnType.DATE, null, query.getTargetQueries().get(2), null, null));
			columns.add(new ColumnDefImpl(SIZE_HEADER, ColumnType.INTEGER, null, query.getTargetQueries().get(3), null, null));
			
			rs = new RowSetImpl(query, subQueryIndex, columns);
			for (ConsumerRecord<String, String> rec : recs)
				{
				final ResultRow row = new DefaultResultRow(columns.size());
				row.getValues().add(rec.offset());
				row.getValues().add(rec.key());
				row.getValues().add(new Date(rec.timestamp()));
				row.getValues().add(rec.serializedValueSize());
				rs.getRows().add(row);
				}
			}
		return (rs);
		}
	}
