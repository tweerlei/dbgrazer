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
package de.tweerlei.dbgrazer.web.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.security.model.SessionInfo;
import de.tweerlei.dbgrazer.security.service.SessionManagerService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.service.ManifestEnumeratorService;
import de.tweerlei.spring.util.ManifestParser;
import de.tweerlei.spring.web.service.WebappResourceService;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class StatisticsController
	{
	/** Helper bean */
	public static final class LinkStats
		{
		private final SchemaDef schema;
		private final int sessionCount;
		private final int cacheSize;
		
		/**
		 * Constructor
		 * @param schema SchemaDef
		 * @param sessionCount Session count
		 * @param cacheSize Cache size
		 */
		public LinkStats(SchemaDef schema, int sessionCount, int cacheSize)
			{
			this.schema = schema;
			this.sessionCount = sessionCount;
			this.cacheSize = cacheSize;
			}

		/**
		 * Get the schema
		 * @return the schema
		 */
		public SchemaDef getSchema()
			{
			return schema;
			}

		/**
		 * Get the sessionCount
		 * @return the sessionCount
		 */
		public int getSessionCount()
			{
			return sessionCount;
			}

		/**
		 * Get the cacheSize
		 * @return the cacheSize
		 */
		public int getCacheSize()
			{
			return cacheSize;
			}
		}
	
	private final QueryService queryService;
	private final LinkService linkService;
	private final SessionManagerService sessionManagerService;
	private final ManifestEnumeratorService manifestEnumeratorService;
	private final WebappResourceService webappResourceService;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param linkService LinkService
	 * @param sessionManagerService SessionManagerService
	 * @param manifestEnumeratorService ManifestEnumeratorService
	 * @param webappResourceService WebappResourceService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public StatisticsController(QueryService queryService,
			LinkService linkService, SessionManagerService sessionManagerService,
			ManifestEnumeratorService manifestEnumeratorService, WebappResourceService webappResourceService,
			UserSettings userSettings)
		{
		this.queryService = queryService;
		this.linkService = linkService;
		this.sessionManagerService = sessionManagerService;
		this.manifestEnumeratorService = manifestEnumeratorService;
		this.webappResourceService = webappResourceService;
		this.userSettings = userSettings;
		}
	
	/**
	 * Show statistics
	 * @return Model
	 */
	@RequestMapping(value = "/stats.html", method = RequestMethod.GET)
	public Map<String, Object> showStats()
		{
		if (!userSettings.isReloadEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
//		final Map<String, Integer> cache = metadataService.getLinkStats();
		final Map<String, LinkStats> cstats = new TreeMap<String, LinkStats>();
		for (Map.Entry<String, Integer> ent : linkService.getLinkStats().entrySet())
			{
			final Integer cacheSize = null;	//cache.get(ent.getKey());
			cstats.put(ent.getKey(), new LinkStats(linkService.getSchema(ent.getKey()), ent.getValue(), (cacheSize == null) ? 0 : cacheSize));
			}
		
		final List<SessionInfo> sessions = sessionManagerService.getAllSessions();
		Collections.sort(sessions, new Comparator<SessionInfo>()
			{
			@Override
			public int compare(SessionInfo a, SessionInfo b)
				{
				int d = StringComparators.CASE_INSENSITIVE.compare(a.getUsername(), b.getUsername());
				if (d != 0)
					return (d);
				d = a.getCreationTime().compareTo(b.getCreationTime());
				if (d != 0)
					return (d);
				return (a.getLoginTime().compareTo(b.getLoginTime()));
				}
			});
		
		model.put("qstats", queryService.getQueryStats());
		model.put("cstats", cstats);
		model.put("ustats", sessions);
		model.put("libs", getDependencies());
		
		return (model);
		}
	
	private Map<String, String> getDependencies()
		{
		final Map<String, String> ret = new TreeMap<String, String>();
		
		for (ManifestParser mp : manifestEnumeratorService.getManifests())
			ret.put(mp.getGroupId() + "." + mp.getArtifactId(), mp.getBranch() + "@" + mp.getRevision());
		
		final ManifestParser mp = webappResourceService.getManifestParser();
		ret.put(mp.getGroupId() + "." + mp.getArtifactId(), mp.getBranch() + "@" + mp.getRevision());
		
		return (ret);
		}
	}
