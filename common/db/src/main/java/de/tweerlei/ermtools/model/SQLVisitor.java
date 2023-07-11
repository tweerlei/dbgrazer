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
package de.tweerlei.ermtools.model;

import de.tweerlei.common5.jdbc.model.TableVisitor;

/**
 * Besucher für SQLSchemata
 * 
 * @author Robert Wruck
 */
public interface SQLVisitor extends TableVisitor
	{
	/**
	 * Aufgerufen bei Beginn des Schemas
	 * @param schema SQLSchema
	 */
	public void beginSchema(SQLSchema schema);
	
	/**
	 * Aufgerufen bei Ende des Schemas
	 * @param schema SQLSchema
	 */
	public void endSchema(SQLSchema schema);
	}
