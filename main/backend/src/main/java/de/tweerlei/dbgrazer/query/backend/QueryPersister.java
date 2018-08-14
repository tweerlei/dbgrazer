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
package de.tweerlei.dbgrazer.query.backend;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.Query;

/**
 * Service for reading/writing Query objects to/from character streams
 * 
 * @author Robert Wruck
 */
public interface QueryPersister
	{
	/**
	 * Load a query from a Reader
	 * @param reader Reader
	 * @param name Query name
	 * @param scope The query scope
	 * @return Query
	 * @throws IOException on error
	 */
	public Query readQuery(Reader reader, String name, SchemaDef scope) throws IOException;
	
	/**
	 * Save a query to a Writer
	 * @param writer Writer
	 * @param query Query
	 * @throws IOException on error
	 */
	public void writeQuery(Writer writer, Query query) throws IOException;
	}