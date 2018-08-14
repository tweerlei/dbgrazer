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
package de.tweerlei.common.codec;

import java.io.IOException;

/**
 * Codec that converts String <-> byte[]
 * 
 * @author Robert Wruck
 */
public interface StringCodec
	{
	/**
	 * Encode binary data
	 * @param data Data
	 * @return Encoded String
	 * @throws IOException on error
	 */
	public String encode(byte[] data) throws IOException;
	
	/**
	 * Encode binary data
	 * @param data Data
	 * @param offset Start offset
	 * @param length Data length
	 * @return Encoded String
	 * @throws IOException on error
	 */
	public String encode(byte[] data, int offset, int length) throws IOException;
	
	/**
	 * Decode a string
	 * @param code Code
	 * @return Data
	 * @throws IOException on error
	 */
	public byte[] decode(String code) throws IOException;
	}
