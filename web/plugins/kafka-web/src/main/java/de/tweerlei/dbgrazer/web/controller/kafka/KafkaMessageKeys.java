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

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class KafkaMessageKeys
	{
	/** JS extension file */
	public static final String EXTENSION_JS = "kafka.js";
	
	/*
	 * Multilevel names for the DB browser
	 */
	
	/** Topic tab title */
	public static final String TOPIC_LEVEL = "$topicLevel";
	/** Partition tab title */
	public static final String PARTITION_LEVEL = "$partitionLevel";
	/** Message tab title */
	public static final String MESSAGE_LEVEL = "$partitionLevel";
	
	/*
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Topics tab title */
	public static final String TOPICS_TAB = "$kafkaTopicsTab";
	/** Partitions tab title */
	public static final String PARTITIONS_TAB = "$kafkaPartitionsTab";
	/** Messages tab title */
	public static final String MESSAGES_TAB = "$kafkaMessagesTab";
	
	/*
	 * Column names
	 */
	/** ID column */
	public static final String ID = "id";
	/** Name column */
	public static final String TOPIC = "kafkaTopic";
	/** Name column */
	public static final String PARTITION = "kafkaPartition";
	/** ID column */
	public static final String REPLICAS = "replicas";
	/** ID column */
	public static final String IN_SYNC_REPLICAS = "inSyncReplicas";
	/** ID column */
	public static final String LEADER = "leader";
	/** ID column */
	public static final String NODES = "$nodes";
	/** ID column */
	public static final String HOST = "nodeHost";
	/** ID column */
	public static final String PORT = "nodePort";
	/** ID column */
	public static final String RACK = "nodeRack";
	/** ID column */
	public static final String ACLS = "$acls";
	/** ID column */
	public static final String PRINCIPAL = "aclPrincipal";
	/** ID column */
	public static final String OPERATION = "aclOperation";
	/** ID column */
	public static final String PERMISSION_TYPE = "aclPermissionType";
	/** ID column */
	public static final String CONFIGS = "$configs";
	/** ID column */
	public static final String RESOURCE = "configResource";
	/** ID column */
	public static final String KEY = "configKey";
	/** ID column */
	public static final String VALUE = "configValue";
	
	
	private KafkaMessageKeys()
		{
		}
	}
