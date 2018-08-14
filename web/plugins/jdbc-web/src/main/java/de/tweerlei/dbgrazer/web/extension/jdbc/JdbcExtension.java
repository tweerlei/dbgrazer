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
package de.tweerlei.dbgrazer.web.extension.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
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
@Order(1)
public class JdbcExtension extends FrontendExtensionAdapter
	{
	private final FrontendHelperService frontendHelper;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param frontendHelper FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public JdbcExtension(FrontendHelperService frontendHelper, ConnectionSettings connectionSettings)
		{
		super("JDBC");
		this.frontendHelper = frontendHelper;
		this.connectionSettings = connectionSettings;
		}
	
	@Override
	public List<ExtensionLink> getEditMenuExtensions()
		{
		if (!connectionSettings.getType().getName().equals(JdbcConstants.LINKTYPE_JDBC))
			return (Collections.emptyList());
		
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		if (connectionSettings.isBrowserEnabled())
			{
			if ((connectionSettings.getCatalog() != null) && (connectionSettings.getSchema() != null))
				ret.add(new ExtensionLink("schemaBrowser", frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "dbobjects.html", "catalog=" + connectionSettings.getCatalog() + "&schema=" + connectionSettings.getSchema()), null, null));
			else
				ret.add(new ExtensionLink("schemaBrowser", frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "dbcatalogs.html", null), null, null));
			}
		if (connectionSettings.isDesignerEnabled())
			{
			ret.add(new ExtensionLink("designer", frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "dbdesigner.html", null), null, null));
			}
		if (connectionSettings.isBrowserEnabled() || connectionSettings.isDesignerEnabled())
			{
			ret.add(new ExtensionLink("clearCache", null, "return clearDbCache();", null));
			}
		if (connectionSettings.isSubmitEnabled() && connectionSettings.isWritable())
			{
			ret.add(new ExtensionLink("scriptQuery", frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "submitexec.html", null), null, null));
			}
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getRestApiExtensions()
		{
		if (!connectionSettings.getType().getName().equals(JdbcConstants.LINKTYPE_JDBC))
			return (Collections.emptyList());
		
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		ret.add(new ExtensionLink("downloadData", frontendHelper.buildPath(MessageKeys.PATH_WS, connectionSettings.getLinkName(), "form-export.html", null), null, null));
		ret.add(new ExtensionLink("fullCompare", frontendHelper.buildPath(MessageKeys.PATH_WS, connectionSettings.getLinkName(), "form-dml.html", null), null, null));
		ret.add(new ExtensionLink("structureCompare", frontendHelper.buildPath(MessageKeys.PATH_WS, connectionSettings.getLinkName(), "form-dbcompare.html", null), null, null));
		ret.add(new ExtensionLink("ddlCompare", frontendHelper.buildPath(MessageKeys.PATH_WS, connectionSettings.getLinkName(), "form-srccompare.html", null), null, null));
		
		return (ret);
		}
	}
