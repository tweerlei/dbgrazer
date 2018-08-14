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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import de.tweerlei.common.codec.HexCodec;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.plugins.jdbc.ConfigKeys;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;

/**
 * Perform user authentication via JDBC
 * 
 * @author Robert Wruck
 */
@Service("jdbcUserLoader")
public class JdbcUserLoader implements UserAuthenticator, ConfigListener
	{
	private static final String PASSWORD_CHARSET = "UTF-8";
	
	private final ConfigService configService;
	private final DataAccessService dataAccessService;
	private final Logger logger;
	
	private String link;
	private String query;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param dataAccessService DataAccessService
	 */
	@Autowired
	public JdbcUserLoader(ConfigService configService, DataAccessService dataAccessService)
		{
		this.configService = configService;
		this.dataAccessService = dataAccessService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		link = configService.get(ConfigKeys.JDBC_LINK);
		query = configService.get(ConfigKeys.JDBC_QUERY);
		}
	
	@Override
	public boolean authenticate(String username, String password)
		{
		if (link == null)
			{
			logger.log(Level.SEVERE, "No valid link for authentication");
			return (false);
			}
		if (query == null)
			{
			logger.log(Level.SEVERE, "No valid query for authentication");
			return (false);
			}
		
		final JdbcTemplate tmpl = dataAccessService.getJdbcTemplate(link);
		if (tmpl == null)
			{
			logger.log(Level.SEVERE, "Unknown link for authentication: " + link);
			return (false);
			}
		
		final Map<?, ?> result = tmpl.queryForMap(query, new Object[] { username });
		if (result.size() < 2)
			{
			logger.log(Level.SEVERE, "Query returned less than 2 columns");
			return (false);
			}
		
		final Iterator<?> it = result.values().iterator();
		final String algo = String.valueOf(it.next());
		final String hash = String.valueOf(it.next());
		
		try	{
			final String myHash = hash(password, algo);
			
			return (myHash.equalsIgnoreCase(hash));
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "Failed to calculate password hash using " + algo, e);
			return (false);
			}
		}
	
	private String hash(String value, String algo) throws IOException
		{
		final byte[] bytes = value.getBytes(PASSWORD_CHARSET);
		
		try	{
			final MessageDigest md = MessageDigest.getInstance(algo);
			final byte[] hash = md.digest(bytes);
		
			final HexCodec codec = new HexCodec();
			return (codec.encode(hash));
			}
		catch (NoSuchAlgorithmException e)
			{
			throw new IOException(e);
			}
		}
	}
