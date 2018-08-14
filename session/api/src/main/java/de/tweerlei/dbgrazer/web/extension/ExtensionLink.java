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
package de.tweerlei.dbgrazer.web.extension;

/**
 * Extension definition
 * 
 * @author Robert Wruck
 */
public class ExtensionLink
	{
	private final String label;
	private final String href;
	private final String onclick;
	private final String title;
	
	/**
	 * Constructor
	 * @param label The label (message key)
	 * @param href The href
	 * @param onclick The onclick
	 * @param title The title (message key)
	 */
	public ExtensionLink(String label, String href, String onclick, String title)
		{
		this.label = label;
		this.href = href;
		this.onclick = onclick;
		this.title = title;
		}
	
	/**
	 * @return the label
	 */
	public String getLabel()
		{
		return label;
		}
	
	/**
	 * @return the href
	 */
	public String getHref()
		{
		return href;
		}
	
	/**
	 * @return the onclick
	 */
	public String getOnclick()
		{
		return onclick;
		}
	
	/**
	 * @return the title
	 */
	public String getTitle()
		{
		return title;
		}
	}
