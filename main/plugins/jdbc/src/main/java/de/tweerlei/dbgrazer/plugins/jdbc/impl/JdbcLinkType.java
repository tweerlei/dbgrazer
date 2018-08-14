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

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.tweerlei.common.util.ClassUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkErrorKeys;
import de.tweerlei.dbgrazer.link.model.impl.BaseLinkType;

/**
 * JDBC impl.
 * 
 * @author Robert Wruck
 */
@Service
@Order(1)
public class JdbcLinkType extends BaseLinkType
	{
	private static final String NAME = JdbcConstants.LINKTYPE_JDBC;
	
	/**
	 * Constructor
	 */
	public JdbcLinkType()
		{
		super(NAME);
		}
	
	@Override
	public boolean isCustomQuerySupported()
		{
		return (true);
		}
	
	@Override
	public void validate(LinkDef conn, Errors errors)
		{
		if (StringUtils.empty(conn.getUrl()))
			errors.rejectValue("url", LinkErrorKeys.EMPTY_URL);
		
		if (StringUtils.empty(conn.getSchema().getName()))
			errors.rejectValue("schema", LinkErrorKeys.EMPTY_SCHEMA);
		
//		if (StringUtils.empty(conn.getUsername()))
//			errors.rejectValue("username", LinkErrorKeys.EMPTY_USERNAME);
		
		if (StringUtils.empty(conn.getDriver()))
			errors.rejectValue("driver", LinkErrorKeys.EMPTY_DRIVER);
		
		if (!ClassUtils.exists(conn.getDriver()))
			errors.rejectValue("driver", LinkErrorKeys.DRIVER_CLASS_NOT_FOUND);
		}
	}
