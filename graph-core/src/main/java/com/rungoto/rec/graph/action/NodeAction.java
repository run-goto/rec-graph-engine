package com.rungoto.rec.graph.action;

import com.rungoto.rec.graph.graph.NodeExecutionContext;
import com.rungoto.rec.graph.state.GraphState;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface NodeAction {

    CompletionStage<NodeResult> execute(NodeExecutionContext context, GraphState state);
}
