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
package de.tweerlei.dbgrazer.web.taglib.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.service.FrontendHelperService;

/**
 * JSP tag library
 *
 * @author Robert Wruck
 */
@Service
public class FileFunctions
	{
	// Hack to make the FrontendHelperService available to static JSP calls
	private static FrontendHelperService frontendHelper;
	
	/**
	 * Constructor 
	 * @param helper FrontendHelperService
	 */
	@Autowired
	public FileFunctions(FrontendHelperService helper)
		{
		frontendHelper = helper;
		}
	
	/**
	 * Get the base name of a path (the part after the last '/', if any)
	 * @param path Path
	 * @return Base name
	 */
	public static String basename(String path)
		{
		return (frontendHelper.basename(path));
		}
	
	/**
	 * Get the dir name of a path (the part before and including the last '/')
	 * @param path Path
	 * @return Dir name
	 */
	public static String dirname(String path)
		{
		return (frontendHelper.dirname(path));
		}
	}
