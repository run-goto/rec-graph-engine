package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.action.NodeAction;
import com.rungoto.rec.graph.action.NodeResult;
import com.rungoto.rec.graph.state.GraphState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class SubGraphAction implements NodeAction {

    private final CompiledGraph subGraph;

    public SubGraphAction(CompiledGraph subGraph) {
        this.subGraph = subGraph;
    }

    @Override
    public CompletionStage<NodeResult> execute(NodeExecutionContext context, GraphState state) {
        GraphRunner runner = new GraphRunner(subGraph);
        GraphResult result = runner.run(state, context.runnableConfig());
        if (!result.success()) {
            return CompletableFuture.completedFuture(NodeResult.error(result.error()));
        }
        return CompletableFuture.completedFuture(NodeResult.success());
    }
}
