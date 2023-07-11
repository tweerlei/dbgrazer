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
 * Schreibt Datens&auml;tze im Textformat
 * 
 * @author Robert Wruck
 */
public interface FieldWriter
	{
	/**
	 * Schreibt einen Datensatz.
	 * Wirft mit IOExceptions, wenn nicht die komplette Zeile geschrieben werden kann.
	 * @param d Datenfelder
	 * @throws IOException bei Fehlern
	 */
	public void writeData(String[] d) throws IOException;
	
	/**
	 * Schließt die Datensenke
	 * @throws IOException bei Fehlern
	 */
	public void close() throws IOException;
	
	/**
	 * Liefert die Anzahl der geschriebenen Datens&auml;tze
	 * @return Zeilennummer
	 */
	public int getLineCount();
	}

/** @} */
