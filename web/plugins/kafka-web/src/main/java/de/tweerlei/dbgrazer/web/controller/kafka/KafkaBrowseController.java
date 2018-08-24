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
package de.tweerlei.dbgrazer.web.controller.kafka;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.extension.kafka.KafkaClientService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KafkaBrowseController
	{
	/** Re-package Kafka objects as Java Beans */
	public static class PartitionInfoBean implements Comparable<PartitionInfoBean>
		{
		private final String topic;
		private final int partition;
		
		/**
		 * Constructor
		 * @param pi PartitionInfo
		 */
		public PartitionInfoBean(PartitionInfo pi)
			{
			this.topic = pi.topic();
			this.partition = pi.partition();
			}

		/**
		 * @return the topic
		 */
		public String getTopic()
			{
			return topic;
			}

		/**
		 * @return the partition
		 */
		public int getPartition()
			{
			return partition;
			}
		
		@Override
		public int compareTo(PartitionInfoBean o)
			{
			return (partition - o.partition);
			}
		}
	
	/** Re-package Kafka objects as Java Beans */
	public static class ConsumerRecordBean implements Comparable<ConsumerRecordBean>
		{
	    private final long offset;
	    private final Date timestamp;
	    private final String key;
	    private final String value;
	    private final Map<String, String> headers;
	    
	    /**
	     * Constructor
	     * @param r ConsumerRecord
	     * @param value Preformatted Value
	     */
		public ConsumerRecordBean(ConsumerRecord<String, String> r, String value)
			{
			this.offset = r.offset();
			this.timestamp = new Date(r.timestamp());
			this.key = r.key();
			this.value = value;
			this.headers = new TreeMap<String, String>();
			try {
				for (Header header : r.headers())
					headers.put(header.key(), new String(header.value(), "UTF-8"));
				}
			catch (UnsupportedEncodingException e)
				{
				// should not happen for UTF-8
				}
			}

	    /**
	     * Constructor
	     * @param r ConsumerRecord
	     */
		public ConsumerRecordBean(ConsumerRecord<String, String> r)
			{
			this(r, r.value());
			}

		/**
		 * @return the offset
		 */
		public long getOffset()
			{
			return offset;
			}
		
		/**
		 * @return the timestamp
		 */
		public Date getTimestamp()
			{
			return timestamp;
			}
		
		/**
		 * @return the key
		 */
		public String getKey()
			{
			return key;
			}
		
		/**
		 * @return the value
		 */
		public String getValue()
			{
			return value;
			}
		
		/**
		 * @return the headers
		 */
		public Map<String, String> getHeaders()
			{
			return headers;
			}
		
		@Override
		public int compareTo(ConsumerRecordBean o)
			{
			return ((int) (offset - o.offset));
			}
		}
	
	private final KafkaClientService kafkaClientService;
	private final TextTransformerService textFormatterService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaClientService
	 * @param textFormatterService TextFormatterService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaBrowseController(KafkaClientService kafkaClientService, TextTransformerService textFormatterService,
			ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.textFormatterService = textFormatterService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/topics.html", method = RequestMethod.GET)
	public Map<String, Object> showBrowser()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final KafkaConsumer<String, String> consumer = kafkaClientService.getConsumer(connectionSettings.getLinkName());
		
		final Map<String, List<PartitionInfo>> topics = consumer.listTopics();
		
		final Map<String, List<PartitionInfoBean>> sortedTopics = new TreeMap<String, List<PartitionInfoBean>>();
		for (Map.Entry<String, List<PartitionInfo>> ent : topics.entrySet())
			{
			final List<PartitionInfoBean> l = new ArrayList<PartitionInfoBean>(ent.getValue().size());
			for (PartitionInfo pi : ent.getValue())
				l.add(new PartitionInfoBean(pi));
			Collections.sort(l);
			sortedTopics.put(ent.getKey(), l);
			}
		
		final Map<String, TabItem<Map<String, List<PartitionInfoBean>>>> tabs = new HashMap<String, TabItem<Map<String, List<PartitionInfoBean>>>>(1);
		tabs.put(MessageKeys.TOPICS_TAB, new TabItem<Map<String, List<PartitionInfoBean>>>(sortedTopics, sortedTopics.size()));
		
		model.put("tabs", tabs);
		model.put("extensionJS", "kafka.js");
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Starting offset
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/partition.html", method = RequestMethod.GET)
	public Map<String, Object> showPartition(
			@RequestParam("topic") String topic,
			@RequestParam("partition") Integer partition,
			@RequestParam(value = "offset", required = false) Long offset
			)
		{
		return (showPartitionInternal(topic, partition, offset));
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Starting offset
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/partition.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxPartition(
			@RequestParam("topic") String topic,
			@RequestParam("partition") Integer partition,
			@RequestParam(value = "offset", required = false) Long offset
			)
		{
		return (showPartitionInternal(topic, partition, offset));
		}
	
	private Map<String, Object> showPartitionInternal(String topic, Integer partition, Long offset)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		model.put("partition", partition);
		model.put("offset", offset);
		
		final KafkaConsumer<String, String> consumer = kafkaClientService.getConsumer(connectionSettings.getLinkName());
		final TopicPartition tp = new TopicPartition(topic, partition);
		
		Long startOffset = null;
		Long endOffset = null;
		for (Long l : consumer.beginningOffsets(Collections.singleton(tp)).values())
			startOffset = l;
		for (Long l : consumer.endOffsets(Collections.singleton(tp)).values())
			endOffset = l - 1;
		
		model.put("startOffset", startOffset);
		model.put("endOffset", endOffset);
		
		consumer.assign(Collections.singleton(tp));
		model.put("currentOffset", consumer.position(tp));
		consumer.unsubscribe();
		
		final ConsumerRecords<String, String> records = kafkaClientService.fetchRecords(connectionSettings.getLinkName(), topic, partition, offset);
		
		final List<ConsumerRecordBean> l = new ArrayList<ConsumerRecordBean>(records.count());
		Long minOffset = null;
		Long maxOffset = null;
		for (ConsumerRecord<String, String> record : records)
			{
			l.add(new ConsumerRecordBean(record));
			if ((minOffset == null) || (record.offset() < minOffset))
				minOffset = record.offset();
			if ((maxOffset == null) || (record.offset() > maxOffset))
				maxOffset = record.offset();
			}
		Collections.sort(l);
		
		model.put("minOffset", minOffset);
		model.put("maxOffset", maxOffset);
		
		if ((minOffset != null) && (startOffset != null) && (minOffset > startOffset))
			model.put("prevOffset", startOffset);	// TODO: Subtract fetch size?
		if ((maxOffset != null) && (endOffset != null) && (maxOffset < endOffset))
			model.put("nextOffset", maxOffset + 1);
		
		final Map<String, TabItem<List<ConsumerRecordBean>>> tabs = new HashMap<String, TabItem<List<ConsumerRecordBean>>>(1);
		tabs.put(MessageKeys.FILES_TAB, new TabItem<List<ConsumerRecordBean>>(l, l.size()));
		
		model.put("tabs", tabs);
		model.put("extensionJS", "kafka.js");
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Starting offset
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/message.html", method = RequestMethod.GET)
	public Map<String, Object> showMessage(
			@RequestParam("topic") String topic,
			@RequestParam("partition") Integer partition,
			@RequestParam("offset") Long offset,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showMessageInternal(topic, partition, offset, format, (formatting == null) ? false : formatting));
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Starting offset
	 * @param format Format
	 * @param formatting Pretty print
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/message.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessage(
			@RequestParam("topic") String topic,
			@RequestParam("partition") Integer partition,
			@RequestParam("offset") Long offset,
			@RequestParam(value = "format", required = false) String format,
			@RequestParam(value = "formatting", required = false) Boolean formatting
			)
		{
		return (showMessageInternal(topic, partition, offset, format, (formatting == null) ? false : formatting));
		}
	
	private Map<String, Object> showMessageInternal(String topic, Integer partition, Long offset, String format, boolean formatting)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		model.put("partition", partition);
		model.put("offset", offset);
		model.put("format", format);
		model.put("formatting", formatting);
		
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final ConsumerRecord<String, String> record = kafkaClientService.fetchRecord(connectionSettings.getLinkName(), topic, partition, offset);
		
		final ConsumerRecordBean rec;
		if (record == null)
			rec = null;
		else
			{
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
			if (formatting)
				options.add(TextTransformerService.Option.FORMATTING);
			rec = new ConsumerRecordBean(record, textFormatterService.format(record.value(), format, options));
			}
		
		final Map<String, TabItem<ConsumerRecordBean>> tabs = new HashMap<String, TabItem<ConsumerRecordBean>>(1);
		tabs.put(offset.toString(), new TabItem<ConsumerRecordBean>(rec, 1));
		
		model.put("tabs", tabs);
		model.put("extensionJS", "kafka.js");
		
		return (model);
		}
	}
