package com.rungoto.rec.graph;

import com.rungoto.rec.graph.action.NodeResult;
import com.rungoto.rec.graph.graph.CompiledGraph;
import com.rungoto.rec.graph.graph.GraphResult;
import com.rungoto.rec.graph.graph.GraphRunner;
import com.rungoto.rec.graph.graph.StateGraph;
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

public class GraphRunnerTest {

    private static final StateKey<String> REQUEST = StateKey.of("request", String.class);

    @SuppressWarnings("unchecked")
    private static final StateKey<List> ITEMS = StateKey.of("items", List.class);

    @SuppressWarnings("unchecked")
    private static final StateKey<List> RANKED_ITEMS = StateKey.of("rankedItems", List.class);

    @Test
    public void shouldRunLinearRecommendationGraph() {
        StateSchema schema = new StateSchema()
                .addChannel(REQUEST, Channel.of(Reducers.replace()))
                .addChannel(ITEMS, Channel.of(Reducers.appendList(), ArrayList::new))
                .addChannel(RANKED_ITEMS, Channel.of(Reducers.replace(), ArrayList::new));

        StateGraph graph = new StateGraph("home-recommend", "v1", schema)
                .addNode("loadFeature", config -> (context, state) -> {
                    Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
                    updates.put(REQUEST, "user_feature_loaded");
                    return CompletableFuture.completedFuture(NodeResult.success(updates));
                })
                .addNode("recall", config -> (context, state) -> {
                    Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
                    updates.put(ITEMS, Arrays.asList("app_a", "app_b", "app_c"));
                    return CompletableFuture.completedFuture(NodeResult.success(updates));
                })
                .addNode("rank", config -> (context, state) -> {
                    List items = state.require(ITEMS);
                    List<String> ranked = new ArrayList<String>();
                    for (Object item : items) {
                        ranked.add(String.valueOf(item));
                    }
                    Collections.reverse(ranked);
                    Map<StateKey<?>, Object> updates = new LinkedHashMap<StateKey<?>, Object>();
                    updates.put(RANKED_ITEMS, ranked);
                    return CompletableFuture.completedFuture(NodeResult.success(updates));
                })
                .addEdge(StateGraph.START, "loadFeature")
                .addEdge("loadFeature", "recall")
                .addEdge("recall", "rank")
                .addEdge("rank", StateGraph.END);

        CompiledGraph compiledGraph = graph.compile();
        GraphResult result = new GraphRunner(compiledGraph).run(Collections.<StateKey<?>, Object>emptyMap());

        Assert.assertTrue(result.success());
        Assert.assertEquals(StateGraph.END, result.lastNodeId());
        Assert.assertEquals("user_feature_loaded", result.state().get(REQUEST));
        Assert.assertEquals(Arrays.asList("app_c", "app_b", "app_a"), result.state().get(RANKED_ITEMS));
    }
}
