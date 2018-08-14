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
 * Contract exception.
 * Derived exceptions should be thrown on implementation errors.
 * 
 * @author Robert Wruck
 */
public abstract class ContractException extends RuntimeException
	{
	/**
	 * Constructor
	 * @param message Error message
	 */
	protected ContractException(String message)
		{
		super(message);
		}
	
	/**
	 * Constructor
	 * @param e Cause
	 * @param message Error message
	 */
	protected ContractException(Throwable e, String message)
		{
		super(message, e);
		}
	}
