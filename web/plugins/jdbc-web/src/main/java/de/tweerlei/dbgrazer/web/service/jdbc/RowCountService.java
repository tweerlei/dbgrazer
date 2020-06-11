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
package de.tweerlei.dbgrazer.web.service.jdbc;

import java.util.Map;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Count table rows
 * 
 * @author Robert Wruck
 */
public interface RowCountService
	{
	/**
	 * Helper bean for table row counts
	 */
	public static final class RowCounts
		{
		private final QualifiedName srcName;
		private final Object srcCount;
		private QualifiedName dstName;
		private Object dstCount;
		
		/**
		 * Constructor
		 * @param srcName Source table name
		 * @param srcCount Source row count
		 * @param dstName Destination table name
		 * @param dstCount Destination row count
		 */
		public RowCounts(QualifiedName srcName, Object srcCount, QualifiedName dstName, Object dstCount)
			{
			this.srcName = srcName;
			this.srcCount = srcCount;
			this.dstName = dstName;
			this.dstCount = dstCount;
			}
		
		/**
		 * Get the source table name
		 * @return source table name
		 */
		public QualifiedName getSrcName()
			{
			return srcName;
			}
		
		/**
		 * Get the destination table name
		 * @return destination table name
		 */
		public QualifiedName getDstName()
			{
			return dstName;
			}
		
		/**
		 * Get the source row count
		 * @return source row count
		 */
		public Object getSrcCount()
			{
			return srcCount;
			}
		
		/**
		 * Get the destination row count
		 * @return destination row count
		 */
		public Object getDstCount()
			{
			return dstCount;
			}
		
		/**
		 * Set the destination table name
		 * @param name destination table name
		 */
		public void setDstName(QualifiedName name)
			{
			dstName = name;
			}
		
		/**
		 * Set the destination row count
		 * @param count destination row count
		 */
		public void setDstCount(Object count)
			{
			dstCount = count;
			}
		}
	
	/**
	 * Count rows in a single table
	 * @param link Link name
	 * @param table Table name
	 * @param dialect SQLDialect
	 * @return Row count
	 */
	public Object countRows(String link, QualifiedName table, SQLDialect dialect);
	
	/**
	 * Count rows in multiple tables
	 * @param link Link name
	 * @param tables Table names
	 * @param dialect SQLDialect
	 * @param monitor For tracking progress
	 * @return Row counts
	 */
	public Map<QualifiedName, Object> countRows(String link, Set<QualifiedName> tables, SQLDialect dialect, TaskProgress monitor);
	
	/**
	 * Merge two sets of row counts
	 * @param src LHS
	 * @param dst RHS
	 * @param dialect SQLDialect
	 * @param qualified Use fully qualified names instead of just object names
	 * @return Merged row counts
	 */
	public Map<String, RowCounts> mergeRowCounts(Map<QualifiedName, Object> src, Map<QualifiedName, Object> dst, SQLDialect dialect, boolean qualified);
	}
