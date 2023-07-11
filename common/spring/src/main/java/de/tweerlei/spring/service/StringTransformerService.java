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
package de.tweerlei.spring.service;

/**
 * Perform String transformations (mainly Unicode related)
 * 
 * @author Robert Wruck
 */
public interface StringTransformerService
	{
	/**
	 * Normalize a String to NFC
	 * @param s String
	 * @return Normalized String
	 */
	public String normalize(String s);
	
	/**
	 * Get an ASCII representation for a String by first performing a compatibility decomposition and then stripping all non-ascii characters.
	 * Thus, "f<u-umlaut>r" becomes "fur"
	 * @param s String
	 * @return ASCII String
	 */
	public String toASCII(String s);
	
	/**
	 * Get a URL-encoded representation for a String, using UTF-8 encoding
	 * @param s String
	 * @return URL-encoded String
	 */
	public String toURL(String s);
	
	/**
	 * Get a URL-decoded representation for a String, using UTF-8 encoding
	 * @param s String
	 * @return URL-decoded String
	 */
	public String fromURL(String s);
	}
