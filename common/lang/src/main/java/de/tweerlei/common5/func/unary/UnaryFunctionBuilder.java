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
package de.tweerlei.common5.func.unary;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import de.tweerlei.common5.func.binary.BinaryFunction;
import de.tweerlei.common5.func.binary.BinaryFunctionBuilder;

/**
 * Builder for UnaryFunctions
 * @param <I> Argument type
 * @param <O> Return type
 * 
 * @author Robert Wruck
 */
public class UnaryFunctionBuilder<I, O>
	{
	private static class ChainFunction<I, M, O> implements UnaryFunction<I, O>
		{
		private final UnaryFunction<I, M> f1;
		private final UnaryFunction<M, O> f2;
		
		public ChainFunction(UnaryFunction<I, M> f1, UnaryFunction<M, O> f2)
			{
			this.f1 = f1;
			this.f2 = f2;
			}
		
		public O applyTo(I i)
			{
			return (f2.applyTo(f1.applyTo(i)));
			}
		}
	
	private UnaryFunction<?, ?> func;
	
	private UnaryFunctionBuilder(UnaryFunction<I, O> func)
		{
		this.func = func;
		}
	
	/**
	 * Get the function
	 * @return BinaryFunction
	 */
	@SuppressWarnings("unchecked")
	public UnaryFunction<I, O> getFunction()
		{
		return ((UnaryFunction<I, O>) func);
		}
	
	/**
	 * Create an instance
	 * @param <I> Argument type
	 * @param <O> Return type
	 * @param func Initial function
	 * @return UnaryFunctionBuilder
	 */
	public static <I, O> UnaryFunctionBuilder<I, O> of(UnaryFunction<I, O> func)
		{
		return (new UnaryFunctionBuilder<I, O>(func));
		}
	
	/**
	 * Create a new UnaryFunction that operates on the result of this one
	 * @param <O2> Result type of the chained function
	 * @param f Function to chain
	 * @return UnaryFunctionBuilder
	 */
	@SuppressWarnings("unchecked")
	public <O2> UnaryFunctionBuilder<I, O2> chain(UnaryFunction<O, O2> f)
		{
		func = new ChainFunction<I, O, O2>(getFunction(), f);
		
		return ((UnaryFunctionBuilder<I, O2>) this);
		}
	
	/**
	 * Create a new UnaryFunction that operates on the result of this one
	 * @param <I2> Result type of the chained function
	 * @param f Function to chain
	 * @return UnaryFunctionBuilder
	 */
	@SuppressWarnings("unchecked")
	public <I2> UnaryFunctionBuilder<I2, O> filter(UnaryFunction<I2, I> f)
		{
		func = new ChainFunction<I2, I, O>(f, getFunction());
		
		return ((UnaryFunctionBuilder<I2, O>) this);
		}
	}
