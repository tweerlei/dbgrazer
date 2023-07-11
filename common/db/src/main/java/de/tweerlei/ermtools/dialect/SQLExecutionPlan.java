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
package de.tweerlei.ermtools.dialect;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL statement execution plan
 * 
 * @author Robert Wruck
 */
public class SQLExecutionPlan
	{
	private final List<SQLExecutionPlan> children;
	private int id;
	private String operation;
	private String objectName;
	private String filter;
	private String other;
	private long cost;
	private long rows;
	
	/**
	 * Constructor
	 */
	public SQLExecutionPlan()
		{
		this.children = new ArrayList<SQLExecutionPlan>();
		}

	/**
	 * @return the id
	 */
	public int getId()
		{
		return id;
		}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
		{
		this.id = id;
		}

	/**
	 * @return the operation
	 */
	public String getOperation()
		{
		return operation;
		}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation)
		{
		this.operation = operation;
		}

	/**
	 * @return the objectName
	 */
	public String getObjectName()
		{
		return objectName;
		}

	/**
	 * @param objectName the objectName to set
	 */
	public void setObjectName(String objectName)
		{
		this.objectName = objectName;
		}

	/**
	 * @return the filter
	 */
	public String getFilter()
		{
		return filter;
		}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter)
		{
		this.filter = filter;
		}

	/**
	 * @return the other
	 */
	public String getOther()
		{
		return other;
		}

	/**
	 * @param other the other to set
	 */
	public void setOther(String other)
		{
		this.other = other;
		}

	/**
	 * @return the cost
	 */
	public long getCost()
		{
		return cost;
		}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(long cost)
		{
		this.cost = cost;
		}

	/**
	 * @return the rows
	 */
	public long getRows()
		{
		return rows;
		}

	/**
	 * @param rows the rows to set
	 */
	public void setRows(long rows)
		{
		this.rows = rows;
		}

	/**
	 * @return the children
	 */
	public List<SQLExecutionPlan> getChildren()
		{
		return children;
		}
	
	/**
	 * Get the total size of the plan, including all children
	 * @return Size
	 */
	public int getSize()
		{
		int size = 1;	// this
		for (SQLExecutionPlan c : children)
			size += c.getSize();
		return (size);
		}
	}
