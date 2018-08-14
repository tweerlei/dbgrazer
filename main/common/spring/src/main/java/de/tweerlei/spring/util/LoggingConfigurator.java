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
package de.tweerlei.spring.util;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Apply logging levels to JDK Loggers
 * 
 * @author Robert Wruck
 */
public class LoggingConfigurator
	{
	/**
	 * Constructor
	 * @param props Properties: Logger name -> Level name
	 */
	public LoggingConfigurator(Properties props)
		{
		Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "Applying logging levels");
		
		for (Map.Entry<Object, Object> ent : props.entrySet())
			{
			final String loggerName = ent.getKey().toString();
			final String levelName = ent.getValue().toString();
			
			final Level level = Level.parse(levelName);
			final Logger l = Logger.getLogger(loggerName);
			l.setLevel(level);
			}
		}
	}
