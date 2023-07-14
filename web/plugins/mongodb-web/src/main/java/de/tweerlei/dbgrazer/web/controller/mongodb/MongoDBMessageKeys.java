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
package de.tweerlei.dbgrazer.web.controller.mongodb;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class MongoDBMessageKeys
	{
	/** JS extension file */
	public static final String EXTENSION_JS = "mongodb.js";
	
	/*
	 * Multilevel names for the DB browser
	 */
	
	/** Topic tab title */
	public static final String DATABASE_LEVEL = "$databaseLevel";
	/** Partition tab title */
	public static final String COLLECTION_LEVEL = "$collectionLevel";
	/** Message tab title */
	public static final String DOCUMENT_LEVEL = "$documentLevel";
	
	/*
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Topics tab title */
	public static final String DATABASES_TAB = "$mongoDatabasesTab";
	/** Partitions tab title */
	public static final String COLLECTIONS_TAB = "$mongoCollectionsTab";
	/** Messages tab title */
	public static final String DOCUMENTS_TAB = "$mongoDocumentsTab";
	
	/*
	 * Column names
	 */
	/** ID column */
	public static final String ID = "id";
	/** Name column */
	public static final String DATABASE = "mongoDatabase";
	/** Name column */
	public static final String COLLECTION = "mongoCollection";
	/** ID column */
	public static final String REPLICAS = "replicas";
	/** ID column */
	public static final String IN_SYNC_REPLICAS = "inSyncReplicas";
	/** ID column */
	public static final String LEADER = "leader";
	/** ID column */
	public static final String NODES = "$nodes";
	/** ID column */
	public static final String HOST = "nodeHost";
	/** ID column */
	public static final String PORT = "nodePort";
	/** ID column */
	public static final String RACK = "nodeRack";
	/** ID column */
	public static final String ACLS = "$acls";
	/** ID column */
	public static final String PRINCIPAL = "aclPrincipal";
	/** ID column */
	public static final String OPERATION = "aclOperation";
	/** ID column */
	public static final String PERMISSION_TYPE = "aclPermissionType";
	/** ID column */
	public static final String CONFIGS = "$configs";
	/** ID column */
	public static final String RESOURCE = "configResource";
	/** ID column */
	public static final String KEY = "configKey";
	/** ID column */
	public static final String VALUE = "configValue";
	
	
	private MongoDBMessageKeys()
		{
		}
	}
