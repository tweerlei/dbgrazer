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

/**
 * \addtogroup textdata Behandlung von Textdateien
 * @{
 */

/**
 * Schreibt Daten mit fester Feldbreite
 * 
 * @author Robert Wruck
 */
public class FixedWriter implements FieldWriter
	{
	/** Writer für Ausgabe */
	private final Writer outw;
	
	/** Zeilenendezeichen */
	private final String eol;
	
	/** Feldlängen */
	private final int[] fields;
	
	/** Anzahl der geschriebenen Zeilen */
	private int lines;
	
	/**
	 * Konstruktor
	 * @param w Writer f&uuml;r Ausgabedaten
	 * @param endl Zeilenendezeichen
	 * @param fl Die Feldl&auml;ngen
	 */
	public FixedWriter(Writer w, String endl, int[] fl)
		{
		outw = w;
		fields = fl;
		lines = 0;
		eol = endl;
		}
	
	/**
	 * \name Methoden aus FieldWriter
	 * @{
	 */
	
	public void writeData(String[] d) throws IOException
		{
		if (d.length != fields.length) throw new DataFormatException("Wrong number of fields");
		final StringBuffer line = new StringBuffer();
		
		for (int i = 0; i < fields.length; i++)
			{
			if (d[i] == null) throw new DataFormatException("Field is NULL: " + i);
			if (d[i].length() > fields[i]) throw new DataFormatException("Field too large: " + i + " (" + d[i] + ")");
			final StringBuffer sb = new StringBuffer(d[i]);
			
			while (sb.length() < fields[i])
				sb.append(' ');
			
			line.append(sb);
			}
		
		outw.write(line.toString());
		outw.write(eol);
		lines++;
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
