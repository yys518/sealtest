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
package ca.uqac.lif.ecp.atomic;

import java.util.Random;

import ca.uqac.lif.ecp.Alphabet;
import ca.uqac.lif.ecp.CoverageMetric;
import ca.uqac.lif.ecp.ThresholdRandomTraceGenerator;

public class AtomicThresholdRandomGenerator extends ThresholdRandomTraceGenerator<AtomicEvent>
{
	/**
	 * The alphabet to pick events from
	 */
	protected AtomicEvent[] m_alphabet;
	
	public AtomicThresholdRandomGenerator(Random random, CoverageMetric<AtomicEvent,Float> metric, int max_trace_length, float threshold, Alphabet<AtomicEvent> alphabet) 
	{
		super(random, metric, max_trace_length, threshold);
		m_alphabet = alphabet.toArray(new AtomicEvent[alphabet.size()]);
	}

	@Override
	protected AtomicEvent nextEvent(Random random)
	{
		return m_alphabet[random.nextInt(m_alphabet.length)];
	}
}
