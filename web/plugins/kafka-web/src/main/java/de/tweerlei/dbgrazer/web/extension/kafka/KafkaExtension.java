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
package de.tweerlei.dbgrazer.web.extension.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.kafka.KafkaConstants;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.extension.ExtensionLink;
import de.tweerlei.dbgrazer.web.extension.FrontendExtensionAdapter;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * JDBC FrontendExtension
 * 
 * @author Robert Wruck
 */
@Service
@Order(8)
public class KafkaExtension extends FrontendExtensionAdapter
	{
	private final FrontendHelperService frontendHelper;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param frontendHelper FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaExtension(FrontendHelperService frontendHelper, ConnectionSettings connectionSettings)
		{
		super("Kafka");
		this.frontendHelper = frontendHelper;
		this.connectionSettings = connectionSettings;
		}
	
	@Override
	public List<ExtensionLink> getEditMenuExtensions()
		{
		if (!connectionSettings.getType().getName().equals(KafkaConstants.LINKTYPE_KAFKA))
			return (Collections.emptyList());
		
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		if (connectionSettings.isBrowserEnabled())
			{
			ret.add(new ExtensionLink("kafkaBrowser", frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "topics.html", null), null, null));
			}
		
		return (ret);
		}
	}
