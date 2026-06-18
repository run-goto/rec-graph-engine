package com.rungoto.rec.graph.state;

import java.util.Objects;

public final class StateKey<T> {

    private final String name;
    private final Class<T> type;

    private StateKey(String name, Class<T> type) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
    }

    public static <T> StateKey<T> of(String name, Class<T> type) {
        return new StateKey<T>(name, type);
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StateKey)) {
            return false;
        }
        StateKey<?> stateKey = (StateKey<?>) o;
        return name.equals(stateKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "StateKey(" + name + ")";
    }
}
