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
package de.tweerlei.dbgrazer.common.file;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for persisting custom objects
 * @param <T> Object type
 * 
 * @author Robert Wruck
 */
public interface ObjectPersister<T>
	{
	/**
	 * Read an object from a Reader
	 * @param r Reader
	 * @return Object
	 * @throws IOException on error
	 */
	public T readObject(Reader r) throws IOException;
	
	/**
	 * Write an object to a Writer
	 * @param w Writer
	 * @param object Object
	 * @throws IOException on error
	 */
	public void writeObject(Writer w, T object) throws IOException;
	}
