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
package de.tweerlei.spring.web.view;

/**
 * Abstract base class that provides default implementations of common methods
 * 
 * @author Robert Wruck
 */
public abstract class AbstractDownloadSource implements DownloadSource
	{
	private String fileName;
	private boolean attachment;
	private Integer expireTime;
	private boolean buffered;
	
	/**
	 * Set the fileName
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName)
		{
		this.fileName = fileName;
		}
	
	/**
	 * Set whether the content will be presented as inline or attachment.
	 * Requires the file name to be set
	 * @param b true for attachment
	 */
	public void setAttachment(boolean b)
		{
		this.attachment = b;
		}
	
	/**
	 * Set the expireTime (in seconds from now).
	 * Use NEVER for resources that should never expire.
	 * Use ALWAYS for resources that should always expire.
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(Integer expireTime)
		{
		this.expireTime = expireTime;
		}
	
	public String getFileName()
		{
		return (fileName);
		}
	
	/**
	 * Set whether the content should be buffered to determine the content length.
	 * @param b true for attachment
	 */
	public void setBuffered(boolean b)
		{
		this.buffered = b;
		}
	
	public boolean isAttachment()
		{
		return (attachment);
		}
	
	public boolean canRead()
		{
		return (true);
		}
	
	public long getLastModified()
		{
		return (-1L);
		}
	
	public int getContentLength()
		{
		return (-1);
		}
	
	public boolean isBuffered()
		{
		return (buffered);
		}
	
	public Integer getExpireTime()
		{
		return (expireTime);
		}
	}
