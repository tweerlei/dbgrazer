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
package de.tweerlei.dbgrazer.web.constant;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class MessageKeys
	{
	/*
	 * Application paths
	 */
	
	/** Form interface */
	public static final String PATH_DB = "db";
	/** Webservice interface */
	public static final String PATH_WS = "ws";
	
	/*
	 * Number and date formats
	 */
	
	/** Timestamp date format */
	public static final String TIMESTAMP_DATE_FORMAT = "timestampDateFormat";
	/** Long date format */
	public static final String LONG_DATE_FORMAT = "longDateFormat";
	/** Short date format */
	public static final String SHORT_DATE_FORMAT = "shortDateFormat";
	/** Integer format */
	public static final String INTEGER_FORMAT = "integerFormat";
	/** Float format */
	public static final String FLOAT_FORMAT = "floatFormat";
	
	/*
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Search results tab title */
	public static final String RESULT_TAB = "$resultTab";
	/** Related queries tab title */
	public static final String EMPTY_TAB = "$emptyTab";
	/** Chart tab title */
	public static final String CHART_TAB = "$chartTab";
	/** Graph tab title */
	public static final String GRAPH_TAB = "$graphTab";
	/** Tree tab title */
	public static final String TREE_TAB = "$treeTab";
	/** Detail tab title */
	public static final String DETAIL_TAB = "$detailTab";
	/** DDL tab title */
	public static final String DDL_TAB = "$ddlTab";
	/** DML tab title */
	public static final String DML_TAB = "$dmlTab";
	/** Data tab title */
	public static final String DATA_TAB = "$dataTab";
	/** Catalog tab title */
	public static final String CATALOG_TAB = "$catalogTab";
	/** Schema tab title */
	public static final String SCHEMA_TAB = "$schemaTab";
	/** Connection tab title */
	public static final String CONNECTION_TAB = "$connectionTab";
	/** Ancestors tab title */
	public static final String ANCESTORS_TAB = "$ancestorsTab";
	/** Descendants tab title */
	public static final String DESCENDANTS_TAB = "$descendantsTab";
	/** Sum tab title */
	public static final String SUM_TAB = "$sumTab";
	/** Folders tab title */
	public static final String FOLDERS_TAB = "$foldersTab";
	/** Files tab title */
	public static final String FILES_TAB = "$filesTab";
	
	/*
	 * Multilevel names for the DB browser
	 */
	
	/** Catalog tab title */
	public static final String CATALOG_LEVEL = "$catalogLevel";
	/** Schema tab title */
	public static final String SCHEMA_LEVEL = "$schemaLevel";
	/** Schema tab title */
	public static final String OBJECT_LEVEL = "$schemaLevel";
	
	/*
	 * Other stuff
	 */
	
	/** Label for timechart time axis */
	public static final String TIMESTAMP = "timestamp";
	
	/** Header text for generated DDL files */
	public static final String DDL_HEADER = "ddlHeader";
	/** Header text for generated DML files */
	public static final String DML_HEADER = "dmlHeader";
	
	/** Header text for generated DDL compare files */
	public static final String DDL_COMPARE_HEADER = "ddlCompareHeader";
	/** Header text for generated DML compare files */
	public static final String DML_COMPARE_HEADER = "dmlCompareHeader";
	
	/** No data found */
	public static final String NO_DATA_FOUND = "noData";
	
	/** Name column title */
	public static final String NAME = "name";
	
	/** Value column title */
	public static final String VALUE = "value";
	
	/** Custom query: default table name */
	public static final String DEFAULT_TABLE_NAME = "defaultTableName";
	
	/** Custom query: default chart title */
	public static final String DEFAULT_CHART_TITLE = "defaultChartTitle";
	
	/** Login failed */
	public static final String LOGIN_FAILED = "loginFailed";
	
	/*
	 * Monitoring keys, sortable by name
	 */
	
	/** Rows from source table */
	public static final String SOURCE_ROWS = "001_sourceRows";
	/** Rows from destination table */
	public static final String DESTINATION_ROWS = "002_destinationRows";
	/** Matched rows */
	public static final String MATCHED = "003_matched";
	/** Inserted rows */
	public static final String INSERTED = "004_inserted";
	/** Updated rows */
	public static final String UPDATED = "005_updated";
	/** Deleted rows */
	public static final String DELETED = "006_deleted";
	
	/** Total statements */
	public static final String TOTAL_STATEMENTS = "007_totalStatements";
	/** Failed statements */
	public static final String FAILED_STATEMENTS = "008_failedStatements";
	/** Total rows */
	public static final String TOTAL_ROWS = "009_totalRows";
	/** Committed rows */
	public static final String COMMITTED_ROWS = "010_committedRows";
	
	/*
	 * Table metadata
	 */
	
	/** Metadata: DB object */
	public static final String DB_OBJECT = "dbObject";
	/** Metadata: DB object type */
	public static final String DB_OBJECT_TYPE = "dbObjectType";
	/** Metadata: Comment */
	public static final String COMMENT = "comment";
	/** Metadata: Columns tab */
	public static final String COLUMNS_TAB = "columnsTab";
	/** Metadata: Indices tab */
	public static final String INDICES_TAB = "indicesTab";
	/** Metadata: Referenced objects tab */
	public static final String REFERENCED_OBJECTS_TAB = "referencedObjectsTab";
	/** Metadata: Referencing objects tab */
	public static final String REFERENCING_OBJECTS_TAB = "referencingObjectsTab";
	/** Metadata: Privileges tab */
	public static final String PRIVILEGES_TAB = "privilegesTab";
	/** Metadata: PK index */
	public static final String PRIMARY_KEY_INDEX = "primaryKeyIndex";
	/** Metadata: Nullable */
	public static final String NULLABLE = "nullable";
	/** Metadata: Column type */
	public static final String COLUMN_TYPE = "columnType";
	/** Metadata: Length */
	public static final String LENGTH = "length";
	/** Metadata: Decimals */
	public static final String DECIMALS = "decimals";
	/** Metadata: Unique */
	public static final String UNIQUE = "unique";
	/** Metadata: Columns */
	public static final String COLUMNS = "columns";
	/** Metadata: Via FK */
	public static final String VIA_FK = "viaFK";
	/** Metadata: Source columns */
	public static final String SOURCE_COLUMNS = "sourceColumns";
	/** Metadata: Destination columns */
	public static final String DESTINATION_COLUMNS = "destinationColumns";
	/** Metadata: Action */
	public static final String ACTION = "action";
	/** Metadata: Grantee */
	public static final String GRANTEE = "grantee";
	/** Metadata: Grantor */
	public static final String GRANTOR = "grantor";
	/** Metadata: Grantable */
	public static final String GRANTABLE = "grantable";
	/** Metadata: True */
	public static final String TRUE = "trueText";
	/** Metadata: False */
	public static final String FALSE = "falseText";
	
	
	private MessageKeys()
		{
		}
	}
