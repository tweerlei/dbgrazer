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
package de.tweerlei.dbgrazer.extension.jdbc;

import java.util.List;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLConsumer;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Generate SQL statement fragments from metadata
 *
 * @author Robert Wruck
 */
public interface SQLGeneratorService
	{
	/** Ordering options for generateSelect */
	public static enum Style
		{
		/** One line */
		SIMPLE,
		/** Order by PK */
		MULTILINE,
		/** Each field on a separate line*/
		INDENTED
		}
	
	/** Join options for generateSelect */
	public static enum Joins
		{
		/** No joins */
		NONE,
		/** Inner joins (not nullable columns) only */
		INNER,
		/** All joins */
		ALL
		}
	
	/** Ordering options for generateSelect */
	public static enum OrderBy
		{
		/** Unordered */
		NONE,
		/** Order by PK */
		PK,
		/** All but PK */
		DATA
		}
	
	/** Aggregation mode */
	public enum AggregationMode
		{
		/** No aggregation */
		NONE(false),
		/** Ignore column */
		HIDDEN(false),
		/** Count */
		COUNT(false),
		/** Distinct count */
		COUNT_DISTINCT(false),
		/** Minimum */
		MIN(false),
		/** Maximum */
		MAX(false),
		/** Sum */
		SUM(true),
		/** Average */
		AVG(true);
		
		private final boolean numberRequired;
		
		private AggregationMode(boolean numberRequired)
			{
			this.numberRequired = numberRequired;
			}
		
		/**
		 * CHeck whether this AggregationMode requires a numeric column
		 * @return Number required
		 */
		public boolean isNumberRequired()
			{
			return (numberRequired);
			}
		}
	
	/** Aggregate column */
	public static final class AggregateColumn
		{
		private final String name;
		private final String condition;
		private final AggregationMode mode;
		
		/**
		 * Constructor
		 * @param name Column name
		 * @param condition Condition
		 * @param mode Aggregation mode
		 */
		public AggregateColumn(String name, String condition, AggregationMode mode)
			{
			this.name = name;
			this.condition = condition;
			this.mode = mode;
			}

		/**
		 * Get the name
		 * @return Name
		 */
		public String getName()
			{
			return name;
			}

		/**
		 * Get the aggregation mode
		 * @return aggregation mode
		 */
		public AggregationMode getMode()
			{
			return mode;
			}

		/**
		 * Get the condition
		 * @return the condition
		 */
		public String getCondition()
			{
			return condition;
			}
		}
	
	/**
	 * Format a column name
	 * @param name Name
	 * @param dialect SQLDialect
	 * @return Formatted name
	 */
	public String formatColumnName(String name, SQLDialect dialect);
	
	/**
	 * Generate a SELECT statement for all columns and all rows
	 * @param t TableDescription
	 * @param style Formatting style
	 * @param joins Which joins to generate
	 * @param where WHERE clause
	 * @param orderBy Columns to order by
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateSelect(TableDescription t, Style style, Joins joins, String where, OrderBy orderBy, SQLDialect dialect);
	
	/**
	 * Generate a SELECT statement for all columns and all rows
	 * @param t TableDescription
	 * @param style Formatting style
	 * @param critColumns Columns for IN clause
	 * @param critCount Value count for IN clause
	 * @param orderBy Columns to order by
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateSelectIn(TableDescription t, Style style, Set<Integer> critColumns, int critCount, OrderBy orderBy, SQLDialect dialect);
	
	/**
	 * Generate a SELECT statement by PK
	 * @param t TableDescription
	 * @param style Formatting style
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generatePKSelect(TableDescription t, Style style, SQLDialect dialect);
	
	/**
	 * Generate a SELECT statement for column statistics
	 * @param t TableDescription
	 * @param style Formatting style
	 * @param where WHERE clause
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateSelectStats(TableDescription t, Style style, String where, SQLDialect dialect);
	
	/**
	 * Generate a SELECT statement for a table
	 * @param t TableDescription
	 * @param style Formatting style
	 * @param where WHERE clause
	 * @param order ORDER BY clause
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateSelect(TableDescription t, Style style, String where, String order, SQLDialect dialect);
	
	/**
	 * Generate a SELECT COUNT statement for a table
	 * @param t Table name
	 * @param style Formatting style
	 * @param where WHERE clause
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateSelectCount(QualifiedName t, Style style, String where, SQLDialect dialect);
	
	/**
	 * Generate a DELETE statement for a table
	 * @param t Table name
	 * @param style Formatting style
	 * @param where WHERE clause
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateDelete(QualifiedName t, Style style, String where, SQLDialect dialect);
	
	/**
	 * Generate a TRUNCATE statement for a table
	 * @param t Table name
	 * @param style Formatting style
	 * @param dialect SQLDialect
	 * @return Statement
	 */
	public String generateTruncate(QualifiedName t, Style style, SQLDialect dialect);
	
	/**
	 * Create an aggregate query from a query
	 * @param query Query
	 * @param columns Aggregate column definitions
	 * @return Transformed query
	 */
	public String createAggregateQuery(String query, List<AggregateColumn> columns);
	
	/**
	 * Create an aggregate query from a query where HIDDEN columns will not be returned
	 * @param query Query
	 * @param columns Aggregate column definitions
	 * @return Transformed query
	 */
	public String createVariableAggregateQuery(String query, List<AggregateColumn> columns);
	
	/**
	 * Create a query that returns the number of rows produced by a given query
	 * @param query Query
	 * @return Row count query
	 */
	public String createRowCountQuery(String query);
	
	/**
	 * Create an SQLConsumer for the given style
	 * @param style Style
	 * @return SQLConsumer
	 */
	public SQLConsumer createConsumer(Style style);
	
	/**
	 * Parse an SQL script into separate statements
	 * @param script SQL script
	 * @param dialect SQLDialect
	 * @return Statements
	 */
	public List<String> parseScript(String script, SQLDialect dialect);
	}
