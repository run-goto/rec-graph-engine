package com.rungoto.rec.graph.state;

import java.util.Optional;
import java.util.function.Supplier;

public interface Channel<T> {

    Optional<Reducer<T>> reducer();

    Optional<Supplier<T>> defaultProvider();

    default T update(StateKey<T> key, T oldValue, T newValue) {
        T base = oldValue == null ? defaultProvider().map(Supplier::get).orElse(null) : oldValue;
        return reducer().map(r -> r.apply(base, newValue)).orElse(newValue);
    }

    static <T> Channel<T> of(Supplier<T> defaultProvider) {
        return new BaseChannel<T>(null, defaultProvider);
    }

    static <T> Channel<T> of(Reducer<T> reducer) {
        return new BaseChannel<T>(reducer, null);
    }

    static <T> Channel<T> of(Reducer<T> reducer, Supplier<T> defaultProvider) {
        return new BaseChannel<T>(reducer, defaultProvider);
    }
}
