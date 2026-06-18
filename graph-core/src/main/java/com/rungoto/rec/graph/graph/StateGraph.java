package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.action.NodeActionFactory;
import com.rungoto.rec.graph.edge.Edge;
import com.rungoto.rec.graph.edge.EdgeAction;
import com.rungoto.rec.graph.node.Node;
import com.rungoto.rec.graph.node.SubStateGraphNode;
import com.rungoto.rec.graph.state.StateSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class StateGraph {

    public static final String START = "__START__";
    public static final String END = "__END__";
    public static final String ERROR = "__ERROR__";

    private final String graphId;
    private final String version;
    private final StateSchema stateSchema;
    private final Map<String, Node> nodes = new LinkedHashMap<String, Node>();
    private final List<Edge> edges = new ArrayList<Edge>();

    public StateGraph(String graphId) {
        this(graphId, "v1", new StateSchema());
    }

    public StateGraph(String graphId, String version, StateSchema stateSchema) {
        this.graphId = Objects.requireNonNull(graphId, "graphId");
        this.version = Objects.requireNonNull(version, "version");
        this.stateSchema = Objects.requireNonNull(stateSchema, "stateSchema");
    }

    public StateGraph addNode(String id, NodeActionFactory actionFactory) {
        return addNode(id, Node.action(id, actionFactory));
    }

    public StateGraph addNode(Node node) {
        return addNode(node.id(), node);
    }

    public StateGraph addNode(String id, Node node) {
        node.validate();
        if (!Objects.equals(id, node.id())) {
            throw new IllegalArgumentException("Node id not match: " + id + " != " + node.id());
        }
        if (nodes.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate node: " + id);
        }
        nodes.put(id, node);
        return this;
    }

    public StateGraph addSubGraph(String id, StateGraph subGraph) {
        subGraph.validateGraph();
        return addNode(id, new SubStateGraphNode(id, subGraph));
    }

    public StateGraph addEdge(String source, String target) {
        edges.add(Edge.of(source, target));
        return this;
    }

    public StateGraph addEdge(String source, List<String> targets) {
        for (String target : targets) {
            addEdge(source, target);
        }
        return this;
    }

    public StateGraph addConditionalEdges(String source, EdgeAction action, Map<String, String> mappings) {
        edges.add(Edge.conditional(source, action, mappings));
        return this;
    }

    public void validateGraph() {
        if (edges.isEmpty()) {
            throw new IllegalArgumentException("Graph has no edges: " + graphId);
        }
        boolean hasStart = false;
        for (Node node : nodes.values()) {
            node.validate();
        }
        for (Edge edge : edges) {
            if (START.equals(edge.source())) {
                hasStart = true;
            }
            edge.validate(nodes);
        }
        if (!hasStart) {
            throw new IllegalArgumentException("Missing START edge in graph: " + graphId);
        }
    }

    public CompiledGraph compile() {
        return compile(CompileConfig.builder().build());
    }

    public CompiledGraph compile(CompileConfig config) {
        validateGraph();
        return new CompiledGraph(this, config);
    }

    public String graphId() {
        return graphId;
    }

    public String version() {
        return version;
    }

    public StateSchema stateSchema() {
        return stateSchema;
    }

    public Map<String, Node> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }
}
