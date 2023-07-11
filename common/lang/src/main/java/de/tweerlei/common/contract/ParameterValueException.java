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
package de.tweerlei.common.contract;

/**
 * Thrown when a parameter's contents violate the contract
 * 
 * @author Robert Wruck
 */
public class ParameterValueException extends ContractException
	{
	private final String param;
	
	/**
	 * Constructor
	 * @param parameter Parameter name
	 * @param message Message describing the contract violation
	 */
	public ParameterValueException(String parameter, String message)
		{
		super("Parameter '" + parameter + "': " + message);
		param = parameter;
		}
	
	/**
	 * Get the parameter name
	 * @return parameter name
	 */
	public final String getParameterName()
		{
		return (param);
		}
	}
