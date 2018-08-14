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
package de.tweerlei.spring.web.view.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.spring.service.StringTransformerService;
import de.tweerlei.spring.web.view.DownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Generic view for passing streamed data to the client.
 * 
 * @author Robert Wruck
 */
public abstract class BaseGenericDownloadView implements GenericDownloadView
	{
	// The date to be sent as expiry date for expire time "ALWAYS"
	private static final long EXPIRED = 978307200000L;	// Mon, 01 Jan 2001 00:00:00 GMT
	
	private final StringTransformerService stringTransformerService;
	private DownloadSource defaultSource;
	
	/**
	 * Constructor
	 * @param stringTransformerService StringTransformerService
	 */
	protected BaseGenericDownloadView(StringTransformerService stringTransformerService)
		{
		this.stringTransformerService = stringTransformerService;
		}
	
	/**
	 * Set the default DownloadSource
	 * @param source DownloadSource
	 */
	public void setDownloadSource(DownloadSource source)
		{
		this.defaultSource = source;
		}
	
	public String getContentType()
		{
		// We set the content type below
		return (null);
		}
	
	public void render(Map model, HttpServletRequest req, HttpServletResponse resp) throws Exception
		{
		final DownloadSource source;
		final Object o = model.get(SOURCE_ATTRIBUTE);
		if (o instanceof DownloadSource)
			source = (DownloadSource) o;
		else
			source = defaultSource;
		
		if ((source == null) || !source.canRead())
			{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
			}
		
		final long lastModified = source.getLastModified();
		if (lastModified > 0)
			{
			final long modifiedSince = req.getDateHeader("If-Modified-Since");
			
			if ((modifiedSince > 0) && (lastModified <= modifiedSince))
				{
				resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
				}
			
			resp.setDateHeader("Last-Modified", lastModified);
			}
		
		boolean buffer = false;
		final int length = source.getContentLength();
		if (length >= 0)
			resp.setContentLength(length);
		else if (source.isBuffered())
			buffer = true;
		
		resp.setContentType(source.getContentType());
		
		final String fileName = source.getFileName();
		if (fileName != null)
			{
			// In application/x-www-form-urlencoded, spaces are represented as "+" and "+" is encoded as "%2B".
			// The filename* parameter, however, expects spaces as "%20" while a "+" does not need to be encoded.
			final String utf8Filename = stringTransformerService.toURL(fileName).replace("+", "%20");
			resp.addHeader("Content-Disposition", (source.isAttachment() ? "attachment" : "inline") + "; filename=\"" + stringTransformerService.toASCII(fileName) + "\"; filename*=UTF-8''" + utf8Filename);
			}
		
		final Integer expireTime = source.getExpireTime();
		if (expireTime != null)
			{
			if (expireTime == DownloadSource.ALWAYS)
				{
				resp.setDateHeader("Expires", EXPIRED);
				resp.setDateHeader("Last-Modified", EXPIRED);
				resp.setHeader("Cache-Control", "no-store,no-cache,must-revalidate,post-check=0,pre-check=0");
				resp.setHeader("Pragma", "no-cache");
				}
			else if (expireTime.intValue() < 0)	// includes DownloadSource.NEVER
				{
				resp.setDateHeader("Expires", System.currentTimeMillis() + 365 * 86400 * 1000);
				resp.setHeader("Cache-Control", "max-age=" + String.valueOf(365 * 86400) + ",public,must-revalidate");
				}
			else
				{
				resp.setDateHeader("Expires", System.currentTimeMillis() + expireTime.intValue() * 1000);
				resp.setHeader("Cache-Control", "max-age=" + expireTime.toString() + ",public,must-revalidate");
				}
			}
		
		if (buffer)
			{
			final File tempFile = File.createTempFile("download", ".tmp");
			try	{
				final OutputStream ts = new FileOutputStream(tempFile);
				try	{
					source.write(ts);
					}
				finally
					{
					ts.close();
					}
				
				resp.setContentLength((int) tempFile.length());
				final OutputStream os = openStream(resp);
				final InputStream is = new FileInputStream(tempFile);
				try	{
					StreamUtils.copy(is, os);
					}
				finally
					{
					is.close();
					}
				
				closeStream(os);
				}
			finally
				{
				tempFile.delete();
				}
			}
		else
			{
			final OutputStream os = openStream(resp);
			source.write(os);
			closeStream(os);
			}
		}
	
	/**
	 * Open the output stream
	 * @param resp HttpServletResponse
	 * @return OutputStream
	 * @throws Exception on error
	 */
	protected abstract OutputStream openStream(HttpServletResponse resp) throws Exception;
	
	/**
	 * Close the stream returned by openStream
	 * @param stream OutputStream
	 * @throws Exception on error
	 */
	protected abstract void closeStream(OutputStream stream) throws Exception;
	}
