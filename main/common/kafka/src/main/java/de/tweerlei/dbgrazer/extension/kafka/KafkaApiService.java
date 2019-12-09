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
package de.tweerlei.dbgrazer.extension.kafka;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.config.ConfigResource;

/**
 * Perform HTTP requests
 * 
 * @author Robert Wruck
 */
public interface KafkaApiService
	{
	/**
	 * Offset information
	 */
	public static final class OffsetInfo
		{
		private final Long minOffset;
		private final Long maxOffset;
		private final Long currentOffset;
		
		/**
		 * Constructor
		 * @param minOffset Minimum valid offset
		 * @param maxOffset Maximum valid offset
		 * @param currentOffset Current offset
		 */
		public OffsetInfo(Long minOffset, Long maxOffset, Long currentOffset)
			{
			this.minOffset = minOffset;
			this.maxOffset = maxOffset;
			this.currentOffset = currentOffset;
			}
		
		/**
		 * Get the minOffset
		 * @return the minOffset
		 */
		public Long getMinOffset()
			{
			return minOffset;
			}
		
		/**
		 * Get the maxOffset
		 * @return the maxOffset
		 */
		public Long getMaxOffset()
			{
			return maxOffset;
			}
		
		/**
		 * Get the currentOffset
		 * @return the currentOffset
		 */
		public Long getCurrentOffset()
			{
			return currentOffset;
			}
		}	
	
	/**
	 * Get the configured max. rows to fetch
	 * @param c Link name
	 * @return Max row count
	 */
	public int getMaxRows(String c);
	
	/**
	 * Get offset information
	 * @param c Link name
	 * @param topic Topic name
	 * @param partition Partition number (null to fetch from all partitions)
	 * @return ConsumerRecord or null
	 */
	public OffsetInfo getOffsetInfo(String c, String topic, Integer partition);
	
	/**
	 * Fetch a single record
	 * @param c Link name
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Record offset
	 * @return ConsumerRecord or null
	 */
	public ConsumerRecord<String, String> fetchRecord(String c, String topic, int partition, long offset);
	
	/**
	 * Fetch records
	 * @param c Link name
	 * @param topic Topic name
	 * @param partition Partition number (null to fetch from all partitions)
	 * @param startOffset Record offset (null to fetch from current offset)
	 * @param endOffset Max. Record offset (null to fetch from current offset)
	 * @param key Optional key to match
	 * @return ConsumerRecord or null
	 */
	public List<ConsumerRecord<String, String>> fetchRecords(String c, String topic, Integer partition, Long startOffset, Long endOffset, String key);
	
	/**
	 * Send a record
	 * @param c Link name
	 * @param topic Topic name
	 * @param partition Partition number (null to send to any partition)
	 * @param key Message key
	 * @param value Message value
	 * @return RecordMetadata
	 */
	public RecordMetadata sendRecord(String c, String topic, Integer partition, String key, String value);
	
	/**
	 * Get the Kafka nodes
	 * @param c Link name
	 * @return Nodes
	 */
	public Collection<Node> getNodes(String c);
	
	/**
	 * Get the Kafka cluster ACLs
	 * @param c Link name
	 * @return AclBindings
	 */
	public Collection<AclBinding> getClusterAcls(String c);
	
	/**
	 * Get the configuration for all nodes
	 * @param c Link name
	 * @return Map: ConfigResource (BROKER) -> Config
	 */
	public Map<ConfigResource, Config> getClusterConfigs(String c);
	
	/**
	 * Get all Kafka topics
	 * @param c Link name
	 * @return Map: Topic name -> PartitionInfo
	 */
	public Map<String, List<PartitionInfo>> getTopics(String c);
	
	/**
	 * Get the Kafka topic ACLs
	 * @param c Link name
	 * @param topic Topic name
	 * @return AclBindings
	 */
	public Collection<AclBinding> getTopicAcls(String c, String topic);
	
	/**
	 * Get the configuration for a topic
	 * @param c Link name
	 * @param topic Topic name
	 * @return Map: ConfigResource (BROKER) -> Config
	 */
	public Map<ConfigResource, Config> getTopicConfigs(String c, String topic);
	
	/**
	 * Get topic partitions
	 * @param c Link name
	 * @param topic Topic name
	 * @return PartitionInfos
	 */
	public List<PartitionInfo> getPartitions(String c, String topic);
	
	/**
	 * Get consumer groups
	 * @param c Link name
	 * @return Consumer groups
	 */
	public Collection<ConsumerGroupListing> getConsumerGroups(String c);
	
	/**
	 * Get consumer group details
	 * @param c Link name
	 * @param group Consumer group name
	 * @return ConsumerGroupDescription
	 */
	public ConsumerGroupDescription getConsumerGroup(String c, String group);
	
	/**
	 * Get consumer group offsets per topic partition
	 * @param c Link name
	 * @param group Consumer group name
	 * @return Map: TopicPartition -> OffsetAndMetadata
	 */
	public Map<TopicPartition, OffsetAndMetadata> getConsumerGroupOffsets(String c, String group);
	
	/**
	 * Seek a consumer group to a given offset in a topic partition
	 * @param c Link name
	 * @param group Consumer group name
	 * @param topic Topic name
	 * @param partition Partition
	 * @param offset Offset
	 */
	public void seekConsumerGroup(String c, String group, String topic, int partition, long offset);
	
	/**
	 * Flush the metadata cache
	 * @param link Link name
	 */
	public void flushCache(String link);
	}
