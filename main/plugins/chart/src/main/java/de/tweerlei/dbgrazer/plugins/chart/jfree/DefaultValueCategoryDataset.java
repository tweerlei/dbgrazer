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
package de.tweerlei.dbgrazer.plugins.chart.jfree;

import org.jfree.data.category.DefaultCategoryDataset;

/**
 * CategoryDataset that returns a default value for missing entries
 * 
 * @author Robert Wruck
 */
public class DefaultValueCategoryDataset extends DefaultCategoryDataset
	{
	private final Number defaultValue;
	
	/**
	 * Constructor
	 * @param defaultValue The default value
	 */
	public DefaultValueCategoryDataset(Number defaultValue)
		{
		this.defaultValue = defaultValue;
		}
	
	@Override
	public Number getValue(int row, int column)
		{
		final Number ret = super.getValue(row, column);
		if (ret == null)
			return (defaultValue);
		return (ret);
		}
	
	@Override
	public Number getValue(@SuppressWarnings("rawtypes") Comparable rowKey, @SuppressWarnings("rawtypes") Comparable columnKey)
		{
		final Number ret = super.getValue(rowKey, columnKey);
		if (ret == null)
			return (defaultValue);
		return (ret);
		}
	}
