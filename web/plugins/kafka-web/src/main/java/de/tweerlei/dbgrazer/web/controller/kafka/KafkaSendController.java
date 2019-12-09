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

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KafkaSendController
	{
	private static final String ATTR_KEY = "key";
	
	private final KafkaApiService kafkaClientService;
	private final QuerySettingsManager querySettingsManager;
	private final FrontendHelperService frontendHelperService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaApiService
	 * @param querySettingsManager QuerySettingsManager
	 * @param frontendHelperService FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaSendController(KafkaApiService kafkaClientService, FrontendHelperService frontendHelperService,
			QuerySettingsManager querySettingsManager, ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.querySettingsManager = querySettingsManager;
		this.frontendHelperService = frontendHelperService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the send dialog
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param offset Message to copy from
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/send-message.html", method = RequestMethod.GET)
	public Map<String, Object> showSendDialog(
			@RequestParam(value = "topic", required = false) String topic,
			@RequestParam(value = "partition", required = false) Integer partition,
			@RequestParam(value = "offset", required = false) Long offset
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		model.put("partition", partition);
		
		if ((topic != null) && (partition != null) && (offset != null))
			{
			final ConsumerRecord<String, String> record = kafkaClientService.fetchRecord(connectionSettings.getLinkName(), topic, partition, offset);
			if (record != null)
				{
				model.put("key", record.key());
				model.put("message", record.value());
				}
			}
		else
			{
			model.put("key", connectionSettings.getCustomQuery().getAttributes().get(ATTR_KEY));
			model.put("message", connectionSettings.getCustomQuery().getQuery());
			}
		
		return (model);
		}
	
	/**
	 * Send a message
	 * @param topic Topic name
	 * @param partition Partition number
	 * @param key Message key
	 * @param message Message body
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/send-message.html", method = RequestMethod.POST)
	public Map<String, Object> sendMessage(
			@RequestParam("topic") String topic,
			@RequestParam(value = "partition", required = false) Integer partition,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "message", required = false) String message
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(message))
			{
			querySettingsManager.addCustomHistoryEntry(message);
			connectionSettings.getCustomQuery().setQuery(message);
			connectionSettings.getCustomQuery().modify();
			}
		connectionSettings.getCustomQuery().getAttributes().put(ATTR_KEY, key);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			final RecordMetadata md = kafkaClientService.sendRecord(connectionSettings.getLinkName(), topic, partition, key, message);
			model.put("result", frontendHelperService.toJSONString(String.valueOf(md.offset())));
			model.put("exceptionText", null);
			}
		catch (RuntimeException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	}
