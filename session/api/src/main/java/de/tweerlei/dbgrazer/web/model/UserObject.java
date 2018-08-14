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
package de.tweerlei.dbgrazer.web.model;

import java.io.Serializable;

/**
 * A user-defined object
 * 
 * @author Robert Wruck
 */
public abstract class UserObject implements Serializable
	{
	private String name;
	private boolean modified;
	
	/**
	 * Constructor
	 */
	protected UserObject()
		{
		reset();
		}
	
	/**
	 * Get the name
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the modified
	 * @return the modified
	 */
	public boolean isModified()
		{
		return modified;
		}
	
	/**
	 * Reset
	 */
	public void reset()
		{
		this.name = null;
		this.modified = true;
		}
	
	/**
	 * Persist
	 * @param n the persisted name
	 */
	public void persist(String n)
		{
		this.name = n;
		this.modified = false;
		}
	
	/**
	 * Modify
	 */
	public void modify()
		{
		this.modified = true;
		}
	}
