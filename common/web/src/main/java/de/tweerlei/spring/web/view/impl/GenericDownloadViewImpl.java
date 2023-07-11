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

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.spring.service.StringTransformerService;

/**
 * Generic view for passing streamed data to the client.
 * 
 * @author Robert Wruck
 */
@Service
@Scope("request")
@Qualifier("default")
public class GenericDownloadViewImpl extends BaseGenericDownloadView
	{
	/**
	 * Constructor
	 * @param stringTransformerService StringTransformerService
	 */
	@Autowired
	public GenericDownloadViewImpl(StringTransformerService stringTransformerService)
		{
		super(stringTransformerService);
		}
	
	/**
	 * Open the output stream
	 * @param resp HttpServletResponse
	 * @return OutputStream
	 * @throws Exception on error
	 */
	@Override
	protected OutputStream openStream(HttpServletResponse resp) throws Exception
		{
		return (resp.getOutputStream());
		}
	
	/**
	 * Close the stream returned by openStream
	 * @param stream OutputStream
	 * @throws Exception on error
	 */
	@Override
	protected void closeStream(OutputStream stream) throws Exception
		{
		stream.close();
		}
	}
