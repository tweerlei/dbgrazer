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
package de.tweerlei.dbgrazer.web.controller.kubernetes;

/**
 * Value object for the resource kind triplet
 * 
 * @author Robert Wruck
 */
public class ObjectKind
	{
	/** API group name */
	public final String group;
	/** API version */
	public final String version;
	/** Resource kind */
	public final String kind;
	
	/**
	 * Constructor
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 */
	public ObjectKind(String group, String version, String kind)
		{
		this.group = group;
		this.version = version;
		this.kind = kind;
		}
	
	@Override
	public String toString()
		{
		return (group + "/" + version + "/" + kind);
		}
	
	/**
	 * Parse a String representation
	 * @param kind String representation
	 * @return ObjectKind
	 */
	public static ObjectKind parse(String kind)
		{
		final String[] parts = kind.split("/", 3);
		return (new ObjectKind(parts[0], parts[1], parts[2]));
		}
	}
