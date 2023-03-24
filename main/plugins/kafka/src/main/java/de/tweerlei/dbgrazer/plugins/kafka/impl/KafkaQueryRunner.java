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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.dbgrazer.plugins.kafka.types.MessageHeadersQueryType;
import de.tweerlei.dbgrazer.plugins.kafka.types.MessageQueryType;
import de.tweerlei.dbgrazer.plugins.kafka.types.QueryTypeAttributes;
import de.tweerlei.dbgrazer.plugins.kafka.types.SendQueryType;
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
	
	private static final class RecordComparator implements Comparator<ConsumerRecord<?, ?>>
		{
		public RecordComparator()
			{
			}
		
		@Override
		public int compare(ConsumerRecord<?, ?> a, ConsumerRecord<?, ?> b)
			{
			if (a.timestamp() < b.timestamp())
				return (1);
			if (a.timestamp() > b.timestamp())
				return (-1);
			return (0);
			}
		}
	
	private static final RecordComparator TIMESTAMP_DESCENDING = new RecordComparator();
	
	private final TimeService timeService;
	private final KafkaApiService kafkaClient;
	private final ResultBuilderService resultBuilder;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param kafkaClient KafkaApiService
	 * @param resultBuilder ResultBuilderService
	 */
	@Autowired
	public KafkaQueryRunner(TimeService timeService, KafkaApiService kafkaClient,
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
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (query.getType() instanceof SendQueryType)
			sendMessage(res, link, query, subQueryIndex, params);
		else if (query.getType() instanceof MessageQueryType)
			fetchSingleMessage(res, link, query, subQueryIndex, params);
		else
			fetchMessages(res, link, query, subQueryIndex, params, limit);
		
		return (res);
		}
	
	private void sendMessage(Result res, String link, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final String topic = query.getAttributes().get(QueryTypeAttributes.ATTR_TOPIC);
			final String partNo = query.getAttributes().get(QueryTypeAttributes.ATTR_PARTITION);
			Integer partition = null;
			try	{
				partition = Integer.valueOf(partNo);
				}
			catch (NumberFormatException e)
				{
				// ignore
				}
			
			final String q = buildQuery(query, params);
			final String[] fields = q.split("\n", 2);
			final String key;
			final String value;
			if (fields.length > 1)
				{
				key = fields[0].trim();
				value = fields[1].trim();
				}
			else
				{
				key = null;
				value = fields[0].trim();
				}
			
			final long start = timeService.getCurrentTime();
			final RecordMetadata rm = kafkaClient.sendRecord(link, topic, partition, key, value);
			final long end = timeService.getCurrentTime();
			
			res.getRowSets().put(BODY_TAB, createResultRowSet(query, subQueryIndex, rm, end - start));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "sendMessage", e);
			throw new PerformQueryException(query.getName(), new RuntimeException("sendMessage: " + e.getMessage(), e));
			}
		}
	
	private void fetchSingleMessage(Result res, String link, Query query, int subQueryIndex, List<Object> params) throws PerformQueryException
		{
		try	{
			final String q = buildQuery(query, params);
			final KafkaQueryParser p = new KafkaQueryParser(q);
			
			if (p.getPartition() == null)
				throw new RuntimeException("No partition specified");
			if (p.getStartOffset() == null)
				throw new RuntimeException("No offset specified");
			
			final long start = timeService.getCurrentTime();
			final ConsumerRecord<String, String> rec = kafkaClient.fetchRecord(link, p.getTopic(), p.getPartition(), p.getStartOffset());
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
	
	private void fetchMessages(Result res, String link, Query query, int subQueryIndex, List<Object> params, int limit) throws PerformQueryException
		{
		try	{
			final String q = buildQuery(query, params);
			final KafkaQueryParser p = new KafkaQueryParser(q);
			
			final int maxRows = Math.min(limit, kafkaClient.getMaxRows(link));
			
			final long start = timeService.getCurrentTime();
			final List<ConsumerRecord<String, String>> recs = kafkaClient.fetchRecords(link, p.getTopic(), p.getPartition(), p.getStartOffset(), p.getEndOffset(), p.getKey(), null);
			final long end = timeService.getCurrentTime();
			
			final RowSet rs = createListRowSet(query, subQueryIndex, recs, maxRows, end - start);
			
			res.getRowSets().put(query.getName(), rs);
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
		final Query q = new QueryImpl(query.getName(), query.getSourceSchema(), query.getGroupName(), query.getStatement(), query.getStatementVariants(), new MessageHeadersQueryType(null), query.getParameters(), null, query.getAttributes());
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
	
	private RowSet createListRowSet(Query query, int subQueryIndex, List<ConsumerRecord<String, String>> recs, int limit, long time)
		{
		final RowSetImpl rs;
		if (recs == null || recs.isEmpty())
			rs = resultBuilder.createEmptyRowSet(query, subQueryIndex, time);
		else
			{
			final List<ColumnDef> columns = new ArrayList<ColumnDef>();
			columns.add(new ColumnDefImpl(ID_HEADER, ColumnType.STRING, null, query.getTargetQueries().get(0), null, null));
			columns.add(new ColumnDefImpl(KEY_HEADER, ColumnType.STRING, null, query.getTargetQueries().get(1), null, null));
			columns.add(new ColumnDefImpl(TIMESTAMP_HEADER, ColumnType.DATE, null, query.getTargetQueries().get(2), null, null));
			columns.add(new ColumnDefImpl(SIZE_HEADER, ColumnType.INTEGER, null, query.getTargetQueries().get(3), null, null));
			
			Collections.sort(recs, TIMESTAMP_DESCENDING);
			
			rs = new RowSetImpl(query, subQueryIndex, columns);
			int count = 0;
			for (ConsumerRecord<String, String> rec : recs)
				{
				if (count >= limit)
					{
					rs.setMoreAvailable(true);
					break;
					}
				
				final ResultRow row = new DefaultResultRow(columns.size());
				row.getValues().add(rec.partition() + "  " + rec.offset());
				row.getValues().add(rec.key());
				row.getValues().add(new Date(rec.timestamp()));
				row.getValues().add(rec.serializedValueSize());
				rs.getRows().add(row);
				count++;
				}
			}
		
		rs.setQueryTime(time);
		return (rs);
		}
	
	private RowSet createResultRowSet(Query query, int subQueryIndex, RecordMetadata rm, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl(ID_HEADER, ColumnType.STRING, null, query.getTargetQueries().get(0), null, null));
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		
		final ResultRow row = new DefaultResultRow(columns.size());
		row.getValues().add(rm.partition() + "  " + rm.offset());
		rs.getRows().add(row);
		
		rs.setQueryTime(time);
		return (rs);
		}
	}
