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
package de.tweerlei.common.state;

import java.util.HashSet;

import de.tweerlei.common.contract.ContractProof;

/**
 * Zustandsautomat
 * 
 * @author Robert Wruck
 */
public class FiniteStateMachine
	{
	private final int max_state;
	private int current_state;
	private HashSet[] transitions;
	
	/** Initialzustand */
	public static final int INITIAL = 0;
	
	/**
	 * Konstruktor
	 * @param num Anzahl der Zustände
	 */
	public FiniteStateMachine(int num)
		{
		ContractProof.greaterThan("num", num, 0);
		max_state = num;
		current_state = INITIAL;
		transitions = new HashSet[max_state];
		}
	
	/**
	 * Liefert den aktuellen Zustand
	 * @return Zustand
	 */
	public final int getState()
		{
		return (current_state);
		}
	
	/**
	 * Erlaubt den Übergang von from nach to
	 * @param from Ausgangszustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn from oder to einen nicht vorhandenen Zustand angeben
	 */
	public void enableTransition(int from, int to) throws InvalidStateException
		{
		validateState(from);
		validateState(to);
		if (transitions[from] == null)
			transitions[from] = new HashSet();
		transitions[from].add(new Integer(to));
		}
	
	/**
	 * Erlaubt den Übergang von from nach to UND von to nach from
	 * @param from Ausgangszustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn from oder to einen nicht vorhandenen Zustand angeben
	 */
	public final void enableTransitions(int from, int to) throws InvalidStateException
		{
		enableTransition(from, to);
		enableTransition(to, from);
		}
	
	/**
	 * Verbietet den Übergang von from nach to
	 * @param from Ausgangszustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn from oder to einen nicht vorhandenen Zustand angeben
	 */
	public void disableTransition(int from, int to) throws InvalidStateException
		{
		validateState(from);
		validateState(to);
		if (transitions[from] != null)
			transitions[from].remove(new Integer(to));
		}
	
	/**
	 * Erlaubt den Übergang von from nach to UND von to nach from
	 * @param from Ausgangszustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn from oder to einen nicht vorhandenen Zustand angeben
	 */
	public final void disableTransitions(int from, int to) throws InvalidStateException
		{
		disableTransition(from, to);
		disableTransition(to, from);
		}
	
	/**
	 * Ermittelt, ob ein Übergang von from nach to erlaubt ist
	 * @param from Ausgangszustand
	 * @param to Zielzustand
	 * @return true, wenn erlaubt
	 * @throws InvalidStateException Wenn from oder to einen nicht vorhandenen Zustand angeben
	 */
	public boolean isTransitionValid(int from, int to) throws InvalidStateException
		{
		validateState(from);
		validateState(to);
		return ((transitions[from] != null) && transitions[from].contains(new Integer(to)));
		}
	
	/**
	 * Wirft eine Exception, wenn s kein gültiger Zustand ist
	 * @param s Zustand
	 * @throws InvalidStateException Wenn s kein gültiger Zustand ist
	 */
	protected void validateState(int s) throws InvalidStateException
		{
		if ((s < 0) || (s >= max_state))
			throw new InvalidStateException(s);
		}
	
	/**
	 * Wechselt den aktuellen Zustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn to kein gültiger Zustand ist
	 * @throws InvalidTransitionException Wenn der Übergang nach to nicht erlaubt ist
	 */
	public final void transit(int to) throws InvalidStateException, InvalidTransitionException
		{
		if (!isTransitionValid(current_state, to))
			throw new InvalidTransitionException(current_state, to);
		
		current_state = to;
		}
	}
