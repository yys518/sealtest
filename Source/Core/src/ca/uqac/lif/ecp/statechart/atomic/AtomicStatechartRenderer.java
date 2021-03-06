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

import java.util.Map;
import java.util.Set;

import ca.uqac.lif.ecp.atomic.AtomicEvent;
import ca.uqac.lif.ecp.ltl.Operator;
import ca.uqac.lif.ecp.statechart.Action;
import ca.uqac.lif.ecp.statechart.Configuration;
import ca.uqac.lif.ecp.statechart.NestedState;
import ca.uqac.lif.ecp.statechart.State;
import ca.uqac.lif.ecp.statechart.Statechart;
import ca.uqac.lif.ecp.statechart.Transition;
import ca.uqac.lif.ecp.statechart.Configuration.UpStateNode;

/**
 * Renders a statechart graphically
 * @author Sylvain Hallé
 */
public class AtomicStatechartRenderer 
{
	protected static final String CRLF = System.getProperty("line.separator");
	
	/**
	 * Renders the statechart as a Graphviz (DOT) file
	 * @param sc The statechart
	 * @return A string with the contents of the DOT file
	 */
	public static String toDot(Statechart<AtomicEvent> sc)
	{
		return toDot(sc, "", "", false, "");
	}
	
	protected static String toDot(Statechart<AtomicEvent> sc, String label, String id, boolean dotted, String indent)
	{
		StringBuilder out = new StringBuilder();
		if (id.isEmpty())
		{
			out.append(indent).append("digraph G {").append(CRLF);
			out.append(indent).append("compound=true;").append(CRLF);
			out.append(indent).append("node [shape=Mrecord];").append(CRLF);
		}
		else
		{
			out.append(indent).append("subgraph cluster_").append(id).append(" {").append(CRLF);
			out.append(indent).append("label=\"").append(label).append("\";").append(CRLF);
		}
		if (dotted)
		{
			out.append(indent).append("graph [style=dashed];").append(CRLF);
		}
		else
		{
			out.append(indent).append("graph [style=solid];").append(CRLF);
		}
		for (Map.Entry<String,State<AtomicEvent>> state_entry : sc.getStates().entrySet())
		{
			State<AtomicEvent> state = state_entry.getValue();
			renderState(out, state, indent);
		}
		// Render "empty" transition for initial state
		State<AtomicEvent> initial_state = sc.getInitialState();
		int initial_id = initial_state.getId();
		out.append(indent).append("i").append(initial_id).append(" [shape=\"point\"];").append(CRLF);
		out.append(indent).append("i").append(initial_id).append(" -> ");
		if (initial_state instanceof NestedState)
		{
			NestedState<AtomicEvent> ns = (NestedState<AtomicEvent>) initial_state;
			State<AtomicEvent> any_child = ns.getAnyAtomicChild();
			int any_id = any_child.getId();
			out.append(any_id).append(" [lhead=\"cluster_").append(initial_id).append("\"]");
		}
		else
		{
			out.append(initial_id);
		}
		out.append(";").append(CRLF);
		// Create other transitions
		for (Map.Entry<Integer,Set<Transition<AtomicEvent>>> trans_entries : sc.getTransitions().entrySet())
		{
			for (Transition<AtomicEvent> trans : trans_entries.getValue())
			{
				renderTransition(out, sc, trans_entries.getKey(), trans, indent);
			}
		}
		out.append(indent).append("}").append(CRLF);
		return out.toString();
	}
	
	protected static void renderState(StringBuilder out, State<AtomicEvent> state, String indent)
	{
		if (state instanceof NestedState)
		{
			NestedState<AtomicEvent> n_state = (NestedState<AtomicEvent>) state;
			if (n_state.getContents().size() == 1)
			{
				// Normal nested state
				Statechart<AtomicEvent> inner_sc = n_state.getContents().get(0);
				out.append(toDot(inner_sc, n_state.getName(), n_state.getId() + "", false, indent + "  "));
			}
			else
			{
				// Orthogonal regions
				out.append(indent).append("subgraph cluster_").append(n_state.getId()).append(" {").append(CRLF);
				out.append(indent).append("label=\"").append(n_state.getName()).append("\";").append(CRLF);
				int reg_cnt = 0;
				for (Statechart<AtomicEvent> sc : n_state.getContents())
				{
					out.append(toDot(sc, "", n_state.getId() + "_" + reg_cnt, true, indent + "  "));
					reg_cnt++;
				}
				out.append(indent).append("};").append(CRLF);
			}
		}
		else
		{
			out.append(indent).append(state.getId()).append(" [label=\"").append(state.getName()).append("\"];").append(CRLF);
		}
	}
	
	protected static void renderTransition(StringBuilder out, Statechart<AtomicEvent> sc, int source_state, Transition<AtomicEvent> transition, String indent)
	{
		State<AtomicEvent> arrow_tail = sc.getAnyAtomicChild(sc.getState(source_state));
		int arrow_tail_id = arrow_tail.getId();
		Configuration<AtomicEvent> target = transition.getTarget();
		ChartStatePair csp = getLastState(sc, target);
		State<AtomicEvent> arrow_head = csp.chart.getAnyAtomicChild(csp.state);
		int arrow_head_id = arrow_head.getId();
		String label = renderTransitionLabel(transition);
		out.append(indent).append(arrow_tail_id).append(" -> ").append(arrow_head_id).append(" [label=\"").append(label).append("\"");
		if (arrow_tail_id != source_state)
		{
			out.append(",ltail=\"cluster_").append(source_state).append("\"");
		}
		if (arrow_head_id != csp.state.getId())
		{
			out.append(",lhead=\"cluster_").append(csp.state.getId()).append("\"");
		}
		out.append("];").append(CRLF);
	}
	
	protected static String renderTransitionLabel(Transition<AtomicEvent> transition)
	{
		StringBuilder out = new StringBuilder();
		out.append(((AtomicTransition) transition).getEvent().toString());
		Operator<?> op = transition.getGuard();
		if (op != null)
		{
			out.append(" [").append(op).append("]");
		}
		Action<?> ac = transition.getAction();
		if (ac != null)
		{
			out.append(" / ").append(ac);
		}
		return out.toString();
	}
	
	protected static ChartStatePair getLastState(Statechart<AtomicEvent> owner, Configuration<AtomicEvent> source)
	{
		while (!source.getChildren().isEmpty())
		{
			if (source instanceof UpStateNode)
			{
				owner = owner.getParent();
				source = source.getChildren().get(0);
				continue;
			}
			State<AtomicEvent> state = owner.getStates().get(source.getName());
			if (state instanceof NestedState)
			{
				owner = ((NestedState<AtomicEvent>) state).getContents().get(0);
			}
		}
		return new ChartStatePair(owner, owner.getStates().get(source.getName()));
	}
	
	protected static class ChartStatePair
	{
		public final Statechart<AtomicEvent> chart;
		public final State<AtomicEvent> state;
		
		public ChartStatePair(Statechart<AtomicEvent> chart, State<AtomicEvent> state)
		{
			super();
			this.chart = chart;
			this.state = state;
		}
	}
}
