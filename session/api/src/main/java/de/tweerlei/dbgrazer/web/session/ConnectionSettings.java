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
package de.tweerlei.dbgrazer.web.session;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.web.model.PathInfo;

/**
 * Connection settings for the current request
 * 
 * @author Robert Wruck
 */
public interface ConnectionSettings extends SchemaSettings
	{
	/**
	 * Get the link name
	 * @return Link name
	 */
	public String getLinkName();
	
	/**
	 * Set the link name
	 * @param link Link definition
	 * @param schemaSettings Schema settings
	 */
	public void setLink(LinkDef link, SchemaSettings schemaSettings);
	
	/**
	 * Reset the link name
	 */
	public void resetLink();
	
	/**
	 * Get the link type
	 * @return the link type
	 */
	public LinkType getType();
	
	/**
	 * Get the link description
	 * @return Link description
	 */
	public String getDescription();
	
	/**
	 * Get the link schema name
	 * @return Link schema name
	 */
	public String getSchemaName();
	
	/**
	 * Get the link schema version
	 * @return Link schema version
	 */
	public String getSchemaVersion();
	
	/**
	 * Get the link dialect name
	 * @return Link dialect name
	 */
	public String getDialectName();
	
	/**
	 * Get the groupName
	 * @return groupName
	 */
	public String getGroupName();
	
	/**
	 * Get the writable flag
	 * @return writable flag
	 */
	public boolean isWritable();
	
	/**
	 * Check whether the editor is enabled
	 * @return Editor enabled
	 */
	public boolean isEditorEnabled();
	
	/**
	 * Set editor enabled
	 * @param b Editor enabled
	 */
	public void setEditorEnabled(boolean b);
	
	/**
	 * Check whether query submission is enabled
	 * @return query submission enabled
	 */
	public boolean isSubmitEnabled();
	
	/**
	 * Set query submission enabled
	 * @param b query submission enabled
	 */
	public void setSubmitEnabled(boolean b);
	
	/**
	 * Check whether the editor is active
	 * @return Editor active
	 */
	public boolean isEditorActive();
	
	/**
	 * Set editor active
	 * @param b Editor active
	 */
	public void setEditorActive(boolean b);
	
	/**
	 * Check whether the browser is enabled
	 * @return Browser enabled
	 */
	public boolean isBrowserEnabled();
	
	/**
	 * Set browser enabled
	 * @param b Browser enabled
	 */
	public void setBrowserEnabled(boolean b);
	
	/**
	 * Check whether the designer is enabled
	 * @return Designer enabled
	 */
	public boolean isDesignerEnabled();
	
	/**
	 * Set designer enabled
	 * @param b Designer enabled
	 */
	public void setDesignerEnabled(boolean b);
	
	/**
	 * Check whether the REST API is enabled
	 * @return REST API enabled
	 */
	public boolean isWsApiEnabled();
	
	/**
	 * Set REST API enabled
	 * @param b REST API enabled
	 */
	public void setWsApiEnabled(boolean b);
	
	/**
	 * Check whether the editor is enabled
	 * @return Editor enabled
	 */
	public boolean isRecordEditorEnabled();
	
	/**
	 * Set editor enabled
	 * @param b Editor enabled
	 */
	public void setRecordEditorEnabled(boolean b);
	
	/**
	 * Get the source URL
	 * @return Source URL
	 */
	public PathInfo getSourceURL();
	
	/**
	 * Set the source URL
	 * @param sourceURL Source URL
	 */
	public void setSourceURL(PathInfo sourceURL);
	}
