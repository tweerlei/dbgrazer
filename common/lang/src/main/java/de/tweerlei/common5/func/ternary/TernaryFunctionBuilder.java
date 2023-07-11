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
package de.tweerlei.common5.func.ternary;

import de.tweerlei.common5.func.unary.UnaryFunction;

/**
 * Builder for BinaryFunctions
 * @param <L> Type of first argument
 * @param <M> Type of second argument
 * @param <R> Type of third argument
 * @param <O> Return type
 * 
 * @author Robert Wruck
 */
public class TernaryFunctionBuilder<L, M, R, O>
	{
	private static class ChainFunction<L, M, R, O, O2> implements TernaryFunction<L, M, R, O2>
		{
		private final TernaryFunction<L, M, R, O> g;
		private final UnaryFunction<O, O2> f;
		
		public ChainFunction(TernaryFunction<L, M, R, O> g, UnaryFunction<O, O2> f)
			{
			this.f = f;
			this.g = g;
			}
		
		public O2 applyTo(L l, M m, R r)
			{
			return (f.applyTo(g.applyTo(l, m, r)));
			}
		}
	
	private TernaryFunction<?, ?, ?, ?> func;
	
	private TernaryFunctionBuilder(TernaryFunction<L, M, R, O> func)
		{
		this.func = func;
		}
	
	/**
	 * Get the function
	 * @return TernaryFunction
	 */
	@SuppressWarnings("unchecked")
	public TernaryFunction<L, M, R, O> getFunction()
		{
		return ((TernaryFunction<L, M, R, O>) func);
		}
	
	/**
	 * Create an instance
	 * @param <L> Type of first argument
	 * @param <M> Type of second argument
	 * @param <R> Type of third argument
	 * @param <O> Return type
	 * @param func Initial function
	 * @return TernaryFunctionBuilder
	 */
	public static <L, M, R, O> TernaryFunctionBuilder<L, M, R, O> of(TernaryFunction<L, M, R, O> func)
		{
		return (new TernaryFunctionBuilder<L, M, R, O>(func));
		}
	
	/**
	 * Create a new BinaryFunction that operates on the result of this one
	 * @param <O2> Result type of the chained function
	 * @param f Function to chain
	 * @return BinaryFunctionBuilder
	 */
	@SuppressWarnings("unchecked")
	public <O2> TernaryFunctionBuilder<L, M, R, O2> chain(UnaryFunction<O, O2> f)
		{
		func = new ChainFunction<L, M, R, O, O2>(getFunction(), f);
		
		return ((TernaryFunctionBuilder<L, M, R, O2>) this);
		}
	}
