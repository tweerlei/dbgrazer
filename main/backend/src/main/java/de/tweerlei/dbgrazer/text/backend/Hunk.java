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
package de.tweerlei.dbgrazer.text.backend;

/**
 * A single block of differing lines
 * 
 * @author Robert Wruck
 */
public class Hunk
	{
	private final int lstart;
	private final int lend;
	private final int rstart;
	private final int rend;
	
	/**
	 * Constructor
	 * @param lstart Start line number in LHS
	 * @param lend End line number in LHS
	 * @param rstart Start line number in RHS
	 * @param rend End line number in RHS
	 */
	public Hunk(int lstart, int lend, int rstart, int rend)
		{
		this.lstart = lstart;
		this.lend = lend;
		this.rstart = rstart;
		this.rend = rend;
		}
	
	/**
	 * Get the left start
	 * @return the lstart
	 */
	public int getLeftStart()
		{
		return lstart;
		}
	
	/**
	 * Get the left end
	 * @return the lend
	 */
	public int getLeftEnd()
		{
		return lend;
		}

	/**
	 * Get the right start
	 * @return the rstart
	 */
	public int getRightStart()
		{
		return rstart;
		}

	/**
	 * Get the right end
	 * @return the rend
	 */
	public int getRightEnd()
		{
		return rend;
		}

	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + lend;
		result = prime * result + lstart;
		result = prime * result + rend;
		result = prime * result + rstart;
		return result;
		}

	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hunk other = (Hunk) obj;
		if (lend != other.lend)
			return false;
		if (lstart != other.lstart)
			return false;
		if (rend != other.rend)
			return false;
		if (rstart != other.rstart)
			return false;
		return true;
		}
	
	@Override
	public String toString()
		{
		return (lstart + "," + (lend - lstart) + " " + rstart + "," + (rend - rstart));
		}
	}
