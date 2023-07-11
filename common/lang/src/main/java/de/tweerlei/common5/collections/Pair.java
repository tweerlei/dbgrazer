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
package de.tweerlei.common5.collections;

/**
 * Pair
 * @param <L> left type
 * @param <R> right type
 * 
 * @author Robert Wruck
 */
public class Pair<L, R>
	{
	private final L left;
	private final R right;
	
	/**
	 * Constructor
	 * @param left The left
	 * @param right The right
	 */
	public Pair(L left, R right)
		{
		this.left = left;
		this.right = right;
		}
	
	/**
	 * Get the left
	 * @return left
	 */
	public L getLeft()
		{
		return (left);
		}
	
	/**
	 * Get the left
	 * @return left
	 */
	public L getFirst()
		{
		return (left);
		}
	
	/**
	 * Get the right
	 * @return right
	 */
	public R getRight()
		{
		return (right);
		}
	
	/**
	 * Get the right
	 * @return right
	 */
	public R getSecond()
		{
		return (right);
		}
	
	@Override
	public int hashCode()
		{
		return ((left == null ? 0 : left.hashCode()) ^ (right == null ? 0 : right.hashCode()));
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == null)
			return (false);
		
		if (o instanceof Pair<?, ?>)
			{
			final Pair<?, ?> p = (Pair<?, ?>) o;
			if (left == null)
				{
				if (p.left != null)
					return (false);
				}
			else if (!left.equals(p.left))
				return (false);
			
			if (right == null)
				{
				if (p.right != null)
					return (false);
				}
			else if (!right.equals(p.right))
				return (false);
			
			return (true);
			}
		
		return (false);
		}
	
	@Override
	public String toString()
		{
		return ("(" + left + "," + right + ")");
		}
	}
