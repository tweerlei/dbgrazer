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
package de.tweerlei.dbgrazer.extension.kafka.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.resource.ResourceFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.kafka.ConfigKeys;
import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.dbgrazer.extension.kafka.KafkaClientService;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KafkaApiServiceImpl implements KafkaApiService
	{
	private static class TopicMetadataHolder
		{
		private final List<PartitionInfo> partitions;
		private Collection<AclBinding> acls;
		private Map<ConfigResource, Config> configs;
		
		public TopicMetadataHolder(List<PartitionInfo> partitions)
			{
			this.partitions = partitions;
			}
		
		public List<PartitionInfo> getPartitions()
			{
			return partitions;
			}
		
		public Collection<AclBinding> getAcls()
			{
			return acls;
			}

		public void setAcls(Collection<AclBinding> acls)
			{
			this.acls = acls;
			}

		public Map<ConfigResource, Config> getConfigs()
			{
			return configs;
			}

		public void setConfigs(Map<ConfigResource, Config> configs)
			{
			this.configs = configs;
			}
		}

	private static class KafkaMetadataHolder
		{
		private Collection<Node> nodes;
		private Collection<AclBinding> acls;
		private Map<ConfigResource, Config> configs;
		private Map<String, TopicMetadataHolder> topics;

		public KafkaMetadataHolder()
			{
			}

		public Collection<Node> getNodes()
			{
			return nodes;
			}

		public void setNodes(Collection<Node> nodes)
			{
			this.nodes = nodes;
			}

		public Collection<AclBinding> getAcls()
			{
			return acls;
			}

		public void setAcls(Collection<AclBinding> acls)
			{
			this.acls = acls;
			}

		public Map<ConfigResource, Config> getConfigs()
			{
			return configs;
			}

		public void setConfigs(Map<ConfigResource, Config> configs)
			{
			this.configs = configs;
			}

		public Map<String, TopicMetadataHolder> getTopics()
			{
			return topics;
			}
		
		public void setTopics(Map<String, TopicMetadataHolder> topics)
			{
			this.topics = topics;
			}
		}
	
	private final ConfigAccessor configService;
	private final KafkaClientService clientService;
	private final Map<String, KafkaMetadataHolder> metadataCache;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param clientService KafkaClientService
	 */
	@Autowired
	public KafkaApiServiceImpl(ConfigAccessor configService, KafkaClientService clientService)
		{
		this.configService = configService;
		this.clientService = clientService;
		this.metadataCache = new ConcurrentHashMap<String, KafkaMetadataHolder>();
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public int getMaxRows(String c)
		{
		return (configService.get(ConfigKeys.KAFKA_FETCH_LIMIT));
		}
	
	@Override
	public OffsetInfo getOffsetInfo(String c, String topic, Integer partition)
		{
		final Consumer<String, String> consumer = clientService.getConsumer(c);
		
		final Collection<TopicPartition> partitions;
		if (partition != null)
			partitions = Collections.singleton(new TopicPartition(topic, partition));
		else
			{
			final List<PartitionInfo> pinfos = consumer.partitionsFor(topic);
			partitions = new ArrayList<TopicPartition>(pinfos.size());
			for (PartitionInfo pi : pinfos)
				partitions.add(new TopicPartition(topic, pi.partition()));
			}
		
		Long startOffset = null;
		for (Long l : consumer.beginningOffsets(partitions).values())
			{
			if (startOffset == null || startOffset > l)
				startOffset = l;
			}
		
		Long endOffset = null;
		for (Long l : consumer.endOffsets(partitions).values())
			{
			if (endOffset == null || endOffset < l)
				endOffset = l - 1;
			}
		
		Long currentOffset = null;
		consumer.assign(partitions);
		for (TopicPartition tp : partitions)
			{
			final Long l = consumer.position(tp);
			if (currentOffset == null || currentOffset > l)
				currentOffset = l;
			}
		consumer.unsubscribe();
		
		return (new OffsetInfo(startOffset, endOffset, currentOffset));
		}
	
	@Override
	public ConsumerRecord<String, String> fetchRecord(String c, String topic, int partition, long offset)
		{
		final Consumer<String, String> consumer = clientService.getConsumer(c);
		final TopicPartition tp = new TopicPartition(topic, partition);
		
		consumer.assign(Collections.singleton(tp));
		try	{
			consumer.seek(tp, offset);
			final ConsumerRecords<String, String> records = consumer.poll(configService.get(ConfigKeys.KAFKA_FETCH_TIMEOUT));
			
			for (ConsumerRecord<String, String> record : records)
				{
				if (record.offset() == offset)
					return (record);
				}
			}
		finally
			{
			consumer.unsubscribe();
			}
		
		return (null);
		}
	
	private void seek(Consumer<String, String> consumer, List<TopicPartition> partitions, long offset)
		{
		if (offset < 0)
			{
			for (Map.Entry<TopicPartition, Long> ent : consumer.endOffsets(partitions).entrySet())
				{
				if (ent.getValue() != null)
					consumer.seek(ent.getKey(), Math.max(ent.getValue() + offset, 0));
				}
			}
		else
			{
			for (TopicPartition tp : partitions)
				consumer.seek(tp, offset);
			}
		}
	
	@Override
	public List<ConsumerRecord<String, String>> fetchRecords(String c, String topic, Integer partition, Long startOffset, Long endOffset, String key)
		{
		final Consumer<String, String> consumer = clientService.getConsumer(c);
		
		if (partition != null)
			{
			final TopicPartition tp = new TopicPartition(topic, partition);
			final List<TopicPartition> partitions = new ArrayList<TopicPartition>(1);
			partitions.add(tp);
			consumer.assign(partitions);
			
			if (startOffset != null)
				seek(consumer, partitions, startOffset);
			}
		else if (startOffset != null)
			{
			final List<PartitionInfo> pinfos = consumer.partitionsFor(topic);
			final List<TopicPartition> partitions = new ArrayList<TopicPartition>(pinfos.size());
			for (PartitionInfo pi : pinfos)
				partitions.add(new TopicPartition(topic, pi.partition()));
			consumer.assign(partitions);
			
			seek(consumer, partitions, startOffset);
			}
		else
			consumer.subscribe(Collections.singleton(topic));
		
		final List<ConsumerRecord<String, String>> ret = new LinkedList<ConsumerRecord<String, String>>();
		try	{
			final int limit = getMaxRows(c);
			int n = 0;
			while (n < limit)
				{
				final ConsumerRecords<String, String> records = consumer.poll(configService.get(ConfigKeys.KAFKA_FETCH_TIMEOUT));
				if (records.isEmpty())
					break;
				for (ConsumerRecord<String, String> rec : records)
					{
					if (((startOffset == null) || (rec.offset() >= startOffset))
							&& ((endOffset == null) || (rec.offset() <= endOffset))
							&& ((key == null) || key.equals(rec.key())))
						{
						ret.add(rec);
						n++;
						}
					}
				}
			}
		finally
			{
			consumer.unsubscribe();
			}
		
		return (ret);
		}
	
	@Override
	public RecordMetadata sendRecord(String c, String topic, Integer partition, String key, String value)
		{
		final Producer<String, String> producer = clientService.getProducer(c);
		
		final ProducerRecord<String, String> rec;
		if (partition != null)
			rec = new ProducerRecord<String, String>(topic, partition, key, value);
		else
			rec = new ProducerRecord<String, String>(topic, key, value);
		
		try	{
			return (producer.send(rec).get());
			}
		catch (ExecutionException e)
			{
			throw new RuntimeException(e.getCause());
			}
		catch (InterruptedException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	private KafkaMetadataHolder getKafkaMetadataHolder(String c)
		{
		KafkaMetadataHolder ret = metadataCache.get(c);
		if (ret == null)
			{
			ret = new KafkaMetadataHolder();
			metadataCache.put(c, ret);
			}
		return (ret);
		}
	
	private Map<String, TopicMetadataHolder> getTopicMetadata(String c)
		{
		final KafkaMetadataHolder md = getKafkaMetadataHolder(c);
		if (md.getTopics() == null)
			{
			try	{
				final Map<String, TopicMetadataHolder> map = new ConcurrentHashMap<String, TopicMetadataHolder>();
				final Map<String, List<PartitionInfo>> topics = clientService.getConsumer(c).listTopics();
				for (Map.Entry<String, List<PartitionInfo>> ent : topics.entrySet())
					{
					final TopicMetadataHolder tmd = new TopicMetadataHolder(ent.getValue());
					map.put(ent.getKey(), tmd);
					}
				md.setTopics(map);
				}
			catch (Exception e)
				{
				logger.log(Level.WARNING, "listTopics", e);
				return (Collections.emptyMap());
				}
			}
		return (md.getTopics());
		}
	
	private TopicMetadataHolder getTopicMetadataHolder(String c, String topic)
		{
		final Map<String, TopicMetadataHolder> md = getTopicMetadata(c);
		
		TopicMetadataHolder ret = md.get(topic);
		if (ret == null)
			{
			try	{
				final List<PartitionInfo> partitions = clientService.getConsumer(c).partitionsFor(topic);
				ret = new TopicMetadataHolder(partitions);
				md.put(topic, ret);
				}
			catch (Exception e)
				{
				logger.log(Level.WARNING, "partitionsFor", e);
				return (new TopicMetadataHolder(Collections.<PartitionInfo>emptyList()));
				}
			}
		return (ret);
		}
	
	@Override
	public Collection<Node> getNodes(String c)
		{
		final KafkaMetadataHolder md = getKafkaMetadataHolder(c);
		if (md.getNodes() != null)
			return (md.getNodes());
		
		try	{
			final Collection<Node> nodes = clientService.getAdminClient(c).describeCluster().nodes().get();
			md.setNodes(nodes);
			return (nodes);
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "getNodes", e);
			return (Collections.emptyList());
			}
		}
	
	@Override
	public Collection<AclBinding> getClusterAcls(String c)
		{
		final KafkaMetadataHolder md = getKafkaMetadataHolder(c);
		if (md.getAcls() != null)
			return (md.getAcls());
		
		try	{
			final AclBindingFilter filter = new AclBindingFilter(new ResourceFilter(ResourceType.CLUSTER, null), AccessControlEntryFilter.ANY);
			final Collection<AclBinding> acls = clientService.getAdminClient(c).describeAcls(filter).values().get();
			md.setAcls(acls);
			return (acls);
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "getClusterAcls", e);
			return (Collections.emptyList());
			}
		}
	
	@Override
	public Map<ConfigResource, Config> getClusterConfigs(String c)
		{
		final KafkaMetadataHolder md = getKafkaMetadataHolder(c);
		if (md.getConfigs() != null)
			return (md.getConfigs());
		
		try	{
			final Collection<Node> nodes = getNodes(c);
			final List<ConfigResource> rsrc = new ArrayList<ConfigResource>(nodes.size());
			for (Node node : nodes)
				rsrc.add(new ConfigResource(ConfigResource.Type.BROKER, node.idString()));
			final Map<ConfigResource, Config> configs = clientService.getAdminClient(c).describeConfigs(rsrc).all().get();
			md.setConfigs(configs);
			return (configs);
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "getClusterConfigs", e);
			return (Collections.emptyMap());
			}
		}
	
	@Override
	public Map<String, List<PartitionInfo>> getTopics(String c)
		{
		final Map<String, List<PartitionInfo>> ret = new TreeMap<String, List<PartitionInfo>>();
		
		final Map<String, TopicMetadataHolder> topics = getTopicMetadata(c);
		for (Map.Entry<String, TopicMetadataHolder> ent : topics.entrySet())
			ret.put(ent.getKey(), ent.getValue().getPartitions());
		return (ret);
		}
	
	@Override
	public Collection<AclBinding> getTopicAcls(String c, String topic)
		{
		final TopicMetadataHolder md = getTopicMetadataHolder(c, topic);
		if (md.getAcls() != null)
			return (md.getAcls());
		
		try	{
			final AclBindingFilter filter = new AclBindingFilter(new ResourceFilter(ResourceType.TOPIC, topic), AccessControlEntryFilter.ANY);
			final Collection<AclBinding> acls = clientService.getAdminClient(c).describeAcls(filter).values().get();
			md.setAcls(acls);
			return (acls);
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "getTopicAcls", e);
			return (Collections.emptyList());
			}
		}
	
	@Override
	public Map<ConfigResource, Config> getTopicConfigs(String c, String topic)
		{
		final TopicMetadataHolder md = getTopicMetadataHolder(c, topic);
		if (md.getConfigs() != null)
			return (md.getConfigs());
		
		try	{
			final ConfigResource rsrc = new ConfigResource(ConfigResource.Type.TOPIC, topic);
			final Map<ConfigResource, Config> configs = clientService.getAdminClient(c).describeConfigs(Collections.singleton(rsrc)).all().get();
			md.setConfigs(configs);
			return (configs);
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "getTopicConfigs", e);
			return (Collections.emptyMap());
			}
		}
	
	@Override
	public List<PartitionInfo> getPartitions(String c, String topic)
		{
		final TopicMetadataHolder md = getTopicMetadataHolder(c, topic);
		return (md.getPartitions());
		}
	
	@Override
	public void flushCache(String link)
		{
		if (link == null)
			metadataCache.clear();
		else
			metadataCache.remove(link);
		}
	}
