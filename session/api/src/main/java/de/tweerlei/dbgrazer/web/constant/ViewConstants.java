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
package de.tweerlei.dbgrazer.web.constant;

/**
 * Magic values used by JSP views
 * 
 * @author Robert Wruck
 */
public final class ViewConstants
	{
	/** Image map ID */
	public static final String IMAGEMAP_ID = "graphmap";
	
	/** Parameter base name for passing query parameters to a view query */
	public static final String PARAM_URL_PARAMETER = "params";
	
	/** Separator for multiple parameters in a single String */
	public static final String PARAM_SEPARATOR = "  ";
	
	/** The empty view used by AJAX handlers that don't return any data */
	public static final String EMPTY_VIEW = "empty";
	
	private ViewConstants()
		{
		}
	}
