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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * DownloadSource that sends the contents of an InputStream
 * 
 * @author Robert Wruck
 */
public class KafkaMessageDownloadSource extends AbstractDownloadSource
	{
	private static final String OUTPUT_CHARSET = "UTF-8";
	
	private final KafkaApiService kafkaClientService;
	private final String link;
	private final String topic;
	private final int partition;
	private final long offset;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaApiService
	 * @param link Link name
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Message offset
	 */
	public KafkaMessageDownloadSource(KafkaApiService kafkaClientService,
			String link, String topic, int partition, long offset)
		{
		this.kafkaClientService = kafkaClientService;
		this.link = link;
		this.topic = topic;
		this.partition = partition;
		this.offset = offset;
		
		this.setAttachment(true);
		this.setExpireTime(DownloadSource.ALWAYS);
		this.setFileName(offset + ".txt");
		}
	
	@Override
	public String getContentType()
		{
		return ("application/octet-stream");
		}
	
	@Override
	public void write(OutputStream stream) throws IOException
		{
		final ConsumerRecord<String, String> record = kafkaClientService.fetchRecord(link, topic, partition, offset);
		if (record != null)
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, OUTPUT_CHARSET);
			w.write(record.value());
			w.flush();
			}
		}
	}
