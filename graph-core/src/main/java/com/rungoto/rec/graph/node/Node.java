package com.rungoto.rec.graph.node;

import com.rungoto.rec.graph.action.NodeActionFactory;
import com.rungoto.rec.graph.graph.StateGraph;
import com.rungoto.rec.graph.state.StateKey;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Node {

    public static final String PRIVATE_PREFIX = "__";

    private final String id;
    private final NodeType type;
    private final NodeActionFactory actionFactory;
    private final Set<StateKey<?>> reads;
    private final Set<StateKey<?>> writes;
    private final NodeOptions options;

    public Node(String id, NodeActionFactory actionFactory) {
        this(id, NodeType.ACTION, actionFactory, Collections.<StateKey<?>>emptySet(), Collections.<StateKey<?>>emptySet(), NodeOptions.defaults());
    }

    public Node(String id, NodeType type, NodeActionFactory actionFactory, Set<StateKey<?>> reads, Set<StateKey<?>> writes, NodeOptions options) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.actionFactory = actionFactory;
        this.reads = Collections.unmodifiableSet(new LinkedHashSet<StateKey<?>>(reads));
        this.writes = Collections.unmodifiableSet(new LinkedHashSet<StateKey<?>>(writes));
        this.options = Objects.requireNonNull(options, "options");
    }

    public static Node action(String id, NodeActionFactory actionFactory) {
        return new Node(id, actionFactory);
    }

    public static Node action(String id, NodeType type, NodeActionFactory actionFactory, Set<StateKey<?>> reads, Set<StateKey<?>> writes, NodeOptions options) {
        return new Node(id, type, actionFactory, reads, writes, options);
    }

    public void validate() {
        if (StateGraph.START.equals(id) || StateGraph.END.equals(id)) {
            return;
        }
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("blank node id");
        }
        if (id.startsWith(PRIVATE_PREFIX)) {
            throw new IllegalArgumentException("node id cannot start with " + PRIVATE_PREFIX + ": " + id);
        }
        if (!(this instanceof SubGraphNode) && actionFactory == null) {
            throw new IllegalArgumentException("missing actionFactory: " + id);
        }
    }

    public String id() {
        return id;
    }

    public NodeType type() {
        return type;
    }

    public NodeActionFactory actionFactory() {
        return actionFactory;
    }

    public Set<StateKey<?>> reads() {
        return reads;
    }

    public Set<StateKey<?>> writes() {
        return writes;
    }

    public NodeOptions options() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Node(" + id + "," + type + ")";
    }
}
