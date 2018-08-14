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
package de.tweerlei.dbgrazer.web.extension.useredit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.extension.ExtensionLink;
import de.tweerlei.dbgrazer.web.extension.FrontendExtensionAdapter;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * JDBC FrontendExtension
 * 
 * @author Robert Wruck
 */
@Service
@Order(4)
public class UserEditExtension extends FrontendExtensionAdapter
	{
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param userSettings UserSettings
	 */
	@Autowired
	public UserEditExtension(UserSettings userSettings)
		{
		super("UserEdit");
		this.userSettings = userSettings;
		}
	
	@Override
	public List<ExtensionLink> getAdminMenuExtensions()
		{
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		if (userSettings.isUserEditorEnabled())
			ret.add(new ExtensionLink("users", "users.html", null, null));
		
		return (ret);
		}
	}
