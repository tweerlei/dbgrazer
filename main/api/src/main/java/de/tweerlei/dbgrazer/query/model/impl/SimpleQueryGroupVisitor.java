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
package de.tweerlei.dbgrazer.query.model.impl;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroupVisitor;

/**
 * A simplified visitor for QueryGroups
 * 
 * @author Robert Wruck
 */
public abstract class SimpleQueryGroupVisitor implements QueryGroupVisitor
	{
	@Override
	public final boolean visitList(Query q)
		{
		return (visitQuery(q));
		}
	
	@Override
	public final boolean visitView(Query q)
		{
		return (visitQuery(q));
		}
	
	@Override
	public final boolean visitListView(Query q)
		{
		return (visitQuery(q));
		}
	
	@Override
	public final boolean visitSubquery(Query q)
		{
		return (visitQuery(q));
		}
	
	@Override
	public final boolean visitAction(Query q)
		{
		return (visitQuery(q));
		}
	}
