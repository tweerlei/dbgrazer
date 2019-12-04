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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.extension.kafka.KafkaApiService;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.TimeService;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class KafkaConsumerGroupController
	{
	public static class ConsumerGroupBean
		{
		private final String id;
		private final boolean simple;
		private final ConsumerGroupState state;
		
		public ConsumerGroupBean(ConsumerGroupDescription desc)
			{
			this.id = desc.groupId();
			this.simple = desc.isSimpleConsumerGroup();
			this.state = desc.state();
			}
		
		public String getId()
			{
			return (id);
			}
		
		public boolean isSimple()
			{
			return (simple);
			}
		
		public ConsumerGroupState getState()
			{
			return (state);
			}
		}
	
	public static class ConsumerGroupAssignmentBean implements Comparable<ConsumerGroupAssignmentBean>
		{
		private final String topic;
		private final int partition;
		private final long offset;
		
		public ConsumerGroupAssignmentBean(String topic, int partition, long offset)
			{
			this.topic = topic;
			this.partition = partition;
			this.offset = offset;
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
		public int getPartition()
			{
			return partition;
			}
		
		/**
		 * @return the offset
		 */
		public long getOffset()
			{
			return offset;
			}
		
		@Override
		public int compareTo(ConsumerGroupAssignmentBean o)
			{
			final int d = o.topic.compareTo(topic);
			if (d != 0)
				return (d);
			
			final int d2 = partition - o.partition;
			if (d2 != 0)
				return (d2);
			
			return ((int) (offset - o.offset));
			}
		}
	
	private final KafkaApiService kafkaClientService;
	private final QuerySettingsManager querySettingsManager;
	private final TimeService timeService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param kafkaClientService KafkaApiService
	 * @param querySettingsManager QuerySettingsManager
	 * @param timeService TimeService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public KafkaConsumerGroupController(KafkaApiService kafkaClientService,
			QuerySettingsManager querySettingsManager,
			TimeService timeService,
			ConnectionSettings connectionSettings)
		{
		this.kafkaClientService = kafkaClientService;
		this.querySettingsManager = querySettingsManager;
		this.timeService = timeService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the file browser
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/consumergroups.html", method = RequestMethod.GET)
	public Map<String, Object> showConsumerGroups()
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		long start, end;
		
		final Map<String, TabItem<RowSet>> results = new LinkedHashMap<String, TabItem<RowSet>>();
		model.put("results", results);
		
		start = timeService.getCurrentTime();
		final Collection<ConsumerGroupListing> groups = kafkaClientService.getConsumerGroups(connectionSettings.getLinkName());
		end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(KafkaMessageKeys.CONSUMERGROUP_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, groups, end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KafkaMessageKeys.CONSUMERGROUP_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Collection<ConsumerGroupListing> values, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.ID, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.TOPIC, ColumnType.STRING, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, RowSetConstants.INDEX_MULTILEVEL, columns);
		rs.setQueryTime(time);
		
		final Set<String> sortedTopics = new TreeSet<String>();
		for (ConsumerGroupListing group : values)
			sortedTopics.add(group.groupId());
		
		for (String value : sortedTopics)
			rs.getRows().add(new DefaultResultRow(value, value));
		
		rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, false);
		return (rs);
		}
	
	/**
	 * Show the file browser
	 * @param group Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/consumergroup.html", method = RequestMethod.GET)
	public Map<String, Object> showConsumerGroup(
			@RequestParam("group") String group
			)
		{
		return (showConsumerGroupInternal(group));
		}
	
	/**
	 * Show the file browser
	 * @param group Topic name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/consumergroup.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxConsumerGroup(
			@RequestParam("group") String group
			)
		{
		return (showConsumerGroupInternal(group));
		}
	
	private Map<String, Object> showConsumerGroupInternal(String group)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("group", group);
		
		final ConsumerGroupDescription desc = kafkaClientService.getConsumerGroup(connectionSettings.getLinkName(), group);
		model.put("description", new ConsumerGroupBean(desc));
		
		long start, end;
		start = timeService.getCurrentTime();
		final Map<TopicPartition, OffsetAndMetadata> offsets = kafkaClientService.getConsumerGroupOffsets(connectionSettings.getLinkName(), group);
		end = timeService.getCurrentTime();
		
		final List<SubQueryDef> levels = new ArrayList<SubQueryDef>();
		final Query query = new ViewImpl(KafkaMessageKeys.CONSUMERGROUP_LEVEL, null, null, null, null, levels, null);
		
		final RowSet cats = buildRowSet(query, offsets, end - start);
		final RowSet members = buildMembersRowSet(query, desc.members(), end - start);
		
		final Map<String, TabItem<RowSet>> tabs = new LinkedHashMap<String, TabItem<RowSet>>();
		tabs.put(KafkaMessageKeys.ASSIGNMENTS_TAB, new TabItem<RowSet>(cats, cats.getRows().size()));
		tabs.put(KafkaMessageKeys.MEMBERS_TAB, new TabItem<RowSet>(members, members.getRows().size()));
		
		model.put("query", query);
		model.put("tabs", tabs);
		model.put("params", querySettingsManager.buildParameterMap(null));
		model.put("extensionJS", KafkaMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	private RowSet buildRowSet(Query query, Map<TopicPartition, OffsetAndMetadata> offsets, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(KafkaMessageKeys.TOPIC, ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.PARTITION, ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl(KafkaMessageKeys.ID, ColumnType.INTEGER, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, 0, columns);
		rs.setQueryTime(time);
		
		final List<ConsumerGroupAssignmentBean> l = new ArrayList<ConsumerGroupAssignmentBean>(offsets.size());
		for (Map.Entry<TopicPartition, OffsetAndMetadata> ent : offsets.entrySet())
			l.add(new ConsumerGroupAssignmentBean(ent.getKey().topic(), ent.getKey().partition(), ent.getValue().offset()));
		Collections.sort(l);
		
		for (ConsumerGroupAssignmentBean a : l)
			rs.getRows().add(new DefaultResultRow(a.getTopic(), String.valueOf(a.getPartition()), String.valueOf(a.getOffset())));
		
		return (rs);
		}
	
	private RowSet buildMembersRowSet(Query query, Collection<MemberDescription> members, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl("consumerId", ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl("clientId", ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl("host", ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl("assignedPartitions", ColumnType.INTEGER, null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, 1, columns);
		rs.setQueryTime(time);
		
		for (MemberDescription a : members)
			rs.getRows().add(new DefaultResultRow(a.consumerId(), a.clientId(), a.host(), String.valueOf(a.assignment().topicPartitions().size())));
		
		return (rs);
		}
	}
