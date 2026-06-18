package com.rungoto.rec.graph.state;

import java.util.Optional;
import java.util.function.Supplier;

public final class BaseChannel<T> implements Channel<T> {

    private final Reducer<T> reducer;
    private final Supplier<T> defaultProvider;

    public BaseChannel(Reducer<T> reducer, Supplier<T> defaultProvider) {
        this.reducer = reducer;
        this.defaultProvider = defaultProvider;
    }

    @Override
    public Optional<Reducer<T>> reducer() {
        return Optional.ofNullable(reducer);
    }

    @Override
    public Optional<Supplier<T>> defaultProvider() {
        return Optional.ofNullable(defaultProvider);
    }
}
