# rec-graph-engine

A Spring Boot friendly graph scheduling engine for online recommendation scenarios.

## Design

- `StateGraph`: graph definition.
- `CompiledGraph`: executable graph model.
- `Node`: computation unit.
- `Edge`: control-flow relation.
- `Channel`: state merge rule.
- `GraphRunner`: request-level graph executor.

The first implementation targets low-latency recommendation chains such as feature loading, multi-channel recall, merge, rank, rerank, and delivery.
