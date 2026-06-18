package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.action.NodeAction;
import com.rungoto.rec.graph.action.NodeActionFactory;
import com.rungoto.rec.graph.edge.Edge;
import com.rungoto.rec.graph.edge.EdgeValue;
import com.rungoto.rec.graph.node.Node;
import com.rungoto.rec.graph.node.SubGraphNode;
import com.rungoto.rec.graph.state.StateSchema;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CompiledGraph {

    private final StateGraph stateGraph;
    private final CompileConfig compileConfig;
    private final StateSchema stateSchema;
    private final Map<String, NodeActionFactory> nodeFactories;
    private final Map<String, EdgeValue> edges;

    public CompiledGraph(StateGraph stateGraph, CompileConfig compileConfig) {
        this.stateGraph = Objects.requireNonNull(stateGraph, "stateGraph");
        this.compileConfig = Objects.requireNonNull(compileConfig, "compileConfig");
        this.stateSchema = mergeStateSchema(stateGraph);
        this.nodeFactories = compileNodeFactories(stateGraph);
        this.edges = compileEdges(stateGraph);
    }

    private StateSchema mergeStateSchema(StateGraph graph) {
        StateSchema merged = graph.stateSchema();
        for (Node node : graph.nodes().values()) {
            if (node instanceof SubGraphNode) {
                merged = merged.merge(((SubGraphNode) node).subGraph().stateSchema());
            }
        }
        return merged;
    }

    private Map<String, NodeActionFactory> compileNodeFactories(StateGraph graph) {
        Map<String, NodeActionFactory> result = new LinkedHashMap<String, NodeActionFactory>();
        for (Node node : graph.nodes().values()) {
            if (node instanceof SubGraphNode) {
                final CompiledGraph subCompiledGraph = ((SubGraphNode) node).subGraph().compile(compileConfig);
                result.put(node.id(), config -> new SubGraphAction(subCompiledGraph));
            }
            else {
                result.put(node.id(), node.actionFactory());
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private Map<String, EdgeValue> compileEdges(StateGraph graph) {
        Map<String, EdgeValue> result = new LinkedHashMap<String, EdgeValue>();
        for (Edge edge : graph.edges()) {
            List<EdgeValue> targets = edge.targets();
            if (targets.size() != 1) {
                throw new UnsupportedOperationException("Multiple targets are not supported in v0.1. Use v0.2 ParallelNode.");
            }
            result.put(edge.source(), targets.get(0));
        }
        return Collections.unmodifiableMap(result);
    }

    public NodeAction getNodeAction(String nodeId) {
        NodeActionFactory factory = nodeFactories.get(nodeId);
        return factory == null ? null : factory.create(compileConfig);
    }

    public EdgeValue getEdge(String nodeId) {
        return edges.get(nodeId);
    }

    public StateSchema stateSchema() {
        return stateSchema;
    }

    public CompileConfig compileConfig() {
        return compileConfig;
    }

    public StateGraph stateGraph() {
        return stateGraph;
    }
}
