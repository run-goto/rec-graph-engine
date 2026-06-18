package com.rungoto.rec.graph.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Reducers {

    private Reducers() {
    }

    public static <T> Reducer<T> replace() {
        return (oldValue, newValue) -> newValue;
    }

    public static <T> Reducer<List<T>> appendList() {
        return (oldValue, newValue) -> {
            List<T> result = new ArrayList<T>();
            if (oldValue != null) {
                result.addAll(oldValue);
            }
            if (newValue != null) {
                result.addAll(newValue);
            }
            return result;
        };
    }

    public static <K, V> Reducer<Map<K, V>> mergeMap() {
        return (oldValue, newValue) -> {
            Map<K, V> result = new HashMap<K, V>();
            if (oldValue != null) {
                result.putAll(oldValue);
            }
            if (newValue != null) {
                result.putAll(newValue);
            }
            return result;
        };
    }
}
