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
package de.tweerlei.dbgrazer.web.session.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.web.model.CustomQuery;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.TableFilterEntry;
import de.tweerlei.dbgrazer.web.model.TableSet;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
@Scope("request")
public class ConnectionSettingsImpl implements ConnectionSettings, Serializable
	{
	private String name;
	private transient LinkType type;
	private String schema;
	private String version;
	private String description;
	private String dialect;
	private String group;
	private boolean writable;
	private boolean editor;
	private boolean editorActive;
	private boolean submit;
	private boolean browser;
	private boolean designer;
	private boolean ws;
	private boolean recordEditor;
	private SchemaSettings schemaSettings;
	private PathInfo sourceURL;
	
	@Override
	public void setLink(LinkDef link, SchemaSettings schemaSettings)
		{
		this.name = link.getName();
		this.type = link.getType();
		this.schema = link.getSchema().getName();
		this.version = link.getSchema().getVersion();
		this.description = link.getFullDescription();
		this.dialect = link.getDialectName();
		this.group = link.getGroupName();
		this.writable = link.isWritable();
		this.schemaSettings = (schemaSettings == null) ? new SchemaSettingsImpl() : schemaSettings;
		}
	
	@Override
	public void resetLink()
		{
		this.name = null;
		this.type = null;
		this.schema = null;
		this.version = null;
		this.description = null;
		this.dialect = null;
		this.group = null;
		this.writable = false;
		this.schemaSettings = null;
		}
	
	@Override
	public String getLinkName()
		{
		return (name);
		}
	
	@Override
	public LinkType getType()
		{
		return (type);
		}
	
	@Override
	public String getDescription()
		{
		return (description);
		}
	
	@Override
	public String getSchemaName()
		{
		return (schema);
		}
	
	@Override
	public String getSchemaVersion()
		{
		return (version);
		}
	
	@Override
	public String getDialectName()
		{
		return (dialect);
		}
	
	@Override
	public String getGroupName()
		{
		return (group);
		}
	
	@Override
	public boolean isWritable()
		{
		return (writable && recordEditor);
		}
	
	@Override
	public boolean isEditorEnabled()
		{
		return (editor);
		}
	
	@Override
	public void setEditorEnabled(boolean b)
		{
		editor = b;
		}

	@Override
	public boolean isEditorActive()
		{
		return (editorActive);
		}
	
	@Override
	public void setEditorActive(boolean b)
		{
		editorActive = b;
		}

	@Override
	public boolean isSubmitEnabled()
		{
		return (submit);
		}
	
	@Override
	public void setSubmitEnabled(boolean b)
		{
		submit = b;
		}

	@Override
	public boolean isBrowserEnabled()
		{
		return (browser);
		}
	
	@Override
	public void setBrowserEnabled(boolean b)
		{
		browser = b;
		}

	@Override
	public boolean isDesignerEnabled()
		{
		return (designer);
		}
	
	@Override
	public void setDesignerEnabled(boolean b)
		{
		designer = b;
		}

	@Override
	public boolean isWsApiEnabled()
		{
		return (ws);
		}
	
	@Override
	public void setWsApiEnabled(boolean b)
		{
		ws = b;
		}

	@Override
	public boolean isRecordEditorEnabled()
		{
		return (recordEditor);
		}
	
	@Override
	public void setRecordEditorEnabled(boolean b)
		{
		recordEditor = b;
		}

	@Override
	public String getCatalog()
		{
		if (schemaSettings != null)
			return (schemaSettings.getCatalog());
		return (null);
		}

	@Override
	public void setCatalog(String catalog)
		{
		if (schemaSettings != null)
			schemaSettings.setCatalog(catalog);
		}

	@Override
	public String getSchema()
		{
		if (schemaSettings != null)
			return (schemaSettings.getSchema());
		return (null);
		}

	@Override
	public void setSchema(String schema)
		{
		if (schemaSettings != null)
			schemaSettings.setSchema(schema);
		}
	
	@Override
	public String getQueryGroup()
		{
		if (schemaSettings != null)
			return (schemaSettings.getQueryGroup());
		return (null);
		}
	
	@Override
	public void setQueryGroup(String queryGroup)
		{
		if (schemaSettings != null)
			schemaSettings.setQueryGroup(queryGroup);
		}
	
	@Override
	public String getSearch()
		{
		if (schemaSettings != null)
			return (schemaSettings.getSearch());
		return (null);
		}
	
	@Override
	public void setSearch(String search)
		{
		if (schemaSettings != null)
			schemaSettings.setSearch(search);
		}
	
	@Override
	public CustomQuery getCustomQuery()
		{
		if (schemaSettings != null)
			return (schemaSettings.getCustomQuery());
		return (null);
		}
	
	@Override
	public boolean isDesignerPreviewMode()
		{
		if (schemaSettings != null)
			return (schemaSettings.isDesignerPreviewMode());
		return (false);
		}

	@Override
	public void setDesignerPreviewMode(boolean b)
		{
		if (schemaSettings != null)
			schemaSettings.setDesignerPreviewMode(b);
		}

	@Override
	public boolean isDesignerCompactMode()
		{
		if (schemaSettings != null)
			return (schemaSettings.isDesignerCompactMode());
		return (false);
		}

	@Override
	public void setDesignerCompactMode(boolean b)
		{
		if (schemaSettings != null)
			schemaSettings.setDesignerCompactMode(b);
		}

	@Override
	public boolean isExpandOtherSchemas()
		{
		if (schemaSettings != null)
			return (schemaSettings.isExpandOtherSchemas());
		return (false);
		}

	@Override
	public void setExpandOtherSchemas(boolean b)
		{
		if (schemaSettings != null)
			schemaSettings.setExpandOtherSchemas(b);
		}
	
	@Override
	public boolean isSortColumns()
		{
		if (schemaSettings != null)
			return (schemaSettings.isSortColumns());
		return (false);
		}

	@Override
	public void setSortColumns(boolean b)
		{
		if (schemaSettings != null)
			schemaSettings.setSortColumns(b);
		}
	
	@Override
	public List<QueryHistoryEntry> getQueryHistory()
		{
		if (schemaSettings != null)
			return (schemaSettings.getQueryHistory());
		return (null);
		}
	
	@Override
	public List<String> getCustomQueryHistory()
		{
		if (schemaSettings != null)
			return (schemaSettings.getCustomQueryHistory());
		return (null);
		}
	
	@Override
	public Map<String, TableFilterEntry> getTableFilters()
		{
		if (schemaSettings != null)
			return (schemaSettings.getTableFilters());
		return (null);
		}
	
	@Override
	public Map<String, Map<String, String>> getQuerySettings()
		{
		if (schemaSettings != null)
			return (schemaSettings.getQuerySettings());
		return (null);
		}
	
	@Override
	public SortedSet<String> getFavorites()
		{
		if (schemaSettings != null)
			return (schemaSettings.getFavorites());
		return (null);
		}
	
	@Override
	public void setFavorites(SortedSet<String> favorites)
		{
		if (schemaSettings != null)
			schemaSettings.setFavorites(favorites);
		}
	
	@Override
	public TableSet getDesign()
		{
		if (schemaSettings != null)
			return (schemaSettings.getDesign());
		return (null);
		}
	
	@Override
	public Map<String, String> getParameterHistory()
		{
		if (schemaSettings != null)
			return (schemaSettings.getParameterHistory());
		return (null);
		}
	
	@Override
	public PathInfo getSourceURL()
		{
		return sourceURL;
		}
	
	@Override
	public void setSourceURL(PathInfo sourceURL)
		{
		this.sourceURL = sourceURL;
		}
	}
