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
 * Liest Datens&auml;tze im Textformat ein
 * 
 * @author Robert Wruck
 */
public interface FieldReader
	{
	/**
	 * Liest eine Zeile und extrahiert die enthaltenen Felder.
	 * Ein Datensatz kann mehrere Zeilen in der Eingabe umfassen
	 * (z. B. wenn der Zeilenumbruch in einer CSV-Datei in Textbegrenzungen eingeschlossen ist.
	 * Wirft mit IOExceptions, wenn keine vollst&auml;ndige Zeile mehr gelesen werden kann.
	 * @return Gelesene Felder
	 * @throws IOException bei Fehlern
	 */
	public String[] readData() throws IOException;
	
	/**
	 * Schließt die Datenquelle 
	 * @throws IOException bei Fehlern
	 */
	public void close() throws IOException;
	
	/**
	 * Liefert die zuletzt eingelesene Zeile oder null, falls noch keine eingelesen wurde oder beim Einlesen ein Fehler auftrat.
	 * @return Zeile
	 */
	public String getLastLine();
	
	/**
	 * Liefert die Zeile in der Datenquelle
	 * (mu&szlig; nicht gleich der Anzahl der Datens&auml;tze sein!)
	 * @return Zeilennummer
	 */
	public int getLineNumber();
	}

/** @} */
