package de.tweerlei.dbgrazer.web.controller.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
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
 * @author Robert Wruck <wruck@tweerlei.de>
 */
@Controller
public class KafkaAdminController
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
	public KafkaAdminController(KafkaClientService kafkaClientService,
			FrontendHelperService frontendHelperService,
			ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.frontendHelperService = frontendHelperService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the send dialog
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/create-topic.html", method = RequestMethod.GET)
	public Map<String, Object> showCreateTopicDialog()
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param name Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/create-topic.html", method = RequestMethod.POST)
	public Map<String, Object> createTopic(@RequestParam("topic") String name)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		final NewTopic topic = new NewTopic(name, 1, (short) 1);
		
		try	{
			kafkaClientService.getAdminClient(connectionSettings.getLinkName()).createTopics(Collections.singleton(topic)).all().get();
			model.put("exceptionText", null);
			}
		catch (Exception e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	
	/**
	 * Show the file browser
	 * @param name Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/delete-topic.html", method = RequestMethod.GET)
	public String deleteTopic(@RequestParam("q") String name)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			kafkaClientService.getAdminClient(connectionSettings.getLinkName()).deleteTopics(Collections.singleton(name)).all().get();
			model.put("exceptionText", null);
			}
		catch (Exception e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return ("redirect:topics.html");
		}
	}
