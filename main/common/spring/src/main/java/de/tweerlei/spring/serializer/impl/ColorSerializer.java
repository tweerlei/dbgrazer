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
package de.tweerlei.spring.serializer.impl;

import java.awt.Color;

import org.springframework.stereotype.Service;

import de.tweerlei.spring.serializer.Serializer;

/**
 * Serializer for Colors
 * 
 * @author Robert Wruck
 */
@Service("colorSerializer")
public class ColorSerializer implements Serializer<Color>
	{
	public Color decode(String value)
		{
		if (value == null)
			return (null);
		
		try	{
			final Long l;
			if (value.startsWith("#"))
				l = Long.valueOf(value.substring(1), 16);
			else
				l = Long.valueOf(value);
			return (new Color(
					(l.intValue() & 0xff0000) >> 16,
					(l.intValue() & 0xff00) >> 8,
					l.intValue() & 0xff
					));			
			}
		catch (NumberFormatException e)
			{
			throw new IllegalArgumentException(e);
			}
		}
	
	public String encode(Color value)
		{
		if (value == null)
			return (null);
		
		return (String.valueOf(value.getRed() << 16 | value.getGreen() << 8 | value.getBlue()));
		}
	
	public Class<Color> getValueType()
		{
		return (Color.class);
		}
	}
