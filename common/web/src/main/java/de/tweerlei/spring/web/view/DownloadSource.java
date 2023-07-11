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

import de.tweerlei.common.io.StreamWriter;

/**
 * Delegate for the GenericDownloadView
 * 
 * @author Robert Wruck
 */
public interface DownloadSource extends StreamWriter
	{
	/** Expire time: never */
	public static final Integer NEVER = Integer.valueOf(-1);
	/** Expire time: always */
	public static final Integer ALWAYS = Integer.valueOf(-2);
	
	/**
	 * Get the content type.
	 * @return Content type
	 */
	public String getContentType();
	
	/**
	 * Get the file name.
	 * If this returns non-null, a Content-Disposition header will be generated
	 * with the returned file name. Otherwise, no such header will be sent.
	 * @return File name or null
	 */
	public String getFileName();
	
	/**
	 * Check whether the content should be sent as attachment.
	 * Only used if getFileName returns non-null.
	 * @return true for attachment, false for inline content
	 */
	public boolean isAttachment();
	
	/**
	 * Check whether this DownloadSource is valid.
	 * If not, a 404 response will be sent.
	 * @return boolean
	 */
	public boolean canRead();
	
	/**
	 * Get the timestamp of last modification.
	 * If not modified, a 304 header will be sent.
	 * @return Timestamp or 0
	 */
	public long getLastModified();
	
	/**
	 * Get the content length if known.
	 * @return Length or -1
	 */
	public int getContentLength();
	
	/**
	 * Check whether the content should be buffered to determine the content length.
	 * Only used if getContentLength returns -1.
	 * @return true for buffering
	 */
	public boolean isBuffered();
	
	/**
	 * Get the expireTime (in seconds from now).
	 * Use NEVER for resources that should never expire.
	 * Use ALWAYS for resources that should always expire.
	 * @return the expireTime or null
	 */
	public Integer getExpireTime();
	}
