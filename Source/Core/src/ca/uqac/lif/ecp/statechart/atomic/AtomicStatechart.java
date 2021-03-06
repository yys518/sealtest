/*
    Log trace triaging and etc.
    Copyright (C) 2016-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.ecp.statechart.atomic;

import java.util.Set;

import ca.uqac.lif.ecp.Alphabet;
import ca.uqac.lif.ecp.atomic.AtomicEvent;
import ca.uqac.lif.ecp.statechart.NestedState;
import ca.uqac.lif.ecp.statechart.State;
import ca.uqac.lif.ecp.statechart.Statechart;
import ca.uqac.lif.ecp.statechart.Transition;

/**
 * Statechart of atomic events whose alphabet is known in advance.
 * @author Sylvain Hallé
 */
public class AtomicStatechart extends Statechart<AtomicEvent> 
{
	/**
	 * The alphabet associated to this statechart
	 */
	protected Alphabet<AtomicEvent> m_alphabet;
	
	public AtomicStatechart()
	{
		super();
	}
	
	public AtomicStatechart(String name)
	{
		super(name);
	}
	
	/**
	 * Gets the set of all the events that occur as transition labels in this
	 * statechart
	 * @return The set of events
	 */
	public Set<AtomicEvent> getAlphabet()
	{
		Set<AtomicEvent> alphabet = new Alphabet<AtomicEvent>();
		for (Set<Transition<AtomicEvent>> trans : getTransitions().values())
		{
			for (Transition<AtomicEvent> t : trans)
			{
				alphabet.add(((AtomicTransition) t).getEvent());
			}
		}
		for (State<AtomicEvent> s : getStates().values())
		{
			if (s instanceof NestedState)
			{
				for (Statechart<AtomicEvent> as : ((NestedState<AtomicEvent>) s).getContents())
				{
					alphabet.addAll(((AtomicStatechart) as).getAlphabet());
				}
			}
		}
		return alphabet;
	}
}
