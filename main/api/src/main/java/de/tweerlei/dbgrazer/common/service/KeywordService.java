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
package de.tweerlei.dbgrazer.common.service;

import java.util.List;

/**
 * Service for normalizing keywords, such as query, group or parameter names
 * 
 * @author Robert Wruck
 */
public interface KeywordService
	{
	/**
	 * Normalize a name for an entity
	 * @param name Name
	 * @return Normalized name
	 */
	public String normalizeName(String name);
	
	/**
	 * Normalize a parameter name
	 * @param name Parameter name
	 * @return Normalized name
	 */
	public String normalizeParam(String name);
	
	/**
	 * Normalize a parameter value
	 * @param name Parameter value
	 * @return Normalized value
	 */
	public String normalizeValue(String name);
	
	/**
	 * Normalize a group name
	 * @param name Group name
	 * @return Normalized name
	 */
	public String normalizeGroup(String name);
	
	/**
	 * Normalize a word
	 * @param word Word
	 * @return Normalized word
	 */
	public String normalizeWord(String word);
	
	/**
	 * Normalize a path
	 * @param path Path
	 * @return Normalized path
	 */
	public String normalizePath(String path);
	
	/**
	 * Split a comma separated list into tokens.
	 * Empty tokens will be discarded.
	 * @param value List
	 * @return Tokens
	 */
	public List<String> extractValues(String value);
	
	/**
	 * Combine values into a comma separated list
	 * @param values Values
	 * @return List
	 */
	public String combineValues(Iterable<String> values);
	
	/**
	 * Split a space separated list into tokens
	 * @param value List
	 * @return Tokens
	 */
	public List<String> extractWords(String value);
	
	/**
	 * Combine values into a space separated list
	 * @param values Values
	 * @return List
	 */
	public String combineWords(Iterable<String> values);
	}
