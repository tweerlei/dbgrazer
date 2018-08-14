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
package de.tweerlei.spring.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.jar.Manifest;

import de.tweerlei.common.util.DateUtils;

/**
 * Read well-known properties from a Manifest
 * 
 * @author Robert Wruck
 */
public class ManifestParser
	{
	private static final String GROUPID_ATTRIBUTE = "GroupID";
	private static final String ARTIFACTID_ATTRIBUTE = "ArtifactID";
	private static final String VERSION_ATTRIBUTE = "Version";
	private static final String BRANCH_ATTRIBUTE = "Branch";
	private static final String REVISION_ATTRIBUTE = "Revision";
	private static final String DATE_ATTRIBUTE = "Date";
	
	/** The value for unknown attributes */
	public static final String UNKNOWN_VALUE = "?";
	private static final Date UNKNOWN_DATE = new Date();
	
	private final Manifest manifest;
	private String groupId;
	private String artifactId;
	private String version;
	private String branch;
	private String revision;
	private Date date;
	
	/**
	 * Constructor
	 * @param manifest Manifest
	 */
	public ManifestParser(Manifest manifest)
		{
		this.manifest = manifest;
		}
	
	/**
	 * Constructor
	 * @param is InputStream
	 */
	public ManifestParser(InputStream is)
		{
		Manifest mf = null;
		if (is != null)
			{
			try	{
				mf = new Manifest(is);
				}
			catch (IOException e)
				{
				// invalid manifest - ignore
				}
			}
		this.manifest = mf;
		}
	
	/**
	 * Get the manifest
	 * @return Manifest (may be null)
	 */
	public Manifest getManifest()
		{
		return (manifest);
		}
	
	private String getAttribute(String attr)
		{
		if (manifest == null)
			return (UNKNOWN_VALUE);
		
		final String value = manifest.getMainAttributes().getValue(attr);
		if (value == null)
			return (UNKNOWN_VALUE);
		
		return (value);
		}
	
	/**
	 * Get the maven groupId
	 * @return groupId
	 */
	public String getGroupId()
		{
		if (groupId == null)
			groupId = getAttribute(GROUPID_ATTRIBUTE);
		return (groupId);
		}
	
	/**
	 * Get the maven artifactId
	 * @return artifactId
	 */
	public String getArtifactId()
		{
		if (artifactId == null)
			artifactId = getAttribute(ARTIFACTID_ATTRIBUTE);
		return (artifactId);
		}
	
	/**
	 * Get the maven version
	 * @return version
	 */
	public String getVersion()
		{
		if (version == null)
			version = getAttribute(VERSION_ATTRIBUTE);
		return (version);
		}
	
	/**
	 * Get the source branch
	 * @return Branch name
	 */
	public String getBranch()
		{
		if (branch == null)
			branch = getAttribute(BRANCH_ATTRIBUTE);
		return (branch);
		}
	
	/**
	 * Get the source revision
	 * @return Revision
	 */
	public String getRevision()
		{
		if (revision == null)
			revision = getAttribute(REVISION_ATTRIBUTE);
		return (revision);
		}
	
	/**
	 * Get the build date
	 * @return Date
	 */
	public Date getDate()
		{
		if (date == null)
			{
			final String builtOn = getAttribute(DATE_ATTRIBUTE);
			try	{
				date = DateUtils.parseDate(builtOn, DateUtils.DATETIME_ISO8601);
				}
			catch (ParseException e)
				{
				// invalid date - ignore
				}
			if (date == null)
				date = new Date(UNKNOWN_DATE.getTime());
			}
		return (date);
		}
	}
