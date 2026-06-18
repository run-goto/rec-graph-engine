package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.action.NodeAction;
import com.rungoto.rec.graph.action.NodeResult;
import com.rungoto.rec.graph.node.ParallelBranch;
import com.rungoto.rec.graph.state.GraphState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class ParallelNodeAction implements NodeAction {

    private final String nodeId;
    private final List<ParallelBranch> branches;
    private final CompileConfig compileConfig;

    public ParallelNodeAction(String nodeId, List<ParallelBranch> branches, CompileConfig compileConfig) {
        this.nodeId = nodeId;
        this.branches = branches;
        this.compileConfig = compileConfig;
    }

    @Override
    public CompletionStage<NodeResult> execute(NodeExecutionContext context, GraphState state) {
        if (branches == null || branches.isEmpty()) {
            return CompletableFuture.completedFuture(NodeResult.success());
        }

        List<CompletableFuture<NodeResult>> futures = new ArrayList<CompletableFuture<NodeResult>>();
        for (ParallelBranch branch : branches) {
            NodeAction action = branch.actionFactory().create(compileConfig);
            NodeExecutionContext branchContext = new NodeExecutionContext(nodeId + "/" + branch.id(), context.compiledGraph(), context.runnableConfig());
            CompletableFuture<NodeResult> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return action.execute(branchContext, state).toCompletableFuture().get();
                }
                catch (Exception e) {
                    if (branch.options().required()) {
                        return NodeResult.error(e);
                    }
                    return NodeResult.skip(e.getMessage());
                }
            }, compileConfig.executor());
            futures.add(future);
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return all.thenApply(ignored -> {
            for (CompletableFuture<NodeResult> future : futures) {
                NodeResult result = future.join();
                if (result.error() != null) {
                    return NodeResult.error(result.error());
                }
                state.updateAll(result.updates());
            }
            return NodeResult.success();
        });
    }
}
