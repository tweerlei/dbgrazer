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
package de.tweerlei.ermtools.dialect.impl;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
public class DummySQLObjectDDLWriter extends AbstractSQLObjectDDLWriter
	{
	public String findObjects(String catalog, String schema, String name, String type)
		{
		return null;
		}
	
	public String findObjectSource(String catalog, String schema, String name, String type)
		{
		return null;
		}
	
	public String findObjectPrivileges(String catalog, String schema, String name, String type)
		{
		return null;
		}
	
	public String createObject(String catalog, String schema, String name, String type, String source)
		{
		return null;
		}
	
	public String dropObject(String catalog, String schema, String name, String type)
		{
		return null;
		}
	
	public String replaceObject(String catalog, String schema, String name, String type, String source)
		{
		return null;
		}
	
	public boolean canReplaceObject(String catalog, String schema, String name, String type)
		{
		return false;
		}
	
	public String grantObjectPrivilege(String catalog, String schema, String name, String type, String grantee, String privilege, boolean grantable)
		{
		return null;
		}
	
	public String revokeObjectPrivilege(String catalog, String schema, String name, String type, String grantee, String privilege)
		{
		return null;
		}
	}
