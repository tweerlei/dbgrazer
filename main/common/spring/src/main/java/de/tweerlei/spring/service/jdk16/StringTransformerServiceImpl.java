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
package de.tweerlei.spring.service.jdk16;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Normalizer;

import org.springframework.stereotype.Service;

import de.tweerlei.spring.service.StringTransformerService;

/**
 * Perform String transformations (mainly Unicode related)
 * 
 * @author Robert Wruck
 */
@Service
public class StringTransformerServiceImpl implements StringTransformerService
	{
	private static final String URL_ENCODING = "UTF-8";
	
	public String normalize(String s)
		{
		if (s == null)
			return (null);
		
		return (Normalizer.normalize(s, Normalizer.Form.NFC));
		}
	
	public String toASCII(String s)
		{
		if (s == null)
			return (null);
		
		final String tmp = Normalizer.normalize(s, Normalizer.Form.NFKD);
		
		final int l = tmp.length();
		final StringBuilder sb = new StringBuilder(l);
		for (int i = 0; i < l; i++)
			{
			final char c = tmp.charAt(i);
			if (c <= 0x7f)
				sb.append(c);
			}
		
		return (sb.toString());
		}
	
	public String toURL(String s)
		{
		if (s == null)
			return (null);
		
		try	{
			return (URLEncoder.encode(s, URL_ENCODING));
			}
		catch (UnsupportedEncodingException e)
			{
			// Should not happen for UTF-8...
			throw new RuntimeException(e);
			}
		}
	
	public String fromURL(String s)
		{
		if (s == null)
			return (null);
		
		try	{
			return (URLDecoder.decode(s, URL_ENCODING));
			}
		catch (UnsupportedEncodingException e)
			{
			// Should not happen for UTF-8...
			throw new RuntimeException(e);
			}
		}
	}
