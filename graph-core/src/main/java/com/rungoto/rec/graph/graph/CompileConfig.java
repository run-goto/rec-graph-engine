package com.rungoto.rec.graph.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class CompileConfig {

    private final int recursionLimit;
    private final Executor executor;
    private final Map<Class<?>, Object> beans;

    private CompileConfig(Builder builder) {
        this.recursionLimit = builder.recursionLimit;
        this.executor = builder.executor;
        this.beans = Collections.unmodifiableMap(new HashMap<Class<?>, Object>(builder.beans));
    }

    public static Builder builder() {
        return new Builder();
    }

    public int recursionLimit() {
        return recursionLimit;
    }

    public Executor executor() {
        return executor;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object bean = beans.get(type);
        if (bean == null) {
            throw new IllegalArgumentException("Missing bean: " + type.getName());
        }
        return (T) bean;
    }

    public static final class Builder {
        private int recursionLimit = 30;
        private Executor executor = ForkJoinPool.commonPool();
        private Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

        public Builder recursionLimit(int recursionLimit) {
            if (recursionLimit <= 0) {
                throw new IllegalArgumentException("recursionLimit must be > 0");
            }
            this.recursionLimit = recursionLimit;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = Objects.requireNonNull(executor, "executor");
            return this;
        }

        public <T> Builder bean(Class<T> type, T bean) {
            beans.put(type, bean);
            return this;
        }

        public CompileConfig build() {
            return new CompileConfig(this);
        }
    }
}
