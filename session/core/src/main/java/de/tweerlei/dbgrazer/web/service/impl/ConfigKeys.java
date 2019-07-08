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

import de.tweerlei.common.math.Rational;
import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Frontend settings (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.web";
	
	/** Whether to enable login */
	public static final ConfigKey<Boolean> ENABLE_LOGIN = ConfigKey.create(PACKAGE_NAME, "enableLogin", Boolean.class, Boolean.TRUE);
	
	/** Whether to require login */
	public static final ConfigKey<Boolean> REQUIRE_LOGIN = ConfigKey.create(PACKAGE_NAME, "requireLogin", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the query history */
	public static final ConfigKey<Boolean> ENABLE_HISTORY = ConfigKey.create(PACKAGE_NAME, "enableHistory", Boolean.class, Boolean.FALSE);
	
	/** History items */
	public static final ConfigKey<Integer> HISTORY_LIMIT = ConfigKey.create(PACKAGE_NAME, "historyLimit", Integer.class, 20);
	
	/** Session timeout */
	public static final ConfigKey<Integer> SESSION_TIMEOUT = ConfigKey.create(PACKAGE_NAME, "sessionTimeout", Integer.class, null);
	
	/*
	 * Global permissions (config.properties only)
	 */
	
	/** Whether to enable the query editor */
	public static final ConfigKey<Boolean> ENABLE_EDITOR = ConfigKey.create(PACKAGE_NAME, "security.enableEditor", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable custom queries */
	public static final ConfigKey<Boolean> ENABLE_SUBMIT = ConfigKey.create(PACKAGE_NAME, "security.enableSubmit", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the DB browser */
	public static final ConfigKey<Boolean> ENABLE_BROWSER = ConfigKey.create(PACKAGE_NAME, "security.enableBrowser", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the DB designer */
	public static final ConfigKey<Boolean> ENABLE_DESIGNER = ConfigKey.create(PACKAGE_NAME, "security.enableDesigner", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable data modification */
	public static final ConfigKey<Boolean> ENABLE_DML = ConfigKey.create(PACKAGE_NAME, "security.enableDML", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the web service API */
	public static final ConfigKey<Boolean> ENABLE_WS = ConfigKey.create(PACKAGE_NAME, "security.enableWS", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the link editor */
	public static final ConfigKey<Boolean> ENABLE_LINKS = ConfigKey.create(PACKAGE_NAME, "security.enableLinkEditor", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the user editor */
	public static final ConfigKey<Boolean> ENABLE_USERS = ConfigKey.create(PACKAGE_NAME, "security.enableUserEditor", Boolean.class, Boolean.FALSE);
	
	/** Whether to enable the config editor */
	public static final ConfigKey<Boolean> ENABLE_CONFIG = ConfigKey.create(PACKAGE_NAME, "security.enableConfigEditor", Boolean.class, Boolean.TRUE);
	
	/** Expose SQL statements */
	public static final ConfigKey<Boolean> SHOW_SQL = ConfigKey.create(PACKAGE_NAME, "security.showSQL", Boolean.class, Boolean.FALSE);
	
	/** Expose graph sources */
	public static final ConfigKey<Boolean> SHOW_DOT = ConfigKey.create(PACKAGE_NAME, "security.showDOT", Boolean.class, Boolean.FALSE);
	
	/*
	 * Result settings (config.properties only)
	 */
	
	/** Display size limit for result columns */
	public static final ConfigKey<Integer> COLUMN_SIZE_LIMIT = ConfigKey.create(PACKAGE_NAME, "result.columnSizeLimit", Integer.class, 4096);
	
	/** Columns for hexadecimal presentation of binary data */
	public static final ConfigKey<Integer> HEX_COLUMN_LIMIT = ConfigKey.create(PACKAGE_NAME, "result.hexColumnLimit", Integer.class, 16);
	
	/** Fetch limit for dashboard queries */
	public static final ConfigKey<Integer> DASHBOARD_ROWS = ConfigKey.create(PACKAGE_NAME, "result.dashboardRows", Integer.class, 10);
	
	/** Fetch limit for panel queries */
	public static final ConfigKey<Integer> PANEL_ROWS = ConfigKey.create(PACKAGE_NAME, "result.panelRows", Integer.class, 20);
	
	/** Fetch limit for timechart queries */
	public static final ConfigKey<Integer> TIMECHART_ROWS = ConfigKey.create(PACKAGE_NAME, "result.timechartRows", Integer.class, 100);
	
	/** Fetch limit for browser queries */
	public static final ConfigKey<Integer> BROWSER_ROWS = ConfigKey.create(PACKAGE_NAME, "result.browserRows", Integer.class, 100);
	
	/** Whether to show empty query results */
	public static final ConfigKey<Boolean> SHOW_EMPTY_SUBQUERIES = ConfigKey.create(PACKAGE_NAME, "result.showEmptySubqueries", Boolean.class, Boolean.FALSE);
	
	/*
	 * Visualization settings (config.properties only)
	 */
	
	/** Base name for custom localizations */
	public static final ConfigKey<String> MESSAGES_FILE = ConfigKey.create(PACKAGE_NAME, "ui.messagesFile", String.class, null);
	
	/** Menu items per column */
	public static final ConfigKey<Rational> MENU_RATIO = ConfigKey.create(PACKAGE_NAME, "ui.menuRatio", Rational.class, new Rational(4, 3));
	
	/** Selectable autorefresh intervals */
	public static final ConfigKey<String> AUTOREFRESH_INTERVALS = ConfigKey.create(PACKAGE_NAME, "ui.autorefreshIntervals", String.class, "5,10,15,30,60");
	
	/** Render graphs as inline SVG (HTML5) */
	public static final ConfigKey<Boolean> INLINE_SVG = ConfigKey.create(PACKAGE_NAME, "ui.inlineSVG", Boolean.class, Boolean.FALSE);
	
	
	private ConfigKeys()
		{
		}
	}
