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
package de.tweerlei.common.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;

/**
 * \addtogroup misc Verschiedenes
 * @{
 */

/**
 * Hilfsroutinen f&uuml;r Dateizugriff
 * 
 * @author Robert Wruck
 */
public final class FileUtils
	{
	private static final int BUFFERSIZE = 4096;
	
	/** Unerreichbarer Konstruktor */
	private FileUtils()
		{
		// s.o.
		}
	
	/**
	 * Kopiert eine Datei
	 * @param in Quelldatei
	 * @param out Zieldatei
	 * @param overwrite Wenn true, wird die Zieldatei �berschrieben, wenn sie existiert
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 * @throws IOException bei Fehlern
	 * @throws FileExistsException wenn die Ausgabedatei existiert
	 */
	public static void copy(File in, File out, boolean overwrite) throws FileNotFoundException, IOException, FileExistsException
		{
		if (!overwrite && out.exists())
			throw new FileExistsException(out.getName());
		
		final FileInputStream fis = new FileInputStream(in);
		try	{
			final FileOutputStream fos = new FileOutputStream(out);
			
			try	{
				StreamUtils.copy(fis, fos);
				}
			finally
				{
				StreamUtils.closeQuietly(fos);
				}
			}
		finally
			{
			StreamUtils.closeQuietly(fis);
			}
		}
	
	/**
	 * Kopiert eine Datei und berechnet gleichzeitig eine Pr�fsumme
	 * @param in Quelldatei
	 * @param out Zieldatei
	 * @param digest MessageDigest f�r die Pr�fsumme
	 * @param overwrite Wenn true, wird die Zieldatei �berschrieben, wenn sie existiert
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 * @throws IOException bei Fehlern
	 * @throws FileExistsException wenn die Ausgabedatei existiert
	 */
	public static void copyAndChecksum(File in, File out, MessageDigest digest, boolean overwrite) throws FileNotFoundException, IOException, FileExistsException
		{
		if (!overwrite && out.exists())
			throw new FileExistsException(out.getName());
		
		final FileInputStream fis = new FileInputStream(in);
		try	{
			final FileOutputStream fos = new FileOutputStream(out);
			final DigestOutputStream dos = new DigestOutputStream(fos, digest);
			
			try	{
				StreamUtils.copy(fis, dos);
				}
			finally
				{
				StreamUtils.closeQuietly(dos);
				}
			}
		finally
			{
			StreamUtils.closeQuietly(fis);
			}
		}
	
	/**
	 * Kopiert eine Datei und berechnet gleichzeitig eine Pr�fsumme
	 * @param in Quelldatei
	 * @param out Zieldatei
	 * @param checksum Checksum f�r die Pr�fsumme
	 * @param overwrite Wenn true, wird die Zieldatei �berschrieben, wenn sie existiert
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 * @throws IOException bei Fehlern
	 * @throws FileExistsException wenn die Ausgabedatei existiert
	 */
	public static void copyAndChecksum(File in, File out, Checksum checksum, boolean overwrite) throws FileNotFoundException, IOException, FileExistsException
		{
		if (!overwrite && out.exists())
			throw new FileExistsException(out.getName());
		
		final FileInputStream fis = new FileInputStream(in);
		try	{
			final FileOutputStream fos = new FileOutputStream(out);
			final CheckedOutputStream dos = new CheckedOutputStream(fos, checksum);
			
			try	{
				StreamUtils.copy(fis, dos);
				}
			finally
				{
				StreamUtils.closeQuietly(dos);
				}
			}
		finally
			{
			StreamUtils.closeQuietly(fis);
			}
		}
	
	/**
	 * Verschiebt eine Datei (Kopieren und L�schen)
	 * @param in Quelldatei
	 * @param out Zieldatei
	 * @param overwrite Wenn true, wird die Zieldatei �berschrieben, wenn sie existiert
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 * @throws IOException bei Fehlern
	 * @throws FileExistsException wenn die Ausgabedatei existiert
	 * @throws FileDeletionException wenn die Eingabedatei nicht gel�scht werden konnte
	 */
	public static void move(File in, File out, boolean overwrite) throws FileNotFoundException, IOException, FileExistsException
		{
		if (!overwrite && out.exists())
			throw new FileExistsException(out.getName());
		
		if (!in.renameTo(out))
			{
			copy(in, out, overwrite);
			if (!in.delete())
				throw new FileDeletionException("Could not delete " + in.getAbsolutePath());
			}
		}
	
	/**
	 * Resolve a file against a directory. This works like File.getAbsoluteFile(), but it uses
	 * the given directory for resolution instead of user.dir
	 * @param file File
	 * @param directory Directory
	 * @return If the file is absolute, it is returned unchanged. Otherwise, an absolute file is
	 * created by resolving the relative file against the directory
	 */
	public static File resolveFile(File file, File directory)
		{
		if (file.isAbsolute())
			return (file);
		
		return (new File(directory, file.getPath()));
		}
	
	/**
	 * Erzeugt einen Dateinamen, der noch nicht vorhanden ist, auf Basis eines vorgegebenen Namens.
	 * Der Dateiname ist entweder das Argument, falls keine Datei gleichen Namens existiert,
	 * oder ein Name der Form base-n.ext, wobei n eine Zahl > 0 ist.
	 * @param file Zu testende Datei
	 * @return File-Objekt der einzigartigen Datei
	 */
	public static File makeUniqueName(File file)
		{
		if (!file.exists())
			return (file);
		
		final Filename fn = new Filename(file);
		final String base = fn.getBasename();
		
		for (int j = 1; ; j++)
			{
			fn.setBasename(base + "-" + j);
			final File f = fn.getFile();
			if (!f.exists())
				return (f);
			}
		}
	
	/**
	 * Verschiebt eine Datei.
	 * Falls das Ziel bereits existiert, wird an den Dateinamen ein Suffix angeh�ngt.
	 * @param in Quelldatei
	 * @param path Zielpfad
	 * @param file Zieldateiname
	 * @return die neue Datei
	 * @throws IOException bei Fehlern
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 */
	public static File moveUnique(File in, File path, String file) throws IOException, FileNotFoundException
		{
		final File f = makeUniqueName(new File(path, file));
		
		move(in, f, false);
		
		return (f);
		}
	
	/**
	 * Kopiert eine Datei.
	 * Falls das Ziel bereits existiert, wird an den Dateinamen ein Suffix angeh�ngt.
	 * @param in Quelldatei
	 * @param path Zielpfad
	 * @param file Zieldateiname
	 * @return die neue Datei
	 * @throws IOException bei Fehlern
	 * @throws FileNotFoundException wenn die Eingabedatei nicht existiert
	 */
	public static File copyUnique(File in, File path, String file) throws IOException, FileNotFoundException
		{
		final File f = makeUniqueName(new File(path,file));
		
		copy(in, f, false);
		
		return (f);
		}
	
	/**
	 * Sane version of File.delete():
	 * First, checks for file existence.
	 * If the file exists, try to delete it.
	 * If deletion fails, throws an IOException.
	 * @param f File (or directory) to delete
	 * @throws IOException on error
	 */
	public static void delete(File f) throws IOException
		{
		if (f.exists() && !f.delete())
			throw new IOException("Could not delete " + f.getAbsolutePath());
		}
	
	/**
	 * Sane version of File.renameTo()
	 * @param f existing file
	 * @param t new name
	 * @throws IOException on error
	 */
	public static void rename(File f, File t) throws IOException
		{
		if (!f.renameTo(t))
			throw new IOException("Could not rename " + f.getAbsolutePath() + " to " + t.getAbsolutePath());
		}
	
	/**
	 * Sane version of File.mkdirs():
	 * First, checks if the file refers to a directory.
	 * If not, try to create it.
	 * If creation fails, throws an IOException.
	 * @param f Directory to create
	 * @throws IOException on error
	 */
	public static void mkdir(File f) throws IOException
		{
		if (!f.isDirectory() && !f.mkdirs())
			throw new IOException("Could not create directory " + f.getAbsolutePath());
		}
	
	/**
	 * Remove a directory and all contained files and subdirs
	 * @param f Directory
	 * @throws IOException on error
	 */
	public static void rmdir(File f) throws IOException
		{
		final File[] files = f.listFiles();
		
		if (files != null)
			{
			for (int i = 0; i < files.length; i++)
				rmdir(files[i]);
			}
		
		delete(f);
		}
	
	/**
	 * Sane version of File.lastModified()
	 * @param f File
	 * @return Last modification date
	 * @throws IOException if no valid date was found
	 */
	public static Date getLastModified(File f) throws IOException
		{
		final long l = f.lastModified();
		if (l <= 0)
			throw new IOException("Could not get last modification of " + f.getAbsolutePath());
		
		return (new Date(l));
		}
	
	/**
	 * Sane version of File.setLastModified()
	 * @param f File
	 * @param d New modification date
	 * @throws IOException If the date could not be set
	 */
	public static void setLastModified(File f, Date d) throws IOException
		{
		if (d.getTime() <= 0)
			throw new IOException("Invalid date: " + d);
		
		if (!f.setLastModified(d.getTime()))
			throw new IOException("Could not set last modification of " + f.getAbsolutePath() + " to " + d);
		}
	
	/**
	 * Liest eine Datei in einen String
	 * @param file File
	 * @param charset Codierung
	 * @param maxSize Maximale L�nge
	 * @return Inhalt
	 * @throws IOException bei Fehlern
	 */
	public static String readFile(File file, String charset, int maxSize) throws IOException
		{
		final StringBuffer sb = new StringBuffer();
		
		final InputStreamReader r = new InputStreamReader(new FileInputStream(file), charset);
		try	{
			final char[] buffer = new char[BUFFERSIZE];
			
			for (;;)
				{
				final int i = r.read(buffer);
				if (i < 0)
					break;
				final int l = sb.length();
				if (l + i >= maxSize)
					{
					sb.append(buffer, 0, maxSize - l);
					break;
					}
				sb.append(buffer, 0, i);
				}
			}
		finally
			{
			StreamUtils.closeQuietly(r);
			}
		
		return (sb.toString());
		}
	
	/**
	 * Liest eine Datei in ein Byte-Array
	 * @param file File
	 * @param maxSize Maximale L�nge
	 * @return Inhalt
	 * @throws IOException bei Fehlern
	 */
	public static byte[] readFile(File file, int maxSize) throws IOException
		{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		final InputStream is = new FileInputStream(file);
		try	{
			final byte[] buffer = new byte[BUFFERSIZE];
			
			for (;;)
				{
				final int i = is.read(buffer);
				if (i < 0)
					break;
				final int l = baos.size();
				if (l + i >= maxSize)
					{
					baos.write(buffer, 0, maxSize - l);
					break;
					}
				baos.write(buffer, 0, i);
				}
			}
		finally
			{
			StreamUtils.closeQuietly(is);
			}
		
		return (baos.toByteArray());
		}
	
	/**
	 * Schreibt einen String in eine Datei
	 * @param file File
	 * @param charset Codierung
	 * @param data String
	 * @throws IOException bei Fehlern
	 */
	public static void writeFile(File file, String charset, String data) throws IOException
		{
		final OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), charset);
		try	{
			w.write(data);
			}
		finally
			{
			w.close();
			}
		}
	
	/**
	 * Schreibt ein Byte-Array in eine Datei
	 * @param file File
	 * @param data Bytes
	 * @throws IOException bei Fehlern
	 */
	public static void writeFile(File file, byte[] data) throws IOException
		{
		final OutputStream os = new FileOutputStream(file);
		try	{
			os.write(data);
			}
		finally
			{
			os.close();
			}
		}
	
	/**
	 * Get a File object for java.home
	 * @return File
	 */
	public static File getJavaHomeDir()
		{
		return (new File(System.getProperty("java.home")));
		}
	
	/**
	 * Get a File object for java.io.tmpdir
	 * @return File
	 */
	public static File getTempDir()
		{
		return (new File(System.getProperty("java.io.tmpdir")));
		}
	
	/**
	 * Get a File object for user.home
	 * @return File
	 */
	public static File getUserHomeDir()
		{
		return (new File(System.getProperty("user.home")));
		}
	
	/**
	 * Get a File object for user.dir
	 * @return File
	 */
	public static File getCurrentDir()
		{
		return (new File(System.getProperty("user.dir")));
		}
	}

/** @} */
