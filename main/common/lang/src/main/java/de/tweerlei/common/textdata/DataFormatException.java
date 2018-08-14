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
package de.tweerlei.common.textdata;

import java.io.IOException;

/**
 * \addtogroup textdata Behandlung von Textdateien
 * @{
 */

/**
 * Exception, die ausgel&ouml;st wird, wenn das Datenformat fehlerhaft ist.
 * M&ouml;gliche F&auml;lle:
 * - Eingabe enth&auml;lt ung&uuml;ltige Zeile
 * - Methodenaufruf enth&auml;lt ung&uuml;ltiges Argument (z. B. falsche Feldanzahl)
 * 
 * @author Robert Wruck
 */
public class DataFormatException extends IOException
	{
	/**
	 * Konstruktor
	 * @param s Fehlerbeschreibung
	 */
	public DataFormatException(String s)
		{
		super(s);
		}
	}

/** @} */
