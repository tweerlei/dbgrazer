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
package de.tweerlei.dbgrazer.web.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception that carries model attributes while the view name is determined by the concrete exception type.
 * Therefore, this base class must be abstract.
 * 
 * @author Robert Wruck
 */
public abstract class ModelDefiningException extends RuntimeException
	{
	private final Map<String, Object> model;
	
	/**
	 * Constructor
	 * @param queryName Failed (sub-)query name
	 * @param outerQuery Toplevel query name
	 * @param cause Causing exception
	 */
	public ModelDefiningException(String queryName, String outerQuery, Throwable cause)
		{
		super(cause);
		model = buildModel(queryName, outerQuery, cause);
		}
	
	/**
	 * Constructor
	 * @param e Exception to copy the model from
	 */
	public ModelDefiningException(ModelDefiningException e)
		{
		super(e.getCause());
		model = e.getModel();
		}
	
	/**
	 * Get the model
	 * @return Model
	 */
	public Map<String, Object> getModel()
		{
		return (model);
		}
	
	private static Map<String, Object> buildModel(String queryName, String outerQuery, Throwable cause)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("exception", cause);
		model.put("backTo", outerQuery);
		model.put("query", queryName);
		return (model);
		}
	}
