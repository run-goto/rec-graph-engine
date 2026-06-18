package com.rungoto.rec.graph.action;

import com.rungoto.rec.graph.state.StateKey;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class NodeResult {

    private final NodeOutcome outcome;
    private final Map<StateKey<?>, Object> updates;
    private final Map<String, Object> attributes;
    private final Throwable error;

    private NodeResult(NodeOutcome outcome, Map<StateKey<?>, Object> updates, Map<String, Object> attributes, Throwable error) {
        this.outcome = outcome;
        this.updates = updates == null ? Collections.<StateKey<?>, Object>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<StateKey<?>, Object>(updates));
        this.attributes = attributes == null ? Collections.<String, Object>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<String, Object>(attributes));
        this.error = error;
    }

    public static NodeResult success() {
        return success(Collections.<StateKey<?>, Object>emptyMap());
    }

    public static NodeResult success(Map<StateKey<?>, Object> updates) {
        return new NodeResult(NodeOutcome.CONTINUE, updates, Collections.<String, Object>emptyMap(), null);
    }

    public static NodeResult stop(Map<StateKey<?>, Object> updates) {
        return new NodeResult(NodeOutcome.STOP, updates, Collections.<String, Object>emptyMap(), null);
    }

    public static NodeResult skip(String reason) {
        Map<String, Object> attributes = new LinkedHashMap<String, Object>();
        attributes.put("reason", reason);
        return new NodeResult(NodeOutcome.SKIP, Collections.<StateKey<?>, Object>emptyMap(), attributes, null);
    }

    public static NodeResult error(Throwable error) {
        return new NodeResult(NodeOutcome.STOP, Collections.<StateKey<?>, Object>emptyMap(), Collections.<String, Object>emptyMap(), error);
    }

    public NodeOutcome outcome() {
        return outcome;
    }

    public Map<StateKey<?>, Object> updates() {
        return updates;
    }

    public Map<String, Object> attributes() {
        return attributes;
    }

    public Throwable error() {
        return error;
    }
}
