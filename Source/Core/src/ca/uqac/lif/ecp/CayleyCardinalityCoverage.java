/*
    Log trace triaging and etc.
    Copyright (C) 2016 Sylvain Hall�

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
package ca.uqac.lif.ecp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.structures.MathSet;

/**
 * Coverage of a set of traces based on the fraction of all traces whose
 * equivalence class for some triaging function is represented 
 * in one of the prefixes of some trace.
 * @author Sylvain
 *
 * @param <T> The type of events in the trace
 * @param <U> The output type of the triaging function
 */
public class CayleyCardinalityCoverage<T extends Event,U extends Object> extends CayleyCoverageMetric<T,U,Float> 
{
	/**
	 * Whether the cardinality considers all traces <em>up to</em> a given
	 * length (true), or exactly of given length (false)
	 */
	protected boolean m_cumulative = true;
	
	public CayleyCardinalityCoverage(CayleyGraph<T, U> graph, TriagingFunction<T,U> function) 
	{
		super(graph, function);
	}

	/**
	 * Sets whether the cardinality considers all traces <em>up to</em> a given
	 * length (true), or exactly of given length (false)
	 * @param b 
	 */
	public void setCumulative(boolean b)
	{
		m_cumulative = b;
	}

	@Override
	public Float getCoverage(Set<Trace<T>> traces) 
	{
		Set<MathSet<U>> all_classes = new HashSet<MathSet<U>>();
		all_classes.addAll(m_graph.getLabelling().values());
		Set<MathSet<U>> covered_classes = new HashSet<MathSet<U>>();
		int max_length = 0;
		for (Trace<T> trace : traces)
		{
			max_length = Math.max(max_length, trace.size());
			m_function.reset();
			for (T event : trace)
			{
				MathSet<U> category = m_function.read(event);
				covered_classes.add(category);
			}
		}
		Map<MathSet<U>,Integer> cardinalities = m_graph.getClassCardinality(max_length, m_cumulative);
		float covered_traces = 0, total_traces = 0;
		for (MathSet<U> category : cardinalities.keySet())
		{
			int cardinality = cardinalities.get(category);
			total_traces += cardinality;
			if (covered_classes.contains(category))
			{
				covered_traces += cardinality;
			}
		}
		return covered_traces / total_traces;
	}

}
