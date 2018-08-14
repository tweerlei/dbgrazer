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
package de.tweerlei.dbgrazer.web.service.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.web.backend.FileUploader;
import de.tweerlei.dbgrazer.web.service.UploadService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class UploadServiceImpl implements UploadService
	{
	private final Logger logger;
	private final Map<String, FileUploader> formats;
	
	/**
	 * Constructor
	 * @param formats FileUploaders
	 */
	@Autowired(required = false)
	public UploadServiceImpl(Set<FileUploader> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<FileUploader>(formats));
		
		this.logger.log(Level.INFO, "File uploaders: " + this.formats);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 */
	public UploadServiceImpl()
		{
		this(Collections.<FileUploader>emptySet());
		}
	
	@Override
	public Set<String> getSupportedUploadFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public RowProducer createRowProducer(TableDescription info, InputStream input, String format)
		{
		final FileUploader c = formats.get(format);
		if (c != null)
			return (c.createRowProducer(info, input));
		
		return (null);
		}
	}
