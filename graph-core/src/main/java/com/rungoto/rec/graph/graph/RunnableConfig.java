package com.rungoto.rec.graph.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RunnableConfig {

    private final String requestId;
    private final String traceId;
    private final boolean debug;
    private final long deadlineMs;
    private final Map<String, Object> metadata;

    private RunnableConfig(Builder builder) {
        this.requestId = builder.requestId;
        this.traceId = builder.traceId;
        this.debug = builder.debug;
        this.deadlineMs = builder.deadlineMs;
        this.metadata = Collections.unmodifiableMap(new HashMap<String, Object>(builder.metadata));
    }

    public static Builder builder() {
        return new Builder();
    }

    public String requestId() {
        return requestId;
    }

    public String traceId() {
        return traceId;
    }

    public boolean debug() {
        return debug;
    }

    public long deadlineMs() {
        return deadlineMs;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public static final class Builder {
        private String requestId = UUID.randomUUID().toString();
        private String traceId = UUID.randomUUID().toString();
        private boolean debug;
        private long deadlineMs = System.currentTimeMillis() + 300L;
        private Map<String, Object> metadata = new HashMap<String, Object>();

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder deadlineMs(long deadlineMs) {
            this.deadlineMs = deadlineMs;
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public RunnableConfig build() {
            return new RunnableConfig(this);
        }
    }
}
