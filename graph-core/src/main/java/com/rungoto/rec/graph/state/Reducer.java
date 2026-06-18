package com.rungoto.rec.graph.state;

@FunctionalInterface
public interface Reducer<T> {

    T apply(T oldValue, T newValue);
}
