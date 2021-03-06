/*
    Log trace triaging and etc.
    Copyright (C) 2016 Sylvain Hallé

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
package ca.uqac.lif.ecp.atomic;

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.ecp.Alphabet;
import ca.uqac.lif.ecp.CayleyGraphFactory;
import ca.uqac.lif.ecp.Edge;
import ca.uqac.lif.ecp.graphs.Vertex;

/**
 * Class that creates a Cayley graph out of a triaging function based on a
 * finite-state automaton.
 * <p>
 * <strong>Caveat emptor:</strong> the transition function of the automaton
 * must be total. 
 * @param <U> The output type of the triaging function
 */
public class AutomatonCayleyGraphFactory<U> extends CayleyGraphFactory<AtomicEvent,U>
{
	protected Alphabet<AtomicEvent> m_alphabet;
	
	public AutomatonCayleyGraphFactory(Alphabet<AtomicEvent> alphabet)
	{
		super();
		m_alphabet = alphabet;
	}

	@Override
	protected Set<AtomicEvent> getNextEvents(Vertex<AtomicEvent> vertex) 
	{
		Set<AtomicEvent> events = new HashSet<AtomicEvent>();
		for (Edge<AtomicEvent> e : vertex.getEdges())
		{
			events.add(e.getLabel());
		}
		return m_alphabet;
	}
}
