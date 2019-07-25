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
package de.tweerlei.dbgrazer.query.backend.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.ResultOrientation;
import de.tweerlei.dbgrazer.query.model.impl.AbstractRecursiveQueryType;

/**
 * Results returned by subqueries, single page display
 * 
 * @author Robert Wruck
 */
@Service
@Order(1001)
public class PanelsQueryType extends AbstractRecursiveQueryType
	{
	/** The NAME */
	public static final String NAME = "PANELS";
	
	/**
	 * Constructor
	 */
	public PanelsQueryType()
		{
		super(NAME, null);
		}
	
	@Override
	public ResultOrientation getOrientation()
		{
		return ResultOrientation.DOWN;
		}
	}
