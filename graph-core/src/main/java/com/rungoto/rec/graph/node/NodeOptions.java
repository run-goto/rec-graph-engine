package com.rungoto.rec.graph.node;

public final class NodeOptions {

    private final long timeoutMs;
    private final boolean required;
    private final boolean observable;

    private NodeOptions(Builder builder) {
        this.timeoutMs = builder.timeoutMs;
        this.required = builder.required;
        this.observable = builder.observable;
    }

    public static NodeOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public long timeoutMs() {
        return timeoutMs;
    }

    public boolean required() {
        return required;
    }

    public boolean observable() {
        return observable;
    }

    public static final class Builder {
        private long timeoutMs = 100L;
        private boolean required = true;
        private boolean observable = true;

        public Builder timeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        public Builder observable(boolean observable) {
            this.observable = observable;
            return this;
        }

        public NodeOptions build() {
            return new NodeOptions(this);
        }
    }
}
