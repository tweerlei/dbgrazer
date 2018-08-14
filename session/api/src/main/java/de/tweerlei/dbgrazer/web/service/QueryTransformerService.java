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
package de.tweerlei.dbgrazer.web.service;

import java.util.Set;
import java.util.SortedMap;

import de.tweerlei.common5.func.predicate.Predicate;
import de.tweerlei.common5.func.unary.UnaryFunction;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.web.model.Visualization;

/**
 * Transform query meta data
 * 
 * @author Robert Wruck
 */
public interface QueryTransformerService
	{
	/**
	 * Filter a QueryGroup to retain only queries that match a predicate
	 * @param group QueryGroup
	 * @param pred Predicate
	 */
	public void filterQueryGroup(QueryGroup group, Predicate<Query> pred);
	
	/**
	 * Split a QueryGroup into subgroups
	 * @param group QueryGroup
	 * @param func Function to determine the subgroup key (null means no subgroup)
	 * @return Map: Key -> Subgroup
	 */
	public SortedMap<String, QueryGroup> splitQueryGroup(QueryGroup group, UnaryFunction<Query, String> func);
	
	/**
	 * Split a QueryGroup into subgroups where each query may appear in more than one subgroup
	 * @param group QueryGroup
	 * @param func Function to determine the subgroup keys (null or empty set means no subgroup)
	 * @return Map: Key -> Subgroup
	 */
	public SortedMap<String, QueryGroup> splitQueryGroupMulti(QueryGroup group, UnaryFunction<Query, Set<String>> func);
	
	/**
	 * Build a graph from a query and its references
	 * @param link Link name
	 * @param query Query name
	 * @param name Graph name
	 * @param nodeLink Node link
	 * @return GraphDefinition
	 */
	public Visualization buildGraph(String link, String query, String name, String nodeLink);
	}
