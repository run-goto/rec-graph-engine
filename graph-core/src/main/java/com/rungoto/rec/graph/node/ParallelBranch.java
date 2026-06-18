package com.rungoto.rec.graph.node;

import com.rungoto.rec.graph.action.NodeActionFactory;
import com.rungoto.rec.graph.state.StateKey;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class ParallelBranch {

    private final String id;
    private final NodeActionFactory actionFactory;
    private final Set<StateKey<?>> reads;
    private final Set<StateKey<?>> writes;
    private final NodeOptions options;

    public ParallelBranch(String id, NodeActionFactory actionFactory) {
        this(id, actionFactory, Collections.<StateKey<?>>emptySet(), Collections.<StateKey<?>>emptySet(), NodeOptions.defaults());
    }

    public ParallelBranch(String id,
                          NodeActionFactory actionFactory,
                          Set<StateKey<?>> reads,
                          Set<StateKey<?>> writes,
                          NodeOptions options) {
        this.id = Objects.requireNonNull(id, "id");
        this.actionFactory = Objects.requireNonNull(actionFactory, "actionFactory");
        this.reads = Collections.unmodifiableSet(new LinkedHashSet<StateKey<?>>(reads));
        this.writes = Collections.unmodifiableSet(new LinkedHashSet<StateKey<?>>(writes));
        this.options = Objects.requireNonNull(options, "options");
    }

    public static ParallelBranch of(String id, NodeActionFactory actionFactory) {
        return new ParallelBranch(id, actionFactory);
    }

    public String id() {
        return id;
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
}
