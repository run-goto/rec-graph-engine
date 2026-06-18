package com.rungoto.rec.graph.node;

import com.rungoto.rec.graph.graph.StateGraph;

public interface SubGraphNode {

    StateGraph subGraph();

    default String formatId(String parentId, String childId) {
        return parentId + "/" + childId;
    }
}
