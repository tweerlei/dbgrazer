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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.StatementCollection;
import de.tweerlei.dbgrazer.web.backend.StatementProducerCreator;
import de.tweerlei.dbgrazer.web.service.StatementProducerService;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class StatementProducerServiceImpl implements StatementProducerService
	{
	private final Logger logger;
	private final Map<String, StatementProducerCreator> formats;
	
	/**
	 * Constructor
	 * @param formats StatementProducerCreators
	 */
	@Autowired(required = false)
	public StatementProducerServiceImpl(Set<StatementProducerCreator> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<StatementProducerCreator>(formats));
		
		this.logger.log(Level.INFO, "StatementProducer creators: " + this.formats);
		}
	
	/**
	 * Constructor used when no StatementProducerCreator instances are available
	 */
	public StatementProducerServiceImpl()
		{
		this(Collections.<StatementProducerCreator>emptySet());
		}
	
	@Override
	public Set<String> getSupportedFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public StatementProducer getStatementProducer(RowProducer p, TableDescription info, SQLDialect dialect, String format)
		{
		final StatementProducerCreator c = getStatementProducerCreator(format);
		if (c != null)
			return (c.getStatementProducer(p, info, dialect));
		
		return (new StatementCollection(null, null));
		}
	
	private StatementProducerCreator getStatementProducerCreator(String format)
		{
		return (formats.get(format));
		}
	}
