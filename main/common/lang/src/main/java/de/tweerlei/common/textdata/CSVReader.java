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

import java.io.EOFException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import de.tweerlei.common.util.StringUtils;

/**
 * \addtogroup textdata Behandlung von Textdateien
 * @{
 */

/**
 * Liest eine Trennzeichen-getrennte Eingabe zeilenweise ein
 * 
 * @author Robert Wruck
 */
public class CSVReader implements FieldReader
	{
	/** Reader für Eingabezeilen */
	private final LineNumberReader reader;
	
	/** Trennzeichen für Felder */
	private final String delim;
	
	/** Textbegrenzungszeichen */
	private final String text;
	
	/** Erwartete Feldanzahl */
	private final int fieldCount;
	
	/** Ob Feldinhalte mit trim() behandelt werden sollen */
	private final boolean trim;
	
	/** Die aktuelle Zeile */
	private String line;
	
	/**
	 * Konstruktor
	 * @param r Reader, der die Zeilen liefert
	 * Standardwerte: Feldtrenner Semikolon (;),
	 * Textbegrenzungszeichen Anf&uuml;hrungszeichen ("),
	 * Felder werden nicht getrimmt.
	 */
	public CSVReader(LineNumberReader r)
		{
		this(r, ";", "\"", 0, false);
		}
	
	/**
	 * Konstruktor
	 * @param r Reader, der die Zeilen liefert
	 * @param d Feldtrennzeichen
	 * @param t Textbegrenzungszeichen
	 * @param fc Anzahl der erwarteten Felder oder 0.
	 *           Wenn nicht 0 wird eine Zeile nur dann als g&uuml;ltig angesehen,
	 *           wenn sie genau diese Anzahl Felder enth&auml;lt.
	 * @param tr Wenn true werden alle Felder mit trim() behandelt.
	 */
	public CSVReader(LineNumberReader r, String d, String t, int fc, boolean tr)
		{
		reader = r;
		delim = d;
		text = StringUtils.nullIfEmpty(t);
		fieldCount = fc;
		trim = tr;
		line = null;
		}
	
	/**
	 * \name Methoden aus FieldReader
	 * @{
	 */
	
	public void close() throws IOException
		{
		reader.close();
		}
	
	public int getLineNumber()
		{
		return (reader.getLineNumber());
		}
	
	public String[] readData() throws IOException
		{
		ArrayList ret;
		boolean complete;
		int i;
		String[] a;
		
		line = "";
		
		do	{
			complete = true;
			ret = new ArrayList();
			i = 0;
			String s = reader.readLine();
			if (s == null)
				throw new EOFException("End of file");
			
			line += s;
			
//			a = s.trim().split(delim);
			a = StringUtils.split(s.trim(), delim);
			for (int j = 0; j < a.length; j++)
				{
				if (trim) a[j] = a[j].trim();
				
				if ((text != null) && !complete)	// letztes Feld war nicht abgeschlossen
					{
					if (a[j].endsWith(text))	// Feld schlie?t ab
						{
						ret.set(i, ret.get(i) + delim + a[j].substring(0, a[j].length() - 1));
						i++;
						complete = true;
						}
					else	// Feld schlie?t nicht ab
						ret.set(i, ret.get(i) + delim + a[j]);
					}
				else if ((text != null) && a[j].startsWith(text))	// Feld beginnt Textsequenz
					{
					if ((a[j].length() > text.length()) && a[j].endsWith(text))	// Vollst?ndig abgeschlossen
						{
						ret.add(a[j].substring(1, a[j].length() - 1));
						i++;
						}
					else	// Textsequenz wird im n?chsten Feld fortgesetzt
						{
						ret.add(a[j].substring(1, a[j].length()));
						complete = false;
						}
					}
				else
					{
					ret.add(a[j]);
					i++;
					}
				}
			}
		while (!complete);
		
		if ((fieldCount > 0) && (i != fieldCount))
			throw new DataFormatException("Wrong number of fields (" + i + "/" + fieldCount + ")");
		
		a = new String[i];
		return ((String[]) ret.toArray(a));
		}
	
	public String getLastLine()
		{
		return (line);
		}
	
	/** @} */
	}

/** @} */
