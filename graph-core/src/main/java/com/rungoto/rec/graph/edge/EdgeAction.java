package com.rungoto.rec.graph.edge;

import com.rungoto.rec.graph.graph.RunnableConfig;
import com.rungoto.rec.graph.state.GraphState;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface EdgeAction {

    CompletionStage<EdgeCommand> apply(GraphState state, RunnableConfig config);
}
