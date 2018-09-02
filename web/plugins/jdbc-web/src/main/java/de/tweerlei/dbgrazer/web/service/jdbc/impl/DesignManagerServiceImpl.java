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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.io.IOException;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.web.model.UserObjectKey;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignPersister;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Manage saved table designs
 * 
 * @author Robert Wruck
 */
@Service
public class DesignManagerServiceImpl implements DesignManagerService
	{
	private static final String DESIGN_EXTENSION = "designs";
	private static final UserObjectKey<TableSet> KEY_DESIGN = UserObjectKey.create(TableSet.class, true);
	
	private final UserManagerService userManagerService;
	private final DesignPersister designPersister;
	private final ConnectionSettings connectionSettings;
	private final UserSettings userSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param userManagerService UserManagerService
	 * @param designPersister DesignPersister
	 * @param connectionSettings ConnectionSettings
	 * @param userSettings UserSettings
	 */
	@Autowired
	public DesignManagerServiceImpl(UserManagerService userManagerService, DesignPersister designPersister,
			ConnectionSettings connectionSettings, UserSettings userSettings)
		{
		this.userManagerService = userManagerService;
		this.designPersister = designPersister;
		this.connectionSettings = connectionSettings;
		this.userSettings = userSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public TableSet getCurrentDesign()
		{
		TableSet ret = connectionSettings.getUserObject(KEY_DESIGN);
		
		if (ret == null)
			{
			ret = new TableSet();
			connectionSettings.setUserObject(KEY_DESIGN, ret);
			}
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> listAvailableDesigns()
		{
		return (userManagerService.listExtensionObjects(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION));
		}
	
	@Override
	public void loadDesign(String name)
		{
		try	{
			final SortedSet<QualifiedName> tables = userManagerService.loadExtensionObject(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name, designPersister);
			
			final TableSet design = new TableSet(tables);
			design.persist(name);
			
			connectionSettings.setUserObject(KEY_DESIGN, design);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "loadDesign", e);
			}
		}
	
	@Override
	public void saveDesign(String name)
		{
		final TableSet design = getCurrentDesign();
		
		if (design.getTableNames().isEmpty())
			{
			try	{
				final String dn = userManagerService.saveExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name, design.getTableNames(), designPersister);
				design.persist(dn);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "saveDesign", e);
				}
			}
		}
	
	@Override
	public void removeDesign(String name)
		{
		try	{
			userManagerService.removeExtensionObject(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName(), DESIGN_EXTENSION, name);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "removeDesign", e);
			}
		}
	}
