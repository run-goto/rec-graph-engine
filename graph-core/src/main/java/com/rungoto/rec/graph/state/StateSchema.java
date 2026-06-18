package com.rungoto.rec.graph.state;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StateSchema {

    private final Map<StateKey<?>, Channel<?>> channels = new LinkedHashMap<StateKey<?>, Channel<?>>();

    public <T> StateSchema addChannel(StateKey<T> key, Channel<T> channel) {
        channels.put(key, channel);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Channel<T> channel(StateKey<T> key) {
        return (Channel<T>) channels.get(key);
    }

    public Map<StateKey<?>, Channel<?>> channels() {
        return Collections.unmodifiableMap(channels);
    }

    public StateSchema merge(StateSchema other) {
        StateSchema merged = new StateSchema();
        merged.channels.putAll(this.channels);
        if (other != null) {
            for (Map.Entry<StateKey<?>, Channel<?>> entry : other.channels.entrySet()) {
                if (!merged.channels.containsKey(entry.getKey())) {
                    merged.channels.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return merged;
    }
}
