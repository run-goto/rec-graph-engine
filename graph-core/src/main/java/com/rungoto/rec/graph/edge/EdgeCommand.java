package com.rungoto.rec.graph.edge;

import com.rungoto.rec.graph.state.StateKey;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EdgeCommand {

    private final String route;
    private final Map<StateKey<?>, Object> updates;

    public EdgeCommand(String route, Map<StateKey<?>, Object> updates) {
        this.route = route;
        this.updates = updates == null ? Collections.<StateKey<?>, Object>emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<StateKey<?>, Object>(updates));
    }

    public static EdgeCommand route(String route) {
        return new EdgeCommand(route, Collections.<StateKey<?>, Object>emptyMap());
    }

    public String route() {
        return route;
    }

    public Map<StateKey<?>, Object> updates() {
        return updates;
    }
}
