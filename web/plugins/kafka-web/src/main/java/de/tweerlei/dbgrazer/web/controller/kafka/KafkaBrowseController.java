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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringJoiner;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService.OffsetInfo;
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
import de.tweerlei.dbgrazer.text.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.kafka.TopicSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.TimeService;

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
		private final String replicas;
		private final String inSync;
		private final String leader;
		
		/**
		 * Constructor
		 * @param pi PartitionInfo
		 */
		public PartitionInfoBean(PartitionInfo pi)
			{
			this.topic = pi.topic();
			this.partition = pi.partition();
			
			final StringJoiner sb = new StringJoiner(",");
			for (Node n : pi.replicas())
				sb.append(String.valueOf(n.id()));
			this.replicas = sb.toString();
			
			final StringJoiner sb2 = new StringJoiner(",");
			for (Node n : pi.inSyncReplicas())
				sb2.append(String.valueOf(n.id()));
			this.inSync = sb2.toString();
			
			this.leader = pi.leader() == null ? "" : String.valueOf(pi.leader().id());
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
		
		/**
		 * @return the replicas
		 */
		public String getReplicas()
			{
			return replicas;
			}
		
		/**
		 * @return the in sync replicas
		 */
		public String getInSyncReplicas()
			{
			return inSync;
			}
		
		/**
		 * @return the replicas
		 */
		public String getLeader()
			{
			return leader;
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
		private final int partition;
		private final long offset;
		private final Date timestamp;
		private int keySize;
		private final String key;
		private int valueSize;
		private final String value;
		private final Map<String, String> headers;
		
		/**
		 * Constructor
		 * @param r ConsumerRecord
		 * @param value Preformatted Value
		 */
		public ConsumerRecordBean(ConsumerRecord<String, String> r, String value)
			{
			this.partition = r.partition();
			this.offset = r.offset();
			this.timestamp = new Date(r.timestamp());
			this.keySize = r.serializedKeySize();
			this.key = r.key();
			this.valueSize = r.serializedValueSize();
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
		 * @return the partition
		 */
		public int getPartition()
			{
			return partition;
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
		 * @return the key size
		 */
		public int getKeySize()
			{
			return keySize;
			}
		
		/**
		 * @return the key
		 */
		public String getKey()
			{
			return key;
			}
		
		/**
		 * @return the offset
		 */
		public int getValueSize()
			{
			return valueSize;
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
			final int d = o.timestamp.compareTo(timestamp);
			if (d != 0)
				return (d);
			
			final int d2 = (int) (o.offset - offset);
			if (d2 != 0)
				return (d2);
			
			return (o.partition - partition);
			}
		}
	
	private final KafkaApiService kafkaClientService;
	private final TextTransformerService textFormatterService;
	private final QuerySettingsManager querySettingsManager;
	private final TopicSettingsManager topicStateManager;
	private final TimeService timeService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaApiService
	 * @param textFormatterService TextFormatterService
	 * @param querySettingsManager QuerySettingsManager
	 * @param topicStateManager TopicStateManager
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaBrowseController(KafkaApiService kafkaClientService, TextTransformerService textFormatterService,
			QuerySettingsManager querySettingsManager,
			TopicSettingsManager topicStateManager, TimeService timeService,
			ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.textFormatterService = textFormatterService;
		this.querySettingsManager = querySettingsManager;
		this.topicStateManager = topicStateManager;
		this.timeService = timeService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/topics.html", method = RequestMethod.GET)
	public Map<String, Object> showTopics()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		long start, end;
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
		start = timeService.getCurrentTime();
		final Collection<Node> nodes = kafkaClientService.getNodes(connectionSettings.getLinkName());
		end = timeService.getCurrentTime();
		results.put(KafkaMessageKeys.NODES, new TabItem<RowSet>(buildNodeRowSet(nodes, end - start), nodes.size()));
		
		start = timeService.getCurrentTime();
		final Collection<AclBinding> acls = kafkaClientService.getClusterAcls(connectionSettings.getLinkName());
		end = timeService.getCurrentTime();
		results.put(KafkaMessageKeys.ACLS, new TabItem<RowSet>(buildAclRowSet(acls, end - start), acls.size()));
		
		start = timeService.getCurrentTime();
		final Map<ConfigResource, Config> configs = kafkaClientService.getClusterConfigs(connectionSettings.getLinkName());
		end = timeService.getCurrentTime();
		final RowSet rs = buildConfigRowSet(configs, end - start);
		results.put(KafkaMessageKeys.CONFIGS, new TabItem<RowSet>(rs, rs.getRows().size()));
		model.put("results", results);
		
		start = timeService.getCurrentTime();
		final Map<String, List<PartitionInfo>> topics = kafkaClientService.getTopics(connectionSettings.getLinkName());
		end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(KafkaMessageKeys.TOPIC_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, topics.keySet(), end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KafkaMessageKeys.TOPICS_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Set<String> values, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.TOPIC, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		final Set<String> sortedTopics = new TreeSet<String>(values);
		for (String value : sortedTopics)
			rs.getRows().add(new DefaultResultRow(value, value));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, true);
		return (rs);
		}
	
	private RowSet buildNodeRowSet(Collection<Node> nodes, long time)
		{
		final Query query = new ViewImpl(KafkaMessageKeys.NODES, null, null, null, null, null, null);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(5);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.ID, ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.HOST, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.PORT, ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.RACK, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, 0, columns);
		rs.setQueryTime(time);
		
		for (Node node : nodes)
			rs.getRows().add(new DefaultResultRow(String.valueOf(node.id()), node.host(), String.valueOf(node.port()), node.rack()));
		
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/partitions.html", method = RequestMethod.GET)
	public Map<String, Object> showPartitions(
			@RequestParam("topic") String topic
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		
		long start, end;
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
		start = timeService.getCurrentTime();
		final Collection<AclBinding> acls = kafkaClientService.getTopicAcls(connectionSettings.getLinkName(), topic);
		end = timeService.getCurrentTime();
		results.put(KafkaMessageKeys.ACLS, new TabItem<RowSet>(buildAclRowSet(acls, end - start), acls.size()));
		
		start = timeService.getCurrentTime();
		final Map<ConfigResource, Config> configs = kafkaClientService.getTopicConfigs(connectionSettings.getLinkName(), topic);
		end = timeService.getCurrentTime();
		final RowSet rs = buildConfigRowSet(configs, end - start);
		results.put(KafkaMessageKeys.CONFIGS, new TabItem<RowSet>(rs, rs.getRows().size()));
		model.put("results", results);
		
		start = timeService.getCurrentTime();
		final List<PartitionInfo> partitions = kafkaClientService.getPartitions(connectionSettings.getLinkName(), topic);
		end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KafkaMessageKeys.TOPIC_LEVEL, null));
		final Query query = new ViewImpl(KafkaMessageKeys.PARTITION_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, partitions, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KafkaMessageKeys.PARTITIONS_TAB, new TabItem<RowSet>(cats, cats.getRows().size() - 1));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(topic)));
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, List<PartitionInfo> partitions, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(5);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.ID, ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.PARTITION, ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.REPLICAS, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.IN_SYNC_REPLICAS, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.LEADER, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		final Set<PartitionInfoBean> sortedPartitions = new TreeSet<PartitionInfoBean>();
		for (PartitionInfo pi : partitions)
			sortedPartitions.add(new PartitionInfoBean(pi));
		
		rs.getRows().add(new DefaultResultRow(null, "All", "&nbsp;", "&nbsp;", "&nbsp;"));
		for (PartitionInfoBean p : sortedPartitions)
			rs.getRows().add(new DefaultResultRow(String.valueOf(p.getPartition()), String.valueOf(p.getPartition()), p.getReplicas(), p.getInSyncReplicas(), p.getLeader()));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, true);
		return (rs);
		}

	private RowSet buildAclRowSet(Collection<AclBinding> acls, long time)
		{
		final Query query = new ViewImpl(KafkaMessageKeys.ACLS, null, null, null, null, null, null);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(5);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.PRINCIPAL, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.HOST, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.OPERATION, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.PERMISSION_TYPE, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, 0, columns);
		rs.setQueryTime(time);
		
		for (AclBinding acl : acls)
			rs.getRows().add(new DefaultResultRow(acl.entry().principal(), acl.entry().host(), acl.entry().operation().toString(), acl.entry().permissionType().toString()));
		
		return (rs);
		}
	
	private RowSet buildConfigRowSet(Map<ConfigResource, Config> configs, long time)
		{
		final Query query = new ViewImpl(KafkaMessageKeys.CONFIGS, null, null, null, null, null, null);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(3);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.RESOURCE, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.KEY, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.VALUE, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, 0, columns);
		rs.setQueryTime(time);
		
		final Map<String, Map<String, String>> sortedConfigs = new TreeMap<String, Map<String, String>>();
		for (Map.Entry<ConfigResource, Config> ent : configs.entrySet())
			{
			final Map<String, String> sortedEntries = new TreeMap<String, String>();
			for (ConfigEntry config : ent.getValue().entries())
				sortedEntries.put(config.name(), config.value());
			sortedConfigs.put(ent.getKey().name(), sortedEntries);
			}
		
		for (Map.Entry<String, Map<String, String>> ent : sortedConfigs.entrySet())
			{
			for (Map.Entry<String, String> config : ent.getValue().entrySet())
				rs.getRows().add(new DefaultResultRow(ent.getKey(), config.getKey(), config.getValue()));
			}
		
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Message offset
	 * @param key Key to match
	 * @param value Value search term
	 * @param compact Compact messages
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/messages.html", method = RequestMethod.GET)
	public Map<String, Object> showMessages(
			@RequestParam("topic") String topic,
			@RequestParam(value = "partition", required = false) Integer partition,
			@RequestParam(value = "offset", required = false) Long offset,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "compact", required = false) Boolean compact
			)
		{
		return (showMessagesInternal(topic, partition, offset, key, value, compact));
		}
	
	/**
	 * Show the file browser
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Message offset
	 * @param key Key to match
	 * @param value Value search term
	 * @param compact Compact messages
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/messages.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxMessages(
			@RequestParam("topic") String topic,
			@RequestParam(value = "partition", required = false) Integer partition,
			@RequestParam(value = "offset", required = false) Long offset,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "value", required = false) String value,
			@RequestParam(value = "compact", required = false) Boolean compact
			)
		{
		return (showMessagesInternal(topic, partition, offset, key, value, compact));
		}
	
	private Map<String, Object> showMessagesInternal(
			String topic,
			Integer partition,
			Long offset,
			String key,
			String value,
			Boolean compact
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		model.put("partition", partition);
		model.put("key", key);
		model.put("value", value);
		model.put("compact", compact);
		
		final OffsetInfo offsets = kafkaClientService.getOffsetInfo(connectionSettings.getLinkName(), topic, partition);
		model.put("startOffset", offsets.getMinOffset());
		model.put("endOffset", offsets.getMaxOffset());
		model.put("currentOffset", offsets.getCurrentOffset());
		
		final Long effectiveOffset;
		if (offset == null)
			effectiveOffset = topicStateManager.getLastOffset(topic, partition);
		else
			effectiveOffset = offset;
		model.put("offset", effectiveOffset);
		
		final List<ConsumerRecord<String, String>> records = kafkaClientService.fetchRecords(connectionSettings.getLinkName(), topic, partition, effectiveOffset, null, StringUtils.nullIfEmpty(key), StringUtils.nullIfEmpty(value));
		
		final List<ConsumerRecordBean> l;
		Long minOffset = null;
		Long maxOffset = null;
		if ((compact != null) && compact.booleanValue())
			{
			final Map<String, ConsumerRecordBean> s = new HashMap<String, ConsumerRecordBean>();
			for (ConsumerRecord<String, String> record : records)
				{
				final ConsumerRecordBean bean = new ConsumerRecordBean(record);
				final String ukey = bean.getPartition() + ":" + bean.getKey();
				if (bean.getValue() == null)
					s.remove(ukey);
				else
					s.put(ukey, bean);
				if ((minOffset == null) || (bean.getOffset() < minOffset))
					minOffset = bean.getOffset();
				if ((maxOffset == null) || (bean.getOffset() > maxOffset))
					maxOffset = bean.getOffset();
				}
			l = new ArrayList<ConsumerRecordBean>(s.values());
			}
		else
			{
			l = new ArrayList<ConsumerRecordBean>(records.size());
			for (ConsumerRecord<String, String> record : records)
				{
				final ConsumerRecordBean bean = new ConsumerRecordBean(record);
				l.add(bean);
				if ((minOffset == null) || (bean.getOffset() < minOffset))
					minOffset = bean.getOffset();
				if ((maxOffset == null) || (bean.getOffset() > maxOffset))
					maxOffset = bean.getOffset();
				}
			}
		Collections.sort(l);
		
		model.put("minOffset", minOffset);
		model.put("maxOffset", maxOffset);
		
		if ((effectiveOffset != null) && (minOffset != null))
			topicStateManager.setLastOffset(topic, partition, minOffset);
		
		if ((minOffset != null) && (offsets.getMinOffset() != null) && (minOffset > offsets.getMinOffset()))
			model.put("prevOffset", Math.max(offsets.getMinOffset(), minOffset - (l.isEmpty() ? 1 : l.size())));
		if ((maxOffset != null) && (offsets.getMaxOffset() != null) && (maxOffset < offsets.getMaxOffset()))
			model.put("nextOffset", maxOffset + 1);
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		levels.add(new SubQueryDefImpl(KafkaMessageKeys.TOPIC_LEVEL, null));
		levels.add(new SubQueryDefImpl(KafkaMessageKeys.PARTITION_LEVEL, null));
		final Query query = new ViewImpl(KafkaMessageKeys.MESSAGE_LEVEL, null, null, null, null, levels, null);
		
		final Map<String, TabItem<List<ConsumerRecordBean>>> tabs = new HashMap<String, TabItem<List<ConsumerRecordBean>>>(1);
		tabs.put(KafkaMessageKeys.MESSAGES_TAB, new TabItem<List<ConsumerRecordBean>>(l, l.size()));
		
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(Arrays.asList(topic, (partition == null) ? "" : String.valueOf(partition))));
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
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
		return (showMessageInternal(topic, partition, offset, format, formatting));
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
		return (showMessageInternal(topic, partition, offset, format, formatting));
		}
	
	private Map<String, Object> showMessageInternal(String topic, Integer partition, Long offset, String format, Boolean formatting)
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
		
		model.put("topic", topic);
		model.put("partition", partition);
		model.put("offset", offset);
		model.put("format", formatName);
		model.put("formatting", formattingActive);
		
		model.put("formats", textFormatterService.getSupportedTextFormats());
		
		final ConsumerRecord<String, String> record = kafkaClientService.fetchRecord(connectionSettings.getLinkName(), topic, partition, offset);
		
		final ConsumerRecordBean rec;
		if (record == null)
			rec = null;
		else
			{
			final Set<TextTransformerService.Option> options = EnumSet.of(TextTransformerService.Option.SYNTAX_COLORING, TextTransformerService.Option.LINE_NUMBERS);
			if (formattingActive)
				options.add(TextTransformerService.Option.FORMATTING);
			rec = new ConsumerRecordBean(record, textFormatterService.format(record.value(), formatName, options));
			}
		
		final Map<String, TabItem<ConsumerRecordBean>> tabs = new HashMap<String, TabItem<ConsumerRecordBean>>(1);
		tabs.put(offset.toString(), new TabItem<ConsumerRecordBean>(rec, 1));
		
		model.put("tabs", tabs);
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/kafka-reload.html", method = RequestMethod.GET)
	public String clearCache()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();

		kafkaClientService.flushCache(connectionSettings.getLinkName());

		return (ViewConstants.EMPTY_VIEW);
		}
	}
