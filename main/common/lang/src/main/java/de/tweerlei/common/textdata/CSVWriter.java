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
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * \addtogroup textdata Behandlung von Textdateien
 * @{
 */

/**
 * Schreibt Trennzeichen-getrennte Daten
 * 
 * @author Robert Wruck
 */
public class CSVWriter implements FieldWriter
	{
	private static final boolean[] EMPTY_FLAGS = { };
	
	/** Writer für Ausgabe */
	private final Writer outw;
	
	/** Feldbegrenzungszeichen */
	private final String delim;
	
	/** Textbegrenzungszeichen */
	private final String text;
	private final Pattern textPattern;
	private final String textEscape;
	
	/** Zeilenendezeichen */
	private final String eol;
	
	/** Anzahl der zu schreibenden Felder pro Zeile */
	private final int fieldCount;
	
	private final boolean escapeAll;
	
	/** Anzahl der geschriebenen Zeilen */
	private int lines;
	
	/**
	 * Konstruktor
	 * Standardwerte: Feldtrenner Semikolon (;),
	 * Textbegrenzung Anf&uuml;hrungszeichen (")
	 * @param w Writer f&uuml;r die Ausgabe
	 */
	public CSVWriter(Writer w)
		{
		this(w, ";", "\"", "\"\"", "\n", 0, false);
		}
	
	/**
	 * Konstruktor
	 * @param w Writer f&uuml;r die Ausgabe
	 * @param d Feldtrennzeichen
	 * @param t Textbegrenzungszeichen
	 * @param endl String, der als Zeilenende verwendet werden soll
	 * @param fc Anzahl der Datenfelder oder 0.
	 *           Wenn nicht 0 werden Aufrufe von writeData zur&uuml;ckgewiesen,
	 *           wenn nicht die korrekte Anzahl an Feldern &uuml;bergeben wurde.
	 */
	public CSVWriter(Writer w, String d, String t, String endl, int fc)
		{
		this(w, d, t, t + t, endl, fc, false);
		}
	
	/**
	 * Konstruktor
	 * @param w Writer f&uuml;r die Ausgabe
	 * @param d Feldtrennzeichen
	 * @param t Textbegrenzungszeichen
	 * @param endl String, der als Zeilenende verwendet werden soll
	 * @param fc Anzahl der Datenfelder oder 0.
	 *           Wenn nicht 0 werden Aufrufe von writeData zur&uuml;ckgewiesen,
	 *           wenn nicht die korrekte Anzahl an Feldern &uuml;bergeben wurde.
	 * @param e Bei fehlender Angabe in writeData alle Felder in Textbegrenzungen einschließen
	 */
	public CSVWriter(Writer w, String d, String t, String endl, int fc, boolean e)
		{
		this(w, d, t, t + t, endl, fc, e);
		}
	
	/**
	 * Konstruktor
	 * @param w Writer f&uuml;r die Ausgabe
	 * @param d Feldtrennzeichen
	 * @param t Textbegrenzungszeichen
	 * @param esc Ersetzungszeichen für Textbegrenzungszeichen im Feldinhalt
	 * @param endl String, der als Zeilenende verwendet werden soll
	 * @param fc Anzahl der Datenfelder oder 0.
	 *           Wenn nicht 0 werden Aufrufe von writeData zur&uuml;ckgewiesen,
	 *           wenn nicht die korrekte Anzahl an Feldern &uuml;bergeben wurde.
	 * @param e Bei fehlender Angabe in writeData alle Felder in Textbegrenzungen einschließen
	 */
	public CSVWriter(Writer w, String d, String t, String esc, String endl, int fc, boolean e)
		{
		outw = w;
		delim = d;
		text = t;
		textPattern = Pattern.compile(t);
		textEscape = esc;
		eol = endl;
		fieldCount = fc;
		escapeAll = e;
		lines = 0;
		}
	
	/**
	 * Schreibt eine CSV-Zeile.
	 * Wirft mit IOExceptions, wenn nicht die komplette Zeile geschrieben werden kann.
	 * @param d Datenfelder
	 * @param t Wenn t[i], dann wird d[i] in Textbegrenzungen eingeschlossen
	 * @throws IOException bei Fehlern
	 */
	public void writeData(String[] d, boolean[] t) throws IOException
		{
		if ((fieldCount > 0) && (d.length != fieldCount))
			throw new DataFormatException("Wrong number of fields (" + d.length + "/" + fieldCount + ")");
		
		boolean first = true;
		for (int i = 0; i < d.length; i++)
			{
			if (d[i] == null)
				throw new DataFormatException("Field is NULL: " + i);
			
			if (first)
				first = false;
			else
				outw.write(delim);
			if ((i < t.length) ? t[i] : escapeAll)
				{
				outw.write(text);
				outw.write(textPattern.matcher(d[i]).replaceAll(textEscape));
				outw.write(text);
				}
			else
				outw.write(d[i]);
			}
		
		outw.write(eol);
		lines++;
		}
	
	/**
	 * \name Methoden aus FieldWriter
	 * @{
	 */
	
	public void writeData(String[] d) throws IOException
		{
		writeData(d, EMPTY_FLAGS);
		}
	
	public void close() throws IOException
		{
		outw.close();
		}
	
	public int getLineCount()
		{
		return (lines);
		}
	
	/** @} */
	}

/** @} */
