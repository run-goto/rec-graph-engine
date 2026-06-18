package com.rungoto.rec.graph.graph;

import com.rungoto.rec.graph.action.NodeAction;
import com.rungoto.rec.graph.action.NodeOutcome;
import com.rungoto.rec.graph.action.NodeResult;
import com.rungoto.rec.graph.edge.EdgeCommand;
import com.rungoto.rec.graph.edge.EdgeCondition;
import com.rungoto.rec.graph.edge.EdgeValue;
import com.rungoto.rec.graph.state.GraphState;
import com.rungoto.rec.graph.state.StateKey;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public final class GraphRunner {

    private final CompiledGraph compiledGraph;

    public GraphRunner(CompiledGraph compiledGraph) {
        this.compiledGraph = compiledGraph;
    }

    public GraphResult run(Map<StateKey<?>, Object> inputs) {
        return run(inputs, RunnableConfig.builder().build());
    }

    public GraphResult run(Map<StateKey<?>, Object> inputs, RunnableConfig config) {
        GraphState state = new GraphState(compiledGraph.stateSchema());
        state.updateAll(inputs);
        return run(state, config);
    }

    public GraphResult run(GraphState state, RunnableConfig config) {
        String currentNodeId = StateGraph.START;
        int iterations = 0;
        try {
            while (iterations++ < compiledGraph.compileConfig().recursionLimit()) {
                String nextNodeId = nextNodeId(currentNodeId, state, config);
                if (nextNodeId == null) {
                    return GraphResult.error(state, currentNodeId, new IllegalStateException("Missing edge from node: " + currentNodeId));
                }
                if (StateGraph.END.equals(nextNodeId)) {
                    return GraphResult.success(state, StateGraph.END);
                }

                NodeAction action = compiledGraph.getNodeAction(nextNodeId);
                if (action == null) {
                    return GraphResult.error(state, nextNodeId, new IllegalStateException("Missing node action: " + nextNodeId));
                }

                NodeExecutionContext nodeContext = new NodeExecutionContext(nextNodeId, compiledGraph, config);
                NodeResult result = action.execute(nodeContext, state).toCompletableFuture().get();
                if (result.error() != null) {
                    return GraphResult.error(state, nextNodeId, result.error());
                }
                state.updateAll(result.updates());
                if (result.outcome() == NodeOutcome.STOP) {
                    return GraphResult.success(state, nextNodeId);
                }
                currentNodeId = nextNodeId;
            }
            return GraphResult.error(state, currentNodeId, new IllegalStateException("Recursion limit exceeded: " + compiledGraph.compileConfig().recursionLimit()));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return GraphResult.error(state, currentNodeId, e);
        }
        catch (ExecutionException e) {
            return GraphResult.error(state, currentNodeId, e.getCause() == null ? e : e.getCause());
        }
        catch (Throwable e) {
            return GraphResult.error(state, currentNodeId, e);
        }
    }

    private String nextNodeId(String nodeId, GraphState state, RunnableConfig config) throws Exception {
        EdgeValue edge = compiledGraph.getEdge(nodeId);
        if (edge == null) {
            return null;
        }
        if (!edge.conditional()) {
            return edge.targetId();
        }
        EdgeCondition condition = edge.condition();
        EdgeCommand command = condition.action().apply(state, config).toCompletableFuture().get();
        state.updateAll(command.updates());
        return condition.resolveTarget(command.route());
    }
}
