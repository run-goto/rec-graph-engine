package com.rungoto.rec.graph.edge;

import com.rungoto.rec.graph.graph.StateGraph;
import com.rungoto.rec.graph.node.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Edge {

    private final String source;
    private final List<EdgeValue> targets;

    public Edge(String source, List<EdgeValue> targets) {
        this.source = Objects.requireNonNull(source, "source");
        this.targets = Collections.unmodifiableList(new ArrayList<EdgeValue>(targets));
    }

    public static Edge of(String source, String target) {
        return new Edge(source, Collections.singletonList(EdgeValue.target(target)));
    }

    public static Edge conditional(String source, EdgeAction action, Map<String, String> mappings) {
        return new Edge(source, Collections.singletonList(EdgeValue.condition(EdgeCondition.single(action, mappings))));
    }

    public String source() {
        return source;
    }

    public List<EdgeValue> targets() {
        return targets;
    }

    public void validate(Map<String, Node> nodes) {
        if (!StateGraph.START.equals(source) && !nodes.containsKey(source)) {
            throw new IllegalArgumentException("Missing edge source node: " + source);
        }
        for (EdgeValue target : targets) {
            if (target.targetId() != null && !StateGraph.END.equals(target.targetId()) && !nodes.containsKey(target.targetId())) {
                throw new IllegalArgumentException("Missing edge target node: " + target.targetId());
            }
            if (target.condition() != null) {
                for (String mappedTarget : target.condition().mappings().values()) {
                    if (!StateGraph.END.equals(mappedTarget) && !nodes.containsKey(mappedTarget)) {
                        throw new IllegalArgumentException("Missing conditional edge target node: " + mappedTarget);
                    }
                }
            }
        }
    }
}
