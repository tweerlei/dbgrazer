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
package de.tweerlei.dbgrazer.plugins.http.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.http.HttpConstants;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkErrorKeys;
import de.tweerlei.dbgrazer.link.model.impl.BaseLinkType;

/**
 * Web service impl.
 * 
 * @author Robert Wruck
 */
@Service
@Order(2)
public class WebserviceLinkType extends BaseLinkType
	{
	/** The name */
	public static final String NAME = HttpConstants.LINKTYPE_WEBSERVICE;
	
	/**
	 * Constructor
	 */
	public WebserviceLinkType()
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
		}
	}
