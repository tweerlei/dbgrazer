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
package de.tweerlei.spring.config.impl;

import de.tweerlei.spring.config.ConfigKey;
import de.tweerlei.spring.config.WritableConfigAccessor;

/**
 * Base class for WritableConfigAccessor implementations providing the put method
 * 
 * @author Robert Wruck
 */
public abstract class AbstractWritableConfigAccessor extends AbstractConfigAccessor implements WritableConfigAccessor
	{
	public <T> T put(ConfigKey<T> key, T value)
		{
		final T ret = putRaw(key, value);
		if (ret == null)
			return (key.getDefaultValue());
		return (ret);
		}
	}
