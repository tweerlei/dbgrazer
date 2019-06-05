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

/**
 * Parse Kafka query strings:
 * topic:start-end:partition:key
 * 
 * @author Robert Wruck
 */
public class KafkaQueryParser
	{
	private final String topic;
	private final Integer partition;
	private final Long startOffset;
	private final Long endOffset;
	private final String key;
	
	/**
	 * Constructor
	 * @param statement Statement to parse
	 */
	public KafkaQueryParser(String statement)
		{
		final String[] fields = statement.split(":", 4);
		topic = fields[0];
		if (fields.length > 2)
			{
			partition = parseInt(fields[2]);
			final String[] offsets = fields[1].split("-");
			startOffset = parseLong(offsets[0]);
			if (offsets.length > 1)
				endOffset = parseLong(offsets[1]);
			else
				endOffset = null;
			if (fields.length > 3)
				key = fields[3].trim();
			else
				key = null;
			}
		else if (fields.length > 1)
			{
			partition = 0;
			final String[] offsets = fields[1].split("-");
			startOffset = parseLong(offsets[0]);
			if (offsets.length > 1)
				endOffset = parseLong(offsets[1]);
			else
				endOffset = null;
			key = null;
			}
		else
			{
			partition = 0;
			startOffset = null;
			endOffset = null;
			key = null;
			}
		}
	
	private Integer parseInt(String s)
		{
		try	{
			return (Integer.valueOf(s));
			}
		catch (NumberFormatException e)
			{
			return (null);
			}
		}
	
	private Long parseLong(String s)
		{
		try	{
			return (Long.valueOf(s));
			}
		catch (NumberFormatException e)
			{
			return (null);
			}
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
	public Integer getPartition()
		{
		return partition;
		}
	
	/**
	 * @return the startOffset
	 */
	public Long getStartOffset()
		{
		return startOffset;
		}
	
	/**
	 * @return the endOffset
	 */
	public Long getEndOffset()
		{
		return endOffset;
		}
	
	/**
	 * @return the key
	 */
	public String getKey()
		{
		return key;
		}
	}
