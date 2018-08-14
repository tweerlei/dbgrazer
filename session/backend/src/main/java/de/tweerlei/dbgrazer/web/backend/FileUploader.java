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
package de.tweerlei.dbgrazer.web.backend;

import java.io.InputStream;

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.query.model.RowProducer;

/**
 * Create a DownloadSource for a Result
 * 
 * @author Robert Wruck
 */
public interface FileUploader extends Named
	{
	/**
	 * Create a RowProducer for table data supplied as InputStream
	 * @param info TableDescription
	 * @param input InputStream
	 * @return RowProducer
	 */
	public RowProducer createRowProducer(TableDescription info, InputStream input);
	}
