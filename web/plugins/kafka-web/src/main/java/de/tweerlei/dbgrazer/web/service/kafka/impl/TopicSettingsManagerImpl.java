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
package de.tweerlei.dbgrazer.web.service.kafka.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.model.UserObjectKey;
import de.tweerlei.dbgrazer.web.service.kafka.TopicSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Manage query settings
 * 
 * @author Robert Wruck
 */
@Service
public class TopicSettingsManagerImpl implements TopicSettingsManager
	{
	private static final UserObjectKey<TopicSettings> KEY_TOPICSTATE = UserObjectKey.create(TopicSettings.class, true);
	
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public TopicSettingsManagerImpl(ConnectionSettings connectionSettings)
		{
		this.connectionSettings = connectionSettings;
		}
	
	private Map<String, Long> getOffsets()
		{
		TopicSettings ret = connectionSettings.getUserObject(KEY_TOPICSTATE);
		
		if (ret == null)
			{
			ret = new TopicSettings();
			connectionSettings.setUserObject(KEY_TOPICSTATE, ret);
			}
		
		return (ret.getOffsets());
		}
	
	@Override
	public void setLastOffset(String topic, int partition, long offset)
		{
		getOffsets().put(topic + ":" + partition, offset);
		}
	
	@Override
	public Long getLastOffset(String topic, int partition)
		{
		return (getOffsets().get(topic + ":" + partition));
		}
	
	@Override
	public void clearOffsets()
		{
		getOffsets().clear();
		}
	}
