package com.rungoto.rec.graph.graph;

public final class NodeExecutionContext {

    private final String nodeId;
    private final CompiledGraph compiledGraph;
    private final RunnableConfig runnableConfig;

    public NodeExecutionContext(String nodeId, CompiledGraph compiledGraph, RunnableConfig runnableConfig) {
        this.nodeId = nodeId;
        this.compiledGraph = compiledGraph;
        this.runnableConfig = runnableConfig;
    }

    public String nodeId() {
        return nodeId;
    }

    public CompiledGraph compiledGraph() {
        return compiledGraph;
    }

    public RunnableConfig runnableConfig() {
        return runnableConfig;
    }
}
