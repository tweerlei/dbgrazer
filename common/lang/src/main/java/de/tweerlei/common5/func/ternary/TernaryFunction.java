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

/**
 * Binary function
 * @param <L> Type of first argument
 * @param <M> Type of second argument
 * @param <R> Type of third argument
 * @param <O> Return type
 * 
 * @author Robert Wruck
 */
public interface TernaryFunction<L, M, R, O>
	{
	/**
	 * Apply the function
	 * @param l First argument
	 * @param m second argument
	 * @param r third argument
	 * @return Result
	 */
	public O applyTo(L l, M m, R r);
	}
