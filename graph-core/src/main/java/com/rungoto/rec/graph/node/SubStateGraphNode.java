package com.rungoto.rec.graph.node;

import com.rungoto.rec.graph.graph.StateGraph;
import com.rungoto.rec.graph.state.StateKey;

import java.util.Collections;
import java.util.Objects;

public final class SubStateGraphNode extends Node implements SubGraphNode {

    private final StateGraph subGraph;

    public SubStateGraphNode(String id, StateGraph subGraph) {
        super(id, NodeType.SUB_GRAPH, null, Collections.<StateKey<?>>emptySet(), Collections.<StateKey<?>>emptySet(), NodeOptions.defaults());
        this.subGraph = Objects.requireNonNull(subGraph, "subGraph");
    }

    @Override
    public StateGraph subGraph() {
        return subGraph;
    }

    public String formatId(String childNodeId) {
        return formatId(id(), childNodeId);
    }
}
