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
 * Magic values used in RowSets
 * 
 * @author Robert Wruck
 */
public final class RowSetConstants
	{
	/** Magic result index for the related queries page */
	public static final int INDEX_RELATED = -1;
	/** Magic result index for the visualization page */
	public static final int INDEX_VISUALIZATION = -2;
	/** Magic result index for the tree page */
	public static final int INDEX_TREE = -3;
	/** Magic result index for the multilevel page */
	public static final int INDEX_MULTILEVEL = -4;
	
	/** RowSet attribute for the parent query */
	public static final String ATTR_PARENT_QUERY = "parentQuery";
	/** RowSet attribute for the level depth */
	public static final String ATTR_DEPTH = "depth";
	/** RowSet attribute for the more levels flag */
	public static final String ATTR_MORE_LEVELS = "moreLevels";
	/** RowSet attribute for the expand levels flag */
	public static final String ATTR_EXPAND_LEVELS = "expandLevels";
	/** RowSet attribute for the image id */
	public static final String ATTR_IMAGE_ID = "imageId";
	/** RowSet attribute for the image map */
	public static final String ATTR_IMAGEMAP = "imagemap";
	/** RowSet attribute for the image map ID */
	public static final String ATTR_IMAGEMAP_ID = "imagemapId";
	/** RowSet attribute for the option code */
	public static final String ATTR_OPTION_CODE = "optionCode";
	/** RowSet attribute for the option names */
	public static final String ATTR_OPTION_NAMES = "optionNames";
	/** RowSet attribute for the source text flag */
	public static final String ATTR_SOURCE_TEXT = "sourceText";
	/** RowSet attribute for the visualization */
	public static final String ATTR_VISUALIZATION = "visualization";
	/** RowSet attribute for the formatter name */
	public static final String ATTR_FORMATTER = "formatter";
	/** RowSet attribute for the colorize flag */
	public static final String ATTR_COLORIZE = "colorize";
	/** RowSet attribute for calculating a sum row */
	public static final String ATTR_SUM_ROW = "sumRow";
	/** RowSet attribute for the actual sum row */
	public static final String ATTR_SUM_VALUES = "sumValues";
	/** RowSet attribute for the diff flag */
	public static final String ATTR_DIFF = "diff";
	/** RowSet attribute for the trim flag */
	public static final String ATTR_TRIM = "trim";
	/** RowSet attribute for the formatting flag */
	public static final String ATTR_FORMATTING = "formatting";
	/** RowSet attribute for the syntax coloring flag */
	public static final String ATTR_SYNTAX_COLORING = "syntaxColoring";
	/** RowSet attribute for the line numbers flag */
	public static final String ATTR_LINE_NUMBERS = "lineNumbers";
	/** RowSet attribute for the table catalog name */
	public static final String ATTR_TABLE_CATALOG = "tableCatalog";
	/** RowSet attribute for the table schema name */
	public static final String ATTR_TABLE_SCHEMA = "tableSchema";
	/** RowSet attribute for the table name */
	public static final String ATTR_TABLE_NAME = "tableName";
	/** RowSet attribute for the table PK generation subselect */
	public static final String ATTR_TABLE_PK_SELECT = "tablePKSelect";
	
	private RowSetConstants()
		{
		}
	}
