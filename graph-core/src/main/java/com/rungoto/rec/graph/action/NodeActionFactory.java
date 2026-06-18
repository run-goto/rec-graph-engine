package com.rungoto.rec.graph.action;

import com.rungoto.rec.graph.graph.CompileConfig;

@FunctionalInterface
public interface NodeActionFactory {

    NodeAction create(CompileConfig config);
}
