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
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Topics tab title */
	public static final String TOPICS_TAB = "$kafkaTopicsTab";
	/** Messages tab title */
	public static final String MESSAGES_TAB = "$kafkaMessagesTab";
	
	
	private KafkaMessageKeys()
		{
		}
	}
