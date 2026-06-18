package com.rungoto.rec.graph.edge;

import java.util.Objects;

public final class EdgeValue {

    private final String targetId;
    private final EdgeCondition condition;

    private EdgeValue(String targetId, EdgeCondition condition) {
        this.targetId = targetId;
        this.condition = condition;
    }

    public static EdgeValue target(String targetId) {
        return new EdgeValue(Objects.requireNonNull(targetId, "targetId"), null);
    }

    public static EdgeValue condition(EdgeCondition condition) {
        return new EdgeValue(null, Objects.requireNonNull(condition, "condition"));
    }

    public String targetId() {
        return targetId;
    }

    public EdgeCondition condition() {
        return condition;
    }

    public boolean conditional() {
        return condition != null;
    }
}
