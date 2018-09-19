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

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.extension.kafka.KafkaClientService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KafkaSendController
	{
	private final KafkaClientService kafkaClientService;
	private final FrontendHelperService frontendHelperService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaClientService
	 * @param frontendHelperService FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaSendController(KafkaClientService kafkaClientService, FrontendHelperService frontendHelperService,
			ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.frontendHelperService = frontendHelperService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the send dialog
	 * @param topic Topic name
	 * @param partition Partition number
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/send-message.html", method = RequestMethod.GET)
	public Map<String, Object> showSendDialog(
			@RequestParam(value = "topic", required = false) String topic,
			@RequestParam(value = "partition", required = false) Integer partition
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("topic", topic);
		model.put("partition", partition);
		
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
			@RequestParam("key") String key,
			@RequestParam("message") String message
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
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
