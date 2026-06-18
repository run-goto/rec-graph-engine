package com.rungoto.rec.graph.state;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class GraphState {

    private final StateSchema schema;
    private final ConcurrentMap<StateKey<?>, Object> values = new ConcurrentHashMap<StateKey<?>, Object>();

    public GraphState(StateSchema schema) {
        this.schema = Objects.requireNonNull(schema, "schema");
    }

    public <T> void put(StateKey<T> key, T value) {
        values.put(key, value);
    }

    public void updateAll(Map<StateKey<?>, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }
        for (Map.Entry<StateKey<?>, Object> entry : updates.entrySet()) {
            updateRaw(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void updateRaw(StateKey key, Object newValue) {
        Channel channel = schema.channel(key);
        values.compute(key, (k, oldValue) -> {
            if (channel == null) {
                return newValue;
            }
            return channel.update(key, oldValue, newValue);
        });
    }

    public <T> T get(StateKey<T> key) {
        Object value = values.get(key);
        if (value == null) {
            Channel<T> channel = schema.channel(key);
            if (channel == null) {
                return null;
            }
            return channel.defaultProvider().map(Supplier::get).orElse(null);
        }
        return key.type().cast(value);
    }

    public <T> T require(StateKey<T> key) {
        T value = get(key);
        if (value == null) {
            throw new IllegalStateException("Missing state: " + key.name());
        }
        return value;
    }

    public Map<StateKey<?>, Object> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<StateKey<?>, Object>(values));
    }
}
