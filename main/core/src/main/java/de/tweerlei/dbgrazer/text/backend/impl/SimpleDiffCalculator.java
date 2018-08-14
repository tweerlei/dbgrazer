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
package de.tweerlei.dbgrazer.text.backend.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.text.backend.DiffCalculator;
import de.tweerlei.dbgrazer.text.backend.Hunk;

/**
 * Trivial implementation that just ignores leading and trailing matching lines
 * 
 * @author Robert Wruck
 */
@Service("simpleDiffCalculator")
public class SimpleDiffCalculator implements DiffCalculator
	{
	@Override
	public List<Hunk> diff(List<String> l, List<String> r)
		{
		final List<Hunk> ret = new ArrayList<Hunk>(1);
		
		final int n = l.size();
		final int m = r.size();
		final int c = Math.min(n, m);
		
		int lower;
		for (lower = 0; lower < c; lower++)
			{
			if (!l.get(lower).equals(r.get(lower)))
				break;
			}
		
		int upper;
		for (upper = 1; upper <= c - lower; upper++)
			{
			if (!l.get(n - upper).equals(r.get(m - upper)))
				break;
			}
		
		if ((n - upper >= lower) || (m - upper >= lower))
			ret.add(new Hunk(lower, n - upper + 1, lower, m - upper + 1));
		
		return (ret);
		}
	}
