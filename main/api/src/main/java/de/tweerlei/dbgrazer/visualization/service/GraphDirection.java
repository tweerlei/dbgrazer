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
package de.tweerlei.dbgrazer.visualization.service;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Graph direction
 * 
 * @author Robert Wruck
 */
public enum GraphDirection implements Named
	{
	/** Undirected */
	UNDIRECTED,
	/** Left to right */
	LEFT_TO_RIGHT,
	/** Right to left */
	RIGHT_TO_LEFT,
	/** Top to bottom */
	TOP_TO_BOTTOM,
	/** Bottom to top */
	BOTTOM_TO_TOP;
	
	@Override
	public String getName()
		{
		return (name());
		}
	}
