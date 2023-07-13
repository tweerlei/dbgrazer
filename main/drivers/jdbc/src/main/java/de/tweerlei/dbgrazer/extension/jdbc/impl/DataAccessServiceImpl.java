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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.jdbc.ConfigKeys;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.extension.jdbc.DataSourceFactory;
import de.tweerlei.dbgrazer.extension.jdbc.DataSourceWrapper;
import de.tweerlei.dbgrazer.extension.jdbc.support.ReadOnlyTransactionManager;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.config.impl.CompositeConfigProvider;
import de.tweerlei.spring.config.impl.ConfigProviderAccessor;
import de.tweerlei.spring.config.impl.MapBasedConfigProvider;
import de.tweerlei.spring.service.ModuleLookupService;
import de.tweerlei.spring.service.SerializerFactory;
import de.tweerlei.spring.util.OrderedSet;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class DataAccessServiceImpl implements DataAccessService, ConfigListener, LinkListener, LinkManager
	{
	private static final class ConnectionInfo
		{
		public final DataSource dataSource;
		public final JdbcTemplate jdbcTemplate;
		public final JdbcTemplate unlimitedJdbcTemplate;
		public final TransactionTemplate transactionTemplate;
		public final TransactionTemplate readonlyTransactionTemplate;
		public final SQLDialect dialect;
		public final String preDMLStatement;
		public final String postDMLStatement;
		
		public ConnectionInfo(DataSource dataSource,
				JdbcTemplate jdbcTemplate, JdbcTemplate unlimitedJdbcTemplate,
				TransactionTemplate transactionTemplate, TransactionTemplate readonlyTransactionTemplate,
				SQLDialect dialect, String preDMLStatement, String postDMLStatement)
			{
			this.dataSource = dataSource;
			this.jdbcTemplate = jdbcTemplate;
			this.unlimitedJdbcTemplate = unlimitedJdbcTemplate;
			this.transactionTemplate = transactionTemplate;
			this.readonlyTransactionTemplate = readonlyTransactionTemplate;
			this.dialect = dialect;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			}
		}
	
	private final SerializerFactory serializerFactory;
	private final ConfigService configService;
	private final LinkService linkService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	private final Map<String, ConnectionInfo> activeConnections;
	private final Set<DataSourceWrapper> wrappers;
	
	private DataSourceFactory factory;
	
	/**
	 * Constructor
	 * @param serializerFactory SerializerFactory
	 * @param configService ConfigService
	 * @param linkService LinkService
	 * @param moduleService ModuleLookupService
	 * @param wrappers DataSourceWrappers
	 */
	@Autowired
	public DataAccessServiceImpl(SerializerFactory serializerFactory, ConfigService configService,
			LinkService linkService, ModuleLookupService moduleService,
			Set<DataSourceWrapper> wrappers)
		{
		this.serializerFactory = serializerFactory;
		this.configService = configService;
		this.linkService = linkService;
		this.moduleService = moduleService;
		this.wrappers = Collections.unmodifiableSet(new OrderedSet<DataSourceWrapper>(wrappers));
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.activeConnections = new ConcurrentHashMap<String, ConnectionInfo>();
		
		this.logger.log(Level.INFO, "DataSource wrappers: " + this.wrappers);
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		linkService.addListener(this);
		linkService.addManager(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		final String factoryPrefix = configService.get(ConfigKeys.DATASOURCE_FACTORY);
		
		logger.log(Level.INFO, "Using DataSourceFactory: " + factoryPrefix);
		try	{
			factory = moduleService.findModuleInstance(factoryPrefix + "DataSourceFactory", DataSourceFactory.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			factory = new DummyDataSourceFactory();
			}
		
		closeConnections();
		}
	
	@Override
	public void linksChanged()
		{
		closeConnections();
		}
	
	@Override
	public void linkChanged(String link)
		{
		closeConnection(link);
		}
	
	/**
	 * Close all connections
	 */
	@PreDestroy
	public synchronized void closeConnections()
		{
		logger.log(Level.INFO, "Closing " + activeConnections.size() + " DataSources");
		
		for (ConnectionInfo c : activeConnections.values())
			factory.cleanup(c.dataSource);
		
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final ConnectionInfo info = activeConnections.remove(link);
		if (info != null)
			{
			logger.log(Level.INFO, "Closing connection " + link);
			factory.cleanup(info.dataSource);
			}
		}
	
	@Override
	public SQLDialect getSQLDialect(String c)
		{
		return (getConnection(c).dialect);
		}
	
	@Override
	public JdbcTemplate getJdbcTemplate(String c)
		{
		return (getConnection(c).jdbcTemplate);
		}
	
	@Override
	public JdbcTemplate getUnlimitedJdbcTemplate(String c)
		{
		return (getConnection(c).unlimitedJdbcTemplate);
		}
	
	@Override
	public TransactionTemplate getTransactionTemplate(String c)
		{
		return (getConnection(c).transactionTemplate);
		}
	
	@Override
	public TransactionTemplate getTestTransactionTemplate(String c)
		{
		return (getConnection(c).readonlyTransactionTemplate);
		}
	
	@Override
	public String getPreDMLStatement(String c)
		{
		return (getConnection(c).preDMLStatement);
		}
	
	@Override
	public String getPostDMLStatement(String c)
		{
		return (getConnection(c).postDMLStatement);
		}
	
	private ConnectionInfo getConnection(String c)
		{
		final ConnectionInfo ret = activeConnections.get(c);
		if (ret != null)
			return (ret);
		
		return (createConnection(c));
		}
	
	private synchronized ConnectionInfo createConnection(String c)
		{
		ConnectionInfo ret = activeConnections.get(c);
		if (ret != null)
			return (ret);
		
		final LinkDef def = linkService.getLinkData(c);
		if ((def == null) /*|| !(def.getType() instanceof JdbcLinkType)*/)
			throw new DataSourceLookupFailureException(c);
		
		final ConfigProviderAccessor accessor = new ConfigProviderAccessor(new CompositeConfigProvider(
				new MapBasedConfigProvider(def.getProperties()),
				configService.getConfigProvider()
				), serializerFactory);
		
		DataSource ds = factory.createDataSource(def, accessor);
		for (DataSourceWrapper wrapper : wrappers)
			ds = wrapper.wrapDataSource(ds, def, accessor);
		
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		jdbcTemplate.setMaxRows(accessor.get(ConfigKeys.MAX_ROWS));
		jdbcTemplate.setFetchSize(accessor.get(ConfigKeys.FETCH_SIZE));
		jdbcTemplate.setQueryTimeout(accessor.get(ConfigKeys.QUERY_TIMEOUT));
		jdbcTemplate.afterPropertiesSet();
		
		final JdbcTemplate unlimited = new JdbcTemplate(ds);
		unlimited.setMaxRows(0);	// unlimited
		unlimited.setFetchSize(accessor.get(ConfigKeys.FETCH_SIZE));
		unlimited.setQueryTimeout(0);	// unlimited
		unlimited.afterPropertiesSet();
		
		final DataSourceTransactionManager mgr;
		if (def.isWritable())
			mgr = new DataSourceTransactionManager(ds);
		else
			mgr = new ReadOnlyTransactionManager(ds);
		mgr.afterPropertiesSet();
		
		final TransactionTemplate transactionTemplate = new TransactionTemplate(mgr);
		transactionTemplate.setReadOnly(!def.isWritable());
		transactionTemplate.afterPropertiesSet();
		
		final DataSourceTransactionManager romgr = new ReadOnlyTransactionManager(ds);
		romgr.afterPropertiesSet();
		
		final TransactionTemplate readonlyTransactionTemplate = new TransactionTemplate(romgr);
		readonlyTransactionTemplate.setReadOnly(!def.isWritable());
		readonlyTransactionTemplate.afterPropertiesSet();
		
		final SQLDialect dialect = SQLDialectFactory.getSQLDialect(def.getDialectName());
		
		ret = new ConnectionInfo(ds, jdbcTemplate, unlimited, transactionTemplate, readonlyTransactionTemplate,
				dialect, def.getPreDMLStatement(), def.getPostDMLStatement());
		
		activeConnections.put(c, ret);
		return (ret);
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, ConnectionInfo> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), factory.getConnectionCount(ent.getValue().dataSource));
		
		return (ret);
		}
	}
