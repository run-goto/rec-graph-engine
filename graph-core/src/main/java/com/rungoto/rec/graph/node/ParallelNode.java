package com.rungoto.rec.graph.node;

import com.rungoto.rec.graph.graph.CompileConfig;
import com.rungoto.rec.graph.graph.ParallelNodeAction;
import com.rungoto.rec.graph.state.StateKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ParallelNode extends Node {

    private final List<ParallelBranch> branches;

    public ParallelNode(String id, List<ParallelBranch> branches) {
        super(id,
                NodeType.PARALLEL,
                (CompileConfig config) -> new ParallelNodeAction(id, branches, config),
                collectReads(branches),
                collectWrites(branches),
                NodeOptions.defaults());
        if (branches == null || branches.isEmpty()) {
            throw new IllegalArgumentException("ParallelNode branches cannot be empty: " + id);
        }
        this.branches = Collections.unmodifiableList(new ArrayList<ParallelBranch>(branches));
    }

    public static ParallelNode of(String id, List<ParallelBranch> branches) {
        return new ParallelNode(id, branches);
    }

    public List<ParallelBranch> branches() {
        return branches;
    }

    private static Set<StateKey<?>> collectReads(List<ParallelBranch> branches) {
        Set<StateKey<?>> result = new LinkedHashSet<StateKey<?>>();
        if (branches != null) {
            for (ParallelBranch branch : branches) {
                result.addAll(branch.reads());
            }
        }
        return result;
    }

    private static Set<StateKey<?>> collectWrites(List<ParallelBranch> branches) {
        Set<StateKey<?>> result = new LinkedHashSet<StateKey<?>>();
        if (branches != null) {
            for (ParallelBranch branch : branches) {
                result.addAll(branch.writes());
            }
        }
        return result;
    }
}
