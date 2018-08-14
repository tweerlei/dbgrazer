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
package de.tweerlei.spring.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tweerlei.common.codec.Base64Codec;
import de.tweerlei.common.codec.StringCodec;
import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.spring.http.HttpClient;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.HttpHeaders;
import de.tweerlei.spring.http.HttpStatusCodes;
import de.tweerlei.spring.http.MimeType;

/**
 * URLConnection based impl.
 * 
 * @author Robert Wruck
 */
public class URLConnectionHttpClient implements HttpClient
	{
	private int connectTimeout;
	private int readTimeout;
	private Proxy proxy;
	private final Set<String> options;
	
	/**
	 * Constructor
	 * @param options Options
	 */
	public URLConnectionHttpClient(Set<String> options)
		{
		this.options = (options == null) ? new HashSet<String>() : options;
		this.connectTimeout = 1000;
		this.readTimeout = 10000;
		}
	
	/**
	 * Constructor
	 */
	public URLConnectionHttpClient()
		{
		this(null);
		}
	
	public void setConnectTimeout(int seconds)
		{
		this.connectTimeout = seconds;
		}
	
	public void setReadTimeout(int seconds)
		{
		this.readTimeout = seconds;
		}
	
	public void setProxy(String host, int port)
		{
		if (!StringUtils.empty(host))
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
		else
			proxy = null;
		}
	
	public void setOption(String option, boolean b)
		{
		if (b)
			options.add(option);
		else
			options.remove(option);
		}
	
	public void close()
		{
		}
	
	public HttpEntity get(String url, String username, String password) throws IOException
		{
		final URLConnection c = getConnection(url);
		
		authenticate(c, username, password);
		
		c.connect();
		
		return (readResponse(c));
		}
	
	public HttpEntity post(String url, HttpEntity request, String username, String password) throws IOException
		{
		final URLConnection c = getConnection(url);
		
		c.setDoOutput(true);	// Send POST data
		if (c instanceof HttpURLConnection)
			{
			final HttpURLConnection ht = (HttpURLConnection) c;
			// Prevent OutOfMemoryError caused by buffering the whole content to determine the Content-Length
			ht.setFixedLengthStreamingMode((int) request.getContentLength());
			}
		
		for (Map.Entry<String, String> ent : request.getHeaders().entrySet())
			{
			// Content-Length is calculated by URLConnection
			if (!HttpHeaders.CONTENT_LENGTH.equals(ent.getKey()))
				c.setRequestProperty(ent.getKey(), ent.getValue());
			}
		authenticate(c, username, password);
		
		c.connect();
		
		writeRequest(c, request);
		
		return (readResponse(c));
		}
	
	public HttpEntity post(String url, List<HttpEntity> request, String username, String password) throws IOException
		{
		final MultipartHttpEntity multipart = new MultipartHttpEntity("related", options);
		for (HttpEntity ent : request)
			multipart.add(ent);
		
		return (post(url, multipart, username, password));
		}
	
	private URLConnection getConnection(String spec) throws IOException
		{
		final URL url = new URL(spec);
		final URLConnection ret;
		
		if (proxy == null)
			ret = url.openConnection();
		else
			ret = url.openConnection(proxy);
		
		if (connectTimeout > 0)
			ret.setConnectTimeout(connectTimeout);
		if (readTimeout > 0)
			ret.setReadTimeout(readTimeout);
		
		return (ret);
		}
	
	private void authenticate(URLConnection c, String username, String password) throws IOException
		{
		if (!StringUtils.empty(username))
			{
			final StringCodec codec = new Base64Codec();
			final String authEnc = codec.encode((username + ":" + password).getBytes("ISO-8859-1"));
			c.setRequestProperty(HttpHeaders.AUTHORIZATION, "Basic " + authEnc);
			}
		}
	
	private void writeRequest(URLConnection c, HttpEntity request) throws IOException
		{
		final OutputStream stream = c.getOutputStream();
		try	{
			request.writeContent(stream);
			}
		finally
			{
			stream.close();
			}
		}
	
	private MimeType getType(String contentType)
		{
		MimeTypeBuilder mt;
		try	{
			mt = MimeTypeBuilder.parse(contentType);
			}
		catch (RuntimeException e)
			{
			mt = new MimeTypeBuilder("application", "octet-stream");
			}
		
		return (mt);
		}
	
	private HttpEntity readResponse(URLConnection c) throws IOException
		{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final InputStream r;
		final int status;
		
		if (c instanceof HttpURLConnection)
			{
			final HttpURLConnection ht = (HttpURLConnection) c;
			status = ht.getResponseCode();
			if (status >= HttpStatusCodes.BAD_REQUEST)
				r = ht.getErrorStream();
			else
				r = ht.getInputStream();
			}
		else
			{
			status = HttpStatusCodes.OK;
			r = c.getInputStream();
			}
		
		final long size;
		try	{
			size = StreamUtils.copy(r, baos);
			}
		finally
			{
			StreamUtils.closeQuietly(r);
			}
		
		final long length = c.getContentLength();
		if ((length >= 0) && (length != size))
			throw new IOException("Short read: " + size + "/" + length);
		
		final MimeType mimeType = getType(c.getHeaderField(HttpHeaders.CONTENT_TYPE));
		final ByteArrayHttpEntity ret = new ByteArrayHttpEntity(mimeType, baos.toByteArray());
		ret.setStatus(status);
		for (Map.Entry<String, List<String>> ent : c.getHeaderFields().entrySet())
			{
			if (ent.getKey() == null)
				ret.setHeader(HttpHeaders.STATUS, StringUtils.join(ent.getValue().iterator(), ","));
			else
				ret.setHeader(ent.getKey(), StringUtils.join(ent.getValue().iterator(), ","));
			}
		
		return (ret);
		}
	}
