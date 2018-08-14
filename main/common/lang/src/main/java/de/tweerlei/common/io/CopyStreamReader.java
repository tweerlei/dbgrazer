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
package de.tweerlei.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copies data from an InputStream to an OutputStream
 * 
 * @author Robert Wruck
 */
public class CopyStreamReader implements StreamReader
	{
	private final OutputStream out;
	
	/**
	 * Constructor
	 * @param out Destination stream
	 */
	public CopyStreamReader(OutputStream out)
		{
		this.out = out;
		}
	
	public void read(InputStream stream) throws IOException
		{
		StreamUtils.copy(stream, out);
		}
	}
