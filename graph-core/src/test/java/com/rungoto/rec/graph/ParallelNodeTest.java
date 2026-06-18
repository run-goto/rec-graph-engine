package com.rungoto.rec.graph;

import com.rungoto.rec.graph.action.NodeResult;
import com.rungoto.rec.graph.graph.CompiledGraph;
import com.rungoto.rec.graph.graph.GraphResult;
import com.rungoto.rec.graph.graph.GraphRunner;
import com.rungoto.rec.graph.graph.StateGraph;
import com.rungoto.rec.graph.node.ParallelBranch;
import com.rungoto.rec.graph.node.ParallelNode;
import com.rungoto.rec.graph.state.Channel;
import com.rungoto.rec.graph.state.Reducers;
import com.rungoto.rec.graph.state.StateKey;
import com.rungoto.rec.graph.state.StateSchema;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ParallelNodeTest {

    @SuppressWarnings("unchecked")
    private static final StateKey<List> ITEMS = StateKey.of("items", List.class);

    @SuppressWarnings("unchecked")
    private static final StateKey<List> RANKED_ITEMS = StateKey.of("rankedItems", List.class);

    @Test
    public void shouldMergeParallelRecallResultsByChannelReducer() {
        StateSchema schema = new StateSchema()
                .addChannel(ITEMS, Channel.of(Reducers.appendList(), ArrayList::new))
                .addChannel(RANKED_ITEMS, Channel.of(Reducers.replace(), ArrayList::new));

        ParallelBranch countryRecall = ParallelBranch.of("countryRecall", config -> (context, state) -> {
            Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
            updates.put(ITEMS, Arrays.asList("country_app_a", "country_app_b"));
            return CompletableFuture.completedFuture(NodeResult.success(updates));
        });

        ParallelBranch i2iRecall = ParallelBranch.of("i2iRecall", config -> (context, state) -> {
            Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
            updates.put(ITEMS, Arrays.asList("i2i_app_a", "i2i_app_b"));
            return CompletableFuture.completedFuture(NodeResult.success(updates));
        });

        StateGraph graph = new StateGraph("parallel-recall", "v1", schema)
                .addNode(ParallelNode.of("parallelRecall", Arrays.asList(countryRecall, i2iRecall)))
                .addNode("rank", config -> (context, state) -> {
                    List items = state.require(ITEMS);
                    Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
                    updates.put(RANKED_ITEMS, new ArrayList(items));
                    return CompletableFuture.completedFuture(NodeResult.success(updates));
                })
                .addEdge(StateGraph.START, "parallelRecall")
                .addEdge("parallelRecall", "rank")
                .addEdge("rank", StateGraph.END);

        CompiledGraph compiledGraph = graph.compile();
        GraphResult result = new GraphRunner(compiledGraph).run(Collections.<StateKey<?>, Object>emptyMap());

        Assert.assertTrue(result.success());
        List items = result.state().get(ITEMS);
        Assert.assertEquals(4, items.size());
        Assert.assertTrue(items.contains("country_app_a"));
        Assert.assertTrue(items.contains("country_app_b"));
        Assert.assertTrue(items.contains("i2i_app_a"));
        Assert.assertTrue(items.contains("i2i_app_b"));
    }
}
