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
package de.tweerlei.dbgrazer.plugins.kafka.types;

/**
 * QueryType attribute names
 * 
 * @author Robert Wruck
 */
public final class QueryTypeAttributes
	{
	/** RowSet attribute for the topic name */
	public static final String ATTR_TOPIC = "topic";
	/** RowSet attribute for the TextFormatter name */
	public static final String ATTR_FORMATTER = "formatter";
	
	private QueryTypeAttributes()
		{
		}
	}
