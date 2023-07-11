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

import java.util.Stack;

/**
 * Zustandsautomat mit Vergangenheit
 * 
 * @author Robert Wruck
 */
public class FiniteStateMachineHistory
	{
	private final FiniteStateMachine fsm;
	private final Stack hist;
	
	/**
	 * Konstruktor
	 * @param f FiniteStateMachine
	 */
	public FiniteStateMachineHistory(FiniteStateMachine f)
		{
		fsm = f;
		hist = new Stack();
		}
	
	/**
	 * Liefert den aktuellen Zustand
	 * @return Zustand
	 */
	public int getState()
		{
		return (fsm.getState());
		}
	
	/**
	 * Wechselt den aktuellen Zustand
	 * @param to Zielzustand
	 * @throws InvalidStateException Wenn to kein gültiger Zustand ist
	 * @throws InvalidTransitionException Wenn der Übergang nach to nicht erlaubt ist
	 */
	public void transit(int to) throws InvalidStateException, InvalidTransitionException
		{
		hist.push(new Integer(getState()));
		fsm.transit(to);
		}
	
	/**
	 * Wechselt zum vorherigen Zustand
	 * @throws InvalidStateException Kommt nicht vor
	 * @throws InvalidTransitionException Wenn der Übergang zurück nicht erlaubt ist
	 */
	public void back() throws InvalidStateException, InvalidTransitionException
		{
		int to = ((Integer) hist.pop()).intValue();
		fsm.transit(to);
		}
	
	/**
	 * Ermittelt, ob es einen vorherigen Zustand gibt
	 * @return true, falls nein
	 */
	public boolean empty()
		{
		return (hist.empty());
		}
	}
