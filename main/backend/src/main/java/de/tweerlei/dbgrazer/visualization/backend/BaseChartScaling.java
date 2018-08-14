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
package de.tweerlei.dbgrazer.visualization.backend;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.visualization.service.ChartScaling;

/**
 * Chart type
 * 
 * @author Robert Wruck
 */
public abstract class BaseChartScaling extends NamedBase implements ChartScaling
	{
	private final boolean scaleXLog;
	private final boolean scaleYLog;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param scaleXLog Domain axis scaled
	 * @param scaleYLog Range axis scaled
	 */
	protected BaseChartScaling(String name, boolean scaleXLog, boolean scaleYLog)
		{
		super(name);
		this.scaleXLog = scaleXLog;
		this.scaleYLog = scaleYLog;
		}
	
	@Override
	public final boolean isDomainAxisScaled()
		{
		return scaleXLog;
		}
	
	@Override
	public final boolean isRangeAxisScaled()
		{
		return scaleYLog;
		}
	}
