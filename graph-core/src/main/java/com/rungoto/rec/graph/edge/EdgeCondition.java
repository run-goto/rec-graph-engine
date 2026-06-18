package com.rungoto.rec.graph.edge;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class EdgeCondition {

    private final EdgeAction action;
    private final Map<String, String> mappings;
    private final boolean multiCommand;

    private EdgeCondition(EdgeAction action, Map<String, String> mappings, boolean multiCommand) {
        this.action = Objects.requireNonNull(action, "action");
        this.mappings = Collections.unmodifiableMap(new LinkedHashMap<String, String>(mappings));
        this.multiCommand = multiCommand;
    }

    public static EdgeCondition single(EdgeAction action, Map<String, String> mappings) {
        return new EdgeCondition(action, mappings, false);
    }

    public EdgeAction action() {
        return action;
    }

    public Map<String, String> mappings() {
        return mappings;
    }

    public boolean multiCommand() {
        return multiCommand;
    }

    public String resolveTarget(String route) {
        String target = mappings.get(route);
        if (target == null) {
            throw new IllegalArgumentException("Missing edge mapping for route: " + route);
        }
        return target;
    }
}
