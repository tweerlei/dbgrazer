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
package de.tweerlei.common5.func.predicate;

/**
 * A builder for predicates
 * @param <T> Argument type
 * 
 * @author Robert Wruck
 */
public class PredicateBuilder<T>
	{
	private static class IsNull<T> implements Predicate<T>
		{
		public IsNull()
			{
			}
		
		public boolean evaluate(T o)
			{
			return (o == null);
			}
		}
	
	private static class IsNotNull<T> implements Predicate<T>
		{
		public IsNotNull()
			{
			}
		
		public boolean evaluate(T o)
			{
			return (o != null);
			}
		}
	
	private static class NotPredicate<T> implements Predicate<T>
		{
		private final Predicate<T> l;
		
		public NotPredicate(Predicate<T> l)
			{
			this.l = l;
			}
		
		public boolean evaluate(T o)
			{
			return (!l.evaluate(o));
			}
		}
	
	private static class AndPredicate<T> implements Predicate<T>
		{
		private final Predicate<T> l;
		private final Predicate<T> r;
		
		public AndPredicate(Predicate<T> l, Predicate<T> r)
			{
			this.l = l;
			this.r = r;
			}
		
		public boolean evaluate(T o)
			{
			return (l.evaluate(o) && r.evaluate(o));
			}
		}
	
	private static class OrPredicate<T> implements Predicate<T>
		{
		private final Predicate<T> l;
		private final Predicate<T> r;
		
		public OrPredicate(Predicate<T> l, Predicate<T> r)
			{
			this.l = l;
			this.r = r;
			}
		
		public boolean evaluate(T o)
			{
			return (l.evaluate(o) || r.evaluate(o));
			}
		}
	
	private static class XorPredicate<T> implements Predicate<T>
		{
		private final Predicate<T> l;
		private final Predicate<T> r;
		
		public XorPredicate(Predicate<T> l, Predicate<T> r)
			{
			this.l = l;
			this.r = r;
			}
		
		public boolean evaluate(T o)
			{
			return (l.evaluate(o) ^ r.evaluate(o));
			}
		}
	
	private static class SimplePredicate<T> implements Predicate<T>
		{
		private final boolean value;
		
		public SimplePredicate(boolean value)
			{
			this.value = value;
			}
		
		public boolean evaluate(T o)
			{
			return (value);
			}
		}
	
	private static final SimplePredicate<?> TRUE = new SimplePredicate<Object>(true);
	private static final SimplePredicate<?> FALSE = new SimplePredicate<Object>(false);
	private static final IsNull<?> NULL = new IsNull<Object>();
	private static final IsNotNull<?> NOTNULL = new IsNotNull<Object>();
	
	
	private Predicate<T> predicate;
	
	private PredicateBuilder(Predicate<T> predicate)
		{
		this.predicate = predicate;
		}
	
	/**
	 * Get the built predicate
	 * @return Predicate
	 */
	public Predicate<T> getPredicate()
		{
		return (predicate);
		}
	
	/**
	 * Create an instance
	 * @param <T> Argument type
	 * @param predicate Initial predicate
	 * @return A builder instance
	 */
	public static <T> PredicateBuilder<T> of(Predicate<T> predicate)
		{
		return (new PredicateBuilder<T>(predicate));
		}
	
	/**
	 * Create a predicate that always applies
	 * @param <T> Argument type
	 * @param type Argument type class
	 * @return Instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysTrue(Class<T> type)
		{
		return ((Predicate<T>) TRUE);
		}
	
	/**
	 * Create a predicate that never applies
	 * @param <T> Argument type
	 * @param type Argument type class
	 * @return Instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysFalse(Class<T> type)
		{
		return ((Predicate<T>) FALSE);
		}
	
	/**
	 * Create a typed IsEqual instance
	 * @param <T> Argument type
	 * @param value Value to test for
	 * @return Instance
	 */
	public static <T> IsEqual<T> isEqualTo(T value)
		{
		return (new IsEqual<T>(value));
		}
	
	/**
	 * Create a typed IsSame instance
	 * @param <T> Argument type
	 * @param value Value to test for
	 * @return Instance
	 */
	public static <T> IsSame<T> isSameAs(T value)
		{
		return (new IsSame<T>(value));
		}
	
	/**
	 * Create a typed IsNotNull instance
	 * @param <T> Argument type
	 * @return Instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> IsNull<T> isNull()
		{
		return ((IsNull<T>) NULL);
		}
	
	/**
	 * Create a typed IsNotNull instance
	 * @param <T> Argument type
	 * @return Instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> IsNotNull<T> isNotNull()
		{
		return ((IsNotNull<T>) NOTNULL);
		}
	
	/**
	 * Negate the predicate
	 * @return PredicateBuilder
	 */
	public PredicateBuilder<T> negate()
		{
		predicate = new NotPredicate<T>(predicate);
		return (this);
		}
	
	/**
	 * AND the current predicate with the argument
	 * @param p Predicate
	 * @return PredicateBuilder
	 */
	public PredicateBuilder<T> and(Predicate<T> p)
		{
		predicate = new AndPredicate<T>(predicate, p);
		return (this);
		}
	
	/**
	 * OR the current predicate with the argument
	 * @param p Predicate
	 * @return PredicateBuilder
	 */
	public PredicateBuilder<T> or(Predicate<T> p)
		{
		predicate = new OrPredicate<T>(predicate, p);
		return (this);
		}
	
	/**
	 * XOR the current predicate with the argument
	 * @param p Predicate
	 * @return PredicateBuilder
	 */
	public PredicateBuilder<T> xor(Predicate<T> p)
		{
		predicate = new XorPredicate<T>(predicate, p);
		return (this);
		}
	}
