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

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.types.QueryTypeAttributes;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;

/**
 * Enrich RowSets of aggregate queries with the possible AggregationModes
 * 
 * @author Robert Wruck
 */
@Service
public class AggregateResultEnricher extends ResultVisitorAdapter
	{
	@Override
	public boolean startRowSet(RowSet rs)
		{
		rs.getAttributes().put(QueryTypeAttributes.ATTR_FUNCS, SQLGeneratorService.AggregationMode.values());
		
		return false;
		}
	}
