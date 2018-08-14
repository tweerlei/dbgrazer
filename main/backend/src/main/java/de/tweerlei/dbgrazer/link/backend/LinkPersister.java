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
package de.tweerlei.dbgrazer.link.backend;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import de.tweerlei.dbgrazer.link.model.LinkDef;

/**
 * Load LinkDef objects from character streams
 * 
 * @author Robert Wruck
 */
public interface LinkPersister
	{
	/**
	 * Load a LinkDef object from a Reader
	 * @param reader Reader
	 * @param name Link name
	 * @return LinkDef
	 * @throws IOException on error
	 */
	public LinkDef readLink(Reader reader, String name) throws IOException;
	
	/**
	 * Write a LinkDef object
	 * @param writer Writer
	 * @param c LinkDef
	 * @throws IOException on error
	 */
	public void writeLink(Writer writer, LinkDef c) throws IOException;
	}
