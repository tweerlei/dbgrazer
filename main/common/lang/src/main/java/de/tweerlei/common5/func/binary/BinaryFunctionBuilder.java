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
package de.tweerlei.common5.func.binary;

import de.tweerlei.common5.func.unary.UnaryFunction;

/**
 * Builder for BinaryFunctions
 * @param <L> Type of first argument
 * @param <R> Type of second argument
 * @param <O> Return type
 * 
 * @author Robert Wruck
 */
public class BinaryFunctionBuilder<L, R, O>
	{
	private static class ChainFunction<L, R, O, O2> implements BinaryFunction<L, R, O2>
		{
		private final BinaryFunction<L, R, O> g;
		private final UnaryFunction<O, O2> f;
		
		public ChainFunction(BinaryFunction<L, R, O> g, UnaryFunction<O, O2> f)
			{
			this.f = f;
			this.g = g;
			}
		
		public O2 applyTo(L l, R r)
			{
			return (f.applyTo(g.applyTo(l, r)));
			}
		}
	
	private BinaryFunction<?, ?, ?> func;
	
	private BinaryFunctionBuilder(BinaryFunction<L, R, O> func)
		{
		this.func = func;
		}
	
	/**
	 * Get the function
	 * @return BinaryFunction
	 */
	@SuppressWarnings("unchecked")
	public BinaryFunction<L, R, O> getFunction()
		{
		return ((BinaryFunction<L, R, O>) func);
		}
	
	/**
	 * Create an instance
	 * @param <L> Type of first argument
	 * @param <R> Type of second argument
	 * @param <O> Return type
	 * @param func Initial function
	 * @return BinaryFunctionBuilder
	 */
	public static <L, R, O> BinaryFunctionBuilder<L, R, O> of(BinaryFunction<L, R, O> func)
		{
		return (new BinaryFunctionBuilder<L, R, O>(func));
		}
	
	/**
	 * Create a new BinaryFunction that operates on the result of this one
	 * @param <O2> Result type of the chained function
	 * @param f Function to chain
	 * @return BinaryFunctionBuilder
	 */
	@SuppressWarnings("unchecked")
	public <O2> BinaryFunctionBuilder<L, R, O2> chain(UnaryFunction<O, O2> f)
		{
		func = new ChainFunction<L, R, O, O2>(getFunction(), f);
		
		return ((BinaryFunctionBuilder<L, R, O2>) this);
		}
	}
