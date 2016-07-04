package ca.uqac.lif.ecp;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Trace generator that picks events randomly. The trace generator is
 * instructed to generate <i>n</i> traces, each of length <i>m</i>, by
 * picking at random each event in the set of possible events.
 * 
 * @param <T> The type of the events in the traces that are generated
 */
public abstract class BoundedRandomTraceGenerator<T> extends RandomTraceGenerator<T> 
{	
	/**
	 * The number of traces to generate
	 */
	protected int m_numTraces;
	
	/**
	 * The length of the traces to generate
	 */
	protected int m_traceLength;
	
	public BoundedRandomTraceGenerator(Random random, int num_traces, int trace_length)
	{
		super(random);
		m_numTraces = num_traces;
		m_traceLength = trace_length;
	}
	
	@Override
	public Set<Trace<T>> generateTraces()
	{
		Set<Trace<T>> trace_set = new HashSet<Trace<T>>();
		for (int trace_count = 0; trace_count < m_numTraces; trace_count++)
		{
			Trace<T> trace = new Trace<T>();
			for (int len_count = 0; len_count < m_traceLength; len_count++)
			{
				T event = nextEvent(m_random);
				trace.add(event);
			}
			trace_set.add(trace);
		}
		return trace_set;
	}

}