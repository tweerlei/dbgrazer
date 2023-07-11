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
package de.tweerlei.dbgrazer.extension.json.parser;

import java.util.LinkedList;

/**
 * Consume XML tokens, counting tag levels
 * 
 * @author Robert Wruck
 */
public class JSONConsumer
	{
	private static enum State
		{
		INITIAL,
		ARRAY,
		OBJECT_KEY,
		OBJECT_VALUE
		}
	
	private final JSONHandler handler;
	private final LinkedList<State> stateStack;
	private boolean expectValue;
	private boolean elementSeen;
	
	/**
	 * Constructor
	 * @param handler Tag handler
	 */
	public JSONConsumer(JSONHandler handler)
		{
		this.handler = handler;
		this.stateStack = new LinkedList<State>();
		this.stateStack.push(State.INITIAL);
		this.expectValue = true;
		this.elementSeen = false;
		}
	
	private int currentLevel()
		{
		return (stateStack.size() - 1);
		}
	
	private State currentState()
		{
		return (stateStack.peek());
		}
	
	private void push(State state)
		{
		stateStack.push(state);
		}
	
	private State pop()
		{
		return (stateStack.pop());
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public JSONConsumer appendString(String tag)
		{
		if (!expectValue)
			throw new IllegalStateException("Separator expected");
		if (currentState() == State.OBJECT_KEY)
			handler.handleKey(tag, currentLevel());
		else
			handler.handleString(tag, currentLevel());
		expectValue = false;
		elementSeen = true;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public JSONConsumer appendNumber(String tag)
		{
		if (!expectValue)
			throw new IllegalStateException("Separator expected");
		if (currentState() == State.OBJECT_KEY)
			throw new IllegalStateException("Number not allowed here");
		handler.handleNumber(tag, currentLevel());
		expectValue = false;
		elementSeen = true;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public JSONConsumer appendName(String text)
		{
		if (!expectValue)
			throw new IllegalStateException("Separator expected");
		if (currentState() == State.OBJECT_KEY)
			throw new IllegalStateException("Name not allowed here");
		handler.handleName(text, currentLevel());
		expectValue = false;
		elementSeen = true;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer startObject()
		{
		if (!expectValue)
			throw new IllegalStateException("Separator expected");
		if (currentState() == State.OBJECT_KEY)
			throw new IllegalStateException("Object not allowed here");
		handler.startObject(currentLevel());
		push(State.OBJECT_KEY);
		elementSeen = false;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer endObject()
		{
		if (expectValue && elementSeen)
			throw new IllegalStateException("Value expected");
		final State lastState = pop();
		if ((lastState != State.OBJECT_KEY) && (lastState != State.OBJECT_VALUE))
			throw new IllegalStateException("Unmatched closing brace");
		
		handler.endObject(currentLevel());
		elementSeen = true;
		expectValue = false;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer startArray()
		{
		if (!expectValue)
			throw new IllegalStateException("Separator expected");
		if (currentState() == State.OBJECT_KEY)
			throw new IllegalStateException("Array not allowed here");
		handler.startArray(currentLevel());
		push(State.ARRAY);
		elementSeen = false;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer endArray()
		{
		if (expectValue && elementSeen)
			throw new IllegalStateException("Value expected");
		final State lastState = pop();
		if (lastState != State.ARRAY)
			throw new IllegalStateException("Unmatched closing bracket");
		
		handler.endArray(currentLevel());
		elementSeen = true;
		expectValue = false;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer appendKeySeparator()
		{
		if (expectValue)
			throw new IllegalStateException("Value expected");
		switch (currentState())
			{
			case OBJECT_KEY:
				pop();
				push(State.OBJECT_VALUE);
				break;
			default:
				throw new IllegalStateException("Unexpected separator");
			}
		handler.handleKeySeparator(currentLevel());
		expectValue = true;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @return this
	 */
	public JSONConsumer appendValueSeparator()
		{
		if (expectValue)
			throw new IllegalStateException("Value expected");
		switch (currentState())
			{
			case OBJECT_VALUE:
				pop();
				push(State.OBJECT_KEY);
				break;
			case ARRAY:
				break;
			default:
				throw new IllegalStateException("Unexpected separator");
			}
		handler.handleValueSeparator(currentLevel());
		expectValue = true;
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public JSONConsumer appendComment(String text)
		{
		handler.handleComment(text, currentLevel());
		return (this);
		}
	
	/**
	 * Handle explicit whitespace
	 * @param text Whitespace
	 * @return this
	 */
	public JSONConsumer appendSpace(String text)
		{
		handler.handleSpace(text, currentLevel());
		return (this);
		}
	
	/**
	 * Finish processing
	 * @return this
	 */
	public JSONConsumer finish()
		{
//		if (expectValue)
//			throw new IllegalStateException("Value expected");
		if (currentState() != State.INITIAL)
			throw new IllegalStateException("Unmatched opening brace");
		return (this);
		}
	
	@Override
	public String toString()
		{
		return (handler.toString());
		}
	}
