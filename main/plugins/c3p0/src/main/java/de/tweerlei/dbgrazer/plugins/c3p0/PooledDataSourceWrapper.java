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
package de.tweerlei.dbgrazer.plugins.c3p0;

import java.beans.PropertyVetoException;
import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import com.mchange.v2.c3p0.ConnectionCustomizer;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.jdbc.DataSourceWrapper;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * DataSourceFactory that uses C3P0 connection pools
 * 
 * @author Robert Wruck
 */
@Service
@Order(1000)
public class PooledDataSourceWrapper implements DataSourceWrapper
	{
	/**
	 * Customizer for connections created by C3P0
	 */
	public static final class ReadOnlyConnectionPreparer implements ConnectionCustomizer
		{
		@Override
		public void onAcquire(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			c.setReadOnly(true);
			}
		
		@Override
		public void onCheckOut(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			// C3P0 does setAutoCommit(true) on every close(), so setting it to false in onAcquire is not enough
			c.setAutoCommit(false);
			}
		
		@Override
		public void onCheckIn(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			// Since we did not set autoCommitOnClose and autoCommit is false, C3P0 will perform a rollback on check-in
			}
		
		@Override
		public void onDestroy(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			}
		}
	
	/**
	 * Customizer for connections created by C3P0
	 */
	public static final class WritableConnectionPreparer implements ConnectionCustomizer
		{
		@Override
		public void onAcquire(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
//			c.setReadOnly(false);
			}
		
		@Override
		public void onCheckOut(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			// C3P0 does setAutoCommit(true) on every close(), so setting it to false in onAcquire is not enough
			c.setAutoCommit(false);
			}
		
		@Override
		public void onCheckIn(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			// Since we did not set autoCommitOnClose and autoCommit is false, C3P0 will perform a rollback on check-in
			}
		
		@Override
		public void onDestroy(Connection c, String parentDataSourceIdentityToken) throws Exception
			{
			}
		}
	
	@Override
	public DataSource wrapDataSource(DataSource ds, LinkDef c, ConfigAccessor config)
		{
		// The preferredTestQuery is required, since c3p0's default test method is VERY slow
		final String preferredTestQuery = config.get(ConfigKeys.PREFERRED_TEST_QUERY);
		if (StringUtils.empty(preferredTestQuery))
			return (ds);
		
		final WrapperConnectionPoolDataSource ret = new WrapperConnectionPoolDataSource();
		ret.setNestedDataSource(ds);
		
		if (c.isWritable())
			ret.setConnectionCustomizerClassName(WritableConnectionPreparer.class.getName());
		else
			ret.setConnectionCustomizerClassName(ReadOnlyConnectionPreparer.class.getName());
		
		// Sadly, ComboPooledDataSource does not scan the passed Properties for the ones recognized in c3p0.properties.
		// So we do that for the properties we consider useful...
		
		ret.setInitialPoolSize(config.get(ConfigKeys.INITIAL_POOL_SIZE));
		ret.setMinPoolSize(config.get(ConfigKeys.MIN_POOL_SIZE));
		ret.setMaxPoolSize(config.get(ConfigKeys.MAX_POOL_SIZE));
		ret.setAcquireIncrement(config.get(ConfigKeys.ACQUIRE_INCREMENT));
		ret.setAcquireRetryAttempts(config.get(ConfigKeys.ACQUIRE_RETRY_ATTEMPTS));
		ret.setAcquireRetryDelay(config.get(ConfigKeys.ACQUIRE_RETRY_DELAY));
		ret.setCheckoutTimeout(config.get(ConfigKeys.CHECKOUT_TIMEOUT));
		ret.setMaxIdleTime(config.get(ConfigKeys.MAX_IDLE_TIME));
		ret.setMaxIdleTimeExcessConnections(config.get(ConfigKeys.MAX_IDLE_TIME_EXCESS_CONNECTIONS));
		ret.setMaxConnectionAge(config.get(ConfigKeys.MAX_CONNECTION_AGE));
		ret.setMaxStatements(config.get(ConfigKeys.MAX_STATEMENTS));
		ret.setMaxStatementsPerConnection(config.get(ConfigKeys.MAX_STATEMENTS_PER_CONNECTION));
		ret.setIdleConnectionTestPeriod(config.get(ConfigKeys.IDLE_CONNECTION_TEST_PERIOD));
		ret.setPreferredTestQuery(preferredTestQuery);
		
		try	{
			final PoolBackedDataSource pbds = new PoolBackedDataSource();
			pbds.setConnectionPoolDataSource(ret);
			
			return (pbds);
			}
		catch (PropertyVetoException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
