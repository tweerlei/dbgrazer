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
package de.tweerlei.dbgrazer.web.service.kafka;

/**
 * Manage query settings
 * 
 * @author Robert Wruck
 */
public interface TopicSettingsManager
	{
	/**
	 * Save the last offset for a TopicPartition
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Offset
	 */
	public void setLastOffset(String topic, int partition, long offset);
	
	/**
	 * Get the last offset for a TopicPartition
	 * @param topic Topic name
	 * @param partition Partition number
	 * @return Offset or null
	 */
	public Long getLastOffset(String topic, int partition);
	
	/**
	 * Clear all saved offsets
	 */
	public void clearOffsets();
	}