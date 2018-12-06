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
package de.tweerlei.dbgrazer.web.export.ldif.download;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import de.tweerlei.common.io.ByteOrderMarkWriter;
import de.tweerlei.common.textdata.LDIFWriter;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Base class for creating LDIF files
 * 
 * @author Robert Wruck
 */
public abstract class AbstractLdifDownloadSource extends AbstractDownloadSource
	{
	private static final String CHARSET = "UTF-8";
	private static final String CONTENT_TYPE = "text/directory; charset=" + CHARSET;
	private static final String EXTENSION = ".ldif";
	
	/**
	 * Constructor
	 * @param name File base name
	 */
	public AbstractLdifDownloadSource(String name)
		{
		this.setAttachment(true);
		this.setExpireTime(DownloadSource.ALWAYS);
		this.setFileName(name + EXTENSION);
		}
	
	@Override
	public final String getContentType()
		{
		return (CONTENT_TYPE);
		}
	
	@Override
	public final void write(OutputStream stream) throws IOException
		{
		final Writer osw = new ByteOrderMarkWriter(new OutputStreamWriter(stream, CHARSET));
		try	{
			final LDIFWriter cw = new LDIFWriter(osw);
			
			writeLdif(cw);
			}
		finally
			{
			osw.flush();
			}
		}
	
	/**
	 * Write the LDIF data
	 * @param cw LDIFWriter
	 * @throws IOException on error
	 */
	protected abstract void writeLdif(LDIFWriter cw) throws IOException;
	}
