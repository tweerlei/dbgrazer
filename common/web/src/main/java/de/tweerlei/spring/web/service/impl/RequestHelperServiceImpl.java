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
package de.tweerlei.spring.web.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import de.tweerlei.common.codec.Base64Codec;
import de.tweerlei.common.codec.StringCodec;
import de.tweerlei.spring.web.service.RequestHelperService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service("requestHelperService")
public class RequestHelperServiceImpl implements RequestHelperService
	{
	private static final String HEADER_REFERER = "Referer";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	
	private static final Pattern AUTH_HEADER_PATTERN = Pattern.compile("Basic ([A-Za-z0-9+/=]+)");
	private static final String AUTH_SEPARATOR = ":";
	private static final String AUTH_CHARSET = "ISO-8859-1";
	
	private static final Map<String, Integer> DEFAULT_PORTS;
	static
		{
		DEFAULT_PORTS = new HashMap<String, Integer>();
		DEFAULT_PORTS.put("http", 80);
		DEFAULT_PORTS.put("https", 443);
		}
	
	private final StringCodec authCodec;
	
	/**
	 * Constructor
	 */
	public RequestHelperServiceImpl()
		{
		this.authCodec = new Base64Codec();
		}
	
	public String[] getBasicAuthentication(HttpServletRequest request)
		{
		final String hdr = request.getHeader(HEADER_AUTHORIZATION);
		if (hdr == null)
			return (null);
		
		final Matcher m = AUTH_HEADER_PATTERN.matcher(hdr);
		if (!m.matches())
			return (null);
		
		final String encodedAuth = m.group(1);
		final String auth;
		try	{
			final byte[] rawAuth = authCodec.decode(encodedAuth);
			auth = new String(rawAuth, AUTH_CHARSET);
			}
		catch (IOException e)
			{
			return (null);
			}
		
		final String[] parts = auth.split(AUTH_SEPARATOR);
		if (parts.length != 2)
			return (null);
		
		return (parts);
		}
	
	public URI getRequestURI(HttpServletRequest request)
		{
		try	{
			final URI rawURI = new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), request.getContextPath(), null, null);
			return (removeDefaultPort(rawURI));
			}
		catch (URISyntaxException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	public URI getReferrerURI(HttpServletRequest request)
		{
		final String referer = request.getHeader(HEADER_REFERER);
		if (referer == null)
			return (null);
		
		try	{
			final URI rawURI = new URI(referer);
			return (removeDefaultPort(rawURI));
			}
		catch (URISyntaxException e)
			{
			return (null);
			}
		}
	
	private URI removeDefaultPort(URI uri) throws URISyntaxException
		{
		final String scheme = uri.getScheme();
		if (scheme == null)
			return (uri);
		
		final Integer defaultPort = DEFAULT_PORTS.get(scheme.toLowerCase());
		if ((defaultPort == null) || (uri.getPort() != defaultPort.intValue()))
			return (uri);
		
		return (new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), -1, uri.getPath(), uri.getQuery(), uri.getFragment()));
		}
	}
