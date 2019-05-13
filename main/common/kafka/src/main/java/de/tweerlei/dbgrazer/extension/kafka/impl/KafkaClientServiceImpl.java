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
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.extension.kafka.ConfigKeys;
import de.tweerlei.dbgrazer.extension.kafka.KafkaClientService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KafkaClientServiceImpl implements KafkaClientService, LinkListener, LinkManager
	{
	private static class KafkaConnectionHolder
		{
		private final List<Consumer<String, String>> allConsumers;
		private final ThreadLocal<Consumer<String, String>> activeConsumer;
		private final List<Producer<String, String>> allProducers;
		private final ThreadLocal<Producer<String, String>> activeProducer;
		private final List<AdminClient> allAdminClients;
		private final ThreadLocal<AdminClient> activeAdminClient;
		
		public KafkaConnectionHolder()
			{
			this.allConsumers = new LinkedList<Consumer<String, String>>();
			this.activeConsumer = new ThreadLocal<Consumer<String, String>>();
			this.allProducers = new LinkedList<Producer<String, String>>();
			this.activeProducer = new ThreadLocal<Producer<String, String>>();
			this.allAdminClients = new LinkedList<AdminClient>();
			this.activeAdminClient = new ThreadLocal<AdminClient>();
			}
		
		public Consumer<String, String> getConsumer()
			{
			return (activeConsumer.get());
			}
		
		public synchronized void setConsumer(Consumer<String, String> consumer)
			{
			allConsumers.add(consumer);
			activeConsumer.set(consumer);
			}
		
		public Producer<String, String> getProducer()
			{
			return (activeProducer.get());
			}
		
		public synchronized void setProducer(Producer<String, String> producer)
			{
			allProducers.add(producer);
			activeProducer.set(producer);
			}
		
		public AdminClient getAdminClient()
			{
			return (activeAdminClient.get());
			}
		
		public synchronized void setAdminClient(AdminClient adminClient)
			{
			allAdminClients.add(adminClient);
			activeAdminClient.set(adminClient);
			}
		
		public synchronized void close()
			{
			for (Consumer<String, String> consumer : allConsumers)
				consumer.wakeup();
			for (Producer<String, String> producer : allProducers)
				producer.close();
			for (AdminClient adminClient : allAdminClients)
				adminClient.close();
			}
		}
	
	private final ConfigFileStore configFileStore;
	private final ConfigAccessor configService;
	private final LinkService linkService;
	private final Logger logger;
	private final Map<String, KafkaConnectionHolder> activeConnections;
	
	/**
	 * Constructor
	 * @param configFileStore ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param linkService LinkService
	 */
	@Autowired
	public KafkaClientServiceImpl(ConfigFileStore configFileStore, ConfigAccessor configService, LinkService linkService)
		{
		this.configFileStore = configFileStore;
		this.configService = configService;
		this.linkService = linkService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.activeConnections = new ConcurrentHashMap<String, KafkaConnectionHolder>();
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		linkService.addListener(this);
		linkService.addManager(this);
		}
	
	@Override
	public void linksChanged()
		{
		closeConnections();
		}
	
	@Override
	public void linkChanged(String link)
		{
		closeConnection(link);
		}
	
	/**
	 * Close all connections
	 */
	@PreDestroy
	public synchronized void closeConnections()
		{
		logger.log(Level.INFO, "Closing " + activeConnections.size() + " connections");
		
		for (KafkaConnectionHolder holder : activeConnections.values())
			holder.close();
		
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final KafkaConnectionHolder holder = activeConnections.remove(link);
		if (holder != null)
			{
			logger.log(Level.INFO, "Closing connection " + link);
			holder.close();
			}
		}
	
	@Override
	public Consumer<String, String> getConsumer(String c)
		{
		final KafkaConnectionHolder holder = activeConnections.get(c);
		if (holder != null)
			{
			final Consumer<String, String> ret = holder.getConsumer();
			if (ret != null)
				return (ret);
			}
		
		return (createConsumer(c));
		}
	
	@Override
	public Producer<String, String> getProducer(String c)
		{
		final KafkaConnectionHolder holder = activeConnections.get(c);
		if (holder != null)
			{
			final Producer<String, String> ret = holder.getProducer();
			if (ret != null)
				return (ret);
			}
		
		return (createProducer(c));
		}
	
	@Override
	public AdminClient getAdminClient(String c)
		{
		final KafkaConnectionHolder holder = activeConnections.get(c);
		if (holder != null)
			{
			final AdminClient ret = holder.getAdminClient();
			if (ret != null)
				return (ret);
			}

		return (createAdminClient(c));
		}
	
	@Override
	public int getMaxRows(String c)
		{
		return (configService.get(ConfigKeys.KAFKA_FETCH_LIMIT));
		}
	
	@Override
	public OffsetInfo getOffsetInfo(String c, String topic, Integer partition)
		{
		final Consumer<String, String> consumer = getConsumer(c);
		
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
		final Consumer<String, String> consumer = getConsumer(c);
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
	
	@Override
	public List<ConsumerRecord<String, String>> fetchRecords(String c, String topic, Integer partition, Long startOffset, Long endOffset, String key)
		{
		final Consumer<String, String> consumer = getConsumer(c);
		
		if (partition != null)
			{
			final TopicPartition tp = new TopicPartition(topic, partition);
			consumer.assign(Collections.singleton(tp));
			if (startOffset != null)
				consumer.seek(tp, startOffset);
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
		final Producer<String, String> producer = getProducer(c);
		
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
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, KafkaConnectionHolder> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	
	private synchronized Consumer<String, String> createConsumer(String c)
		{
		KafkaConnectionHolder holder = activeConnections.get(c);
		Consumer<String, String> ret = null;
		if (holder != null)
			{
			ret = holder.getConsumer();
			if (ret != null)
				return (ret);
			}
		else
			{
			holder = new KafkaConnectionHolder();
			activeConnections.put(c, holder);
			}
		
		final Properties props = initKafkaProperties(c);
		
		// Always treat contents as Strings
		props.setProperty("key.deserializer", StringDeserializer.class.getName());
		props.setProperty("value.deserializer", StringDeserializer.class.getName());
		
		// Don't commit offsets, start at earliest message
		props.setProperty("enable.auto.commit", "false");
		props.setProperty("auto.offset.reset", "earliest");
		
		// Group ID is required for subscribe()
		if (!props.containsKey("group.id"))
			props.setProperty("group.id", holder.toString());
		
		ret = new KafkaConsumer<String, String>(props);
		
		holder.setConsumer(ret);
		return (ret);
		}
	
	private synchronized Producer<String, String> createProducer(String c)
		{
		KafkaConnectionHolder holder = activeConnections.get(c);
		Producer<String, String> ret = null;
		if (holder != null)
			{
			ret = holder.getProducer();
			if (ret != null)
				return (ret);
			}
		else
			{
			holder = new KafkaConnectionHolder();
			activeConnections.put(c, holder);
			}
		
		final Properties props = initKafkaProperties(c);
		
		// Always treat contents as Strings
		props.setProperty("key.serializer", StringSerializer.class.getName());
		props.setProperty("value.serializer", StringSerializer.class.getName());
		
		// Send each message exactly once
		props.setProperty("enable.idempotence", "true");
		
		ret = new KafkaProducer<String, String>(props);
		
		holder.setProducer(ret);
		return (ret);
		}
	
	private synchronized AdminClient createAdminClient(String c)
		{
		KafkaConnectionHolder holder = activeConnections.get(c);
		AdminClient ret = null;
		if (holder != null)
			{
			ret = holder.getAdminClient();
			if (ret != null)
				return (ret);
			}
		else
			{
			holder = new KafkaConnectionHolder();
			activeConnections.put(c, holder);
			}

		final Properties props = initKafkaProperties(c);

		ret = AdminClient.create(props);

		holder.setAdminClient(ret);
		return (ret);
		}
	
	private Properties initKafkaProperties(String c)
		{
		final LinkDef def = linkService.getLink(c, null);
		if ((def == null) /*|| !(def.getType() instanceof WebserviceLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		final Properties props = new Properties();
		props.putAll(def.getProperties());
		
		// Override specific properties from link settings 
		props.setProperty("bootstrap.servers", def.getUrl());
		if (!StringUtils.empty(def.getUsername()))
			props.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + def.getUsername() + "\" password=\"" + def.getPassword() + "\";");
		
		// Resolve key store paths
		final String keyStorePath = props.getProperty("ssl.keystore.location");
		if (!StringUtils.empty(keyStorePath))
			props.setProperty("ssl.keystore.location", configFileStore.getFileLocation(keyStorePath).getAbsolutePath());
		
		final String trustStorePath = props.getProperty("ssl.truststore.location");
		if (!StringUtils.empty(trustStorePath))
			props.setProperty("ssl.truststore.location", configFileStore.getFileLocation(trustStorePath).getAbsolutePath());
		
		return (props);
		}
	}
