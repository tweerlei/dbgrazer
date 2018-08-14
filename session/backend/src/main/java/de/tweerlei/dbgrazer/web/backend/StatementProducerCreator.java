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

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Create DownloadSource objects for query results
 * 
 * @author Robert Wruck
 */
public interface StatementProducerCreator extends Named
	{
	/**
	 * Create a StatementProducer for a RowProducer
	 * @param p RowProducer
	 * @param info TableDescription
	 * @param dialect SQLDialect
	 * @return StatementProducer
	 */
	public StatementProducer getStatementProducer(RowProducer p, TableDescription info, SQLDialect dialect);
	}
