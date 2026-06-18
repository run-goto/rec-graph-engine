package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.state.GraphState;

public final class GraphResult {

    private final GraphState state;
    private final String lastNodeId;
    private final boolean success;
    private final Throwable error;

    private GraphResult(GraphState state, String lastNodeId, boolean success, Throwable error) {
        this.state = state;
        this.lastNodeId = lastNodeId;
        this.success = success;
        this.error = error;
    }

    public static GraphResult success(GraphState state, String lastNodeId) {
        return new GraphResult(state, lastNodeId, true, null);
    }

    public static GraphResult error(GraphState state, String lastNodeId, Throwable error) {
        return new GraphResult(state, lastNodeId, false, error);
    }

    public GraphState state() {
        return state;
    }

    public String lastNodeId() {
        return lastNodeId;
    }

    public boolean success() {
        return success;
    }

    public Throwable error() {
        return error;
    }
}
