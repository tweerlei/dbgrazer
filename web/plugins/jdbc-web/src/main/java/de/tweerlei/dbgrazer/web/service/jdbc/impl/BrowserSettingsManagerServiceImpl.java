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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.model.UserObjectKey;
import de.tweerlei.dbgrazer.web.service.jdbc.BrowserSettingsManagerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Manage saved table designs
 * 
 * @author Robert Wruck
 */
@Service
public class BrowserSettingsManagerServiceImpl implements BrowserSettingsManagerService
	{
	private static final UserObjectKey<BrowserSettings> KEY_SETTINGS = UserObjectKey.create(BrowserSettings.class, true);
	
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public BrowserSettingsManagerServiceImpl(ConnectionSettings connectionSettings)
		{
		this.connectionSettings = connectionSettings;
		}
	
	@Override
	public String getCatalog()
		{
		return (getBrowserSettings().getCatalog());
		}

	@Override
	public void setCatalog(String catalog)
		{
		getBrowserSettings().setCatalog(catalog);
		}

	@Override
	public String getSchema()
		{
		return (getBrowserSettings().getSchema());
		}

	@Override
	public void setSchema(String schema)
		{
		getBrowserSettings().setSchema(schema);
		}
	
	@Override
	public boolean isDesignerPreviewMode()
		{
		return (getBrowserSettings().isDesignerPreviewMode());
		}

	@Override
	public void setDesignerPreviewMode(boolean b)
		{
		getBrowserSettings().setDesignerPreviewMode(b);
		}

	@Override
	public boolean isDesignerCompactMode()
		{
		return (getBrowserSettings().isDesignerCompactMode());
		}

	@Override
	public void setDesignerCompactMode(boolean b)
		{
		getBrowserSettings().setDesignerCompactMode(b);
		}

	@Override
	public boolean isExpandOtherSchemas()
		{
		return (getBrowserSettings().isExpandOtherSchemas());
		}

	@Override
	public void setExpandOtherSchemas(boolean b)
		{
		getBrowserSettings().setExpandOtherSchemas(b);
		}
	
	@Override
	public boolean isSortColumns()
		{
		return (getBrowserSettings().isSortColumns());
		}

	@Override
	public void setSortColumns(boolean b)
		{
		getBrowserSettings().setSortColumns(b);
		}
	
	@Override
	public Map<String, TableFilterEntry> getTableFilters()
		{
		return (getBrowserSettings().getTableFilters());
		}
	
	private BrowserSettings getBrowserSettings()
		{
		BrowserSettings ret = connectionSettings.getUserObject(KEY_SETTINGS);
		
		if (ret == null)
			{
			ret = new BrowserSettings();
			connectionSettings.setUserObject(KEY_SETTINGS, ret);
			}
		
		return (ret);
		}
	}
