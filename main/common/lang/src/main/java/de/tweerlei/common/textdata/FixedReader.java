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

/**
 * \addtogroup textdata Behandlung von Textdateien
 * @{
 */

/**
 * Liest eine Eingabe mit festen Feldl&auml;ngen zeilenweise ein
 * 
 * @author Robert Wruck
 */
public class FixedReader implements FieldReader
	{
	/** Datenquelle */
	private final LineNumberReader reader;
	
	/** Startoffsets für jedes Feld */
	private final int[] von;
	
	/** Endoffsets für jedes Feld */
	private final int[] bis;
	
	/** true, wenn die gelesenen Felder mit trim() behandelt werden sollen */
	private final boolean trim;
	
	/** Aktuelle Zeilennummer */
	private int zeile;
	
	/** Aktuelle Zeile */
	private String line;
	
	/** Wenn true, ist eine Zeile mit zu wenigen Zeichen ein Fehler */
	private boolean errorIfLineTooShort;
	
	/** Wenn true, ist eine Zeile mit zu vielen Zeichen ein Fehler */
	private boolean errorIfLineTooLong;
	
	/**
	 * Konstruktor
	 * @param r Die Datenquelle
	 * @param fields Die Feldl&auml;ngen
	 * @param t Wenn true, werden die Feldinhalte mit trim() bearbeitet
	 */
	public FixedReader(LineNumberReader r, int[] fields, boolean t)
		{
		reader = r;
		int vl = 0;
		von = new int[fields.length];
		bis = new int[fields.length];
		for (int i = 0; i < fields.length; i++)
			{
			von[i] = vl;
			bis[i] = vl + fields[i];
			vl += fields[i];
			}
		zeile = vl;
		trim = t;
		line = null;
		errorIfLineTooShort = true;
		errorIfLineTooLong = true;
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
		line = reader.readLine();
		if (line == null)
			throw new EOFException("End of file");
		
		final int len = line.length();
		
		if ((len < zeile) && errorIfLineTooShort)
			throw new DataFormatException("Line too short");
		if ((len > zeile) && errorIfLineTooLong)
			throw new DataFormatException("Line too long");
		
		final String[] ret = new String[von.length];
		for (int i = 0; i < von.length; i++)
			{
			final int v = (von[i] > len) ? len : von[i];
			final int b = (bis[i] > len) ? len : bis[i];
			
			if (trim)
				ret[i] = line.substring(v, b).trim();
			else
				ret[i] = line.substring(v, b);
			}
		
		return (ret);
		}
	
	public String getLastLine()
		{
		return (line);
		}
	
	/** @} */
	
	/**
	 * Liefert true, wenn zu lange Zeilen als Fehler betrachtet werden
	 * @return boolean
	 */
	public boolean getErrorIfLineTooLong()
		{
		return errorIfLineTooLong;
		}

	/**
	 * Liefert true, wenn zu kurze Zeilen als Fehler betrachtet werden
	 * @return boolean
	 */
	public boolean getErrorIfLineTooShort()
		{
		return errorIfLineTooShort;
		}

	/**
	 * @param b wenn true, werden zu lange Zeilen als Fehler betrachtet.
	 */
	public void setErrorIfLineTooLong(boolean b)
		{
		errorIfLineTooLong = b;
		}

	/**
	 * @param b wenn true, werden zu kurze Zeilen als Fehler betrachtet.
	 */
	public void setErrorIfLineTooShort(boolean b)
		{
		errorIfLineTooShort = b;
		}
	}

/** @} */
