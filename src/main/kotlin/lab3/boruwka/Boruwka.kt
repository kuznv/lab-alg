package lab3.boruwka

import util.dfs
import java.util.*

internal typealias Weight = Int
internal typealias VertexIndex = Int
internal typealias Graph = List<Map<VertexIndex, Weight>>

private data class Edge(val weight: Weight) {
    var isColored = false; internal set

}
private class Vertex(val index: VertexIndex) {
    lateinit var edges: Map<Vertex, Edge>
}

fun boruwka(graph: Graph): Graph {
    val vertices = graph.toVertices()

    while (true)
        for (component in getComponents(vertices)) {
            component
                    .asSequence()
                    .flatMap { it.edges.entries.asSequence() }
                    .filter { (vertex) -> vertex !in component }
                    .map { (_, edge) -> edge }
                    .minBy { it.weight }
                    ?.apply { isColored = true }
                    ?: return vertices.toGraph()
        }
}

private fun getComponents(vertices: List<Vertex>): List<Set<Vertex>> {
    val components = mutableListOf<Set<Vertex>>()
    val visitedVertices = BitSet(vertices.size)

    var lastNotVisited = 0
    while (lastNotVisited < vertices.size) {
        val startingVertex = vertices[lastNotVisited]

        val component = startingVertex.dfs { vertex ->
            visitedVertices[vertex.index] = true
            vertex.edges.filter { (_, edge) -> edge.isColored }.keys
        }
        components += component

        lastNotVisited = visitedVertices.nextClearBit(lastNotVisited)
    }

    return components
}

private fun List<Vertex>.toGraph(): Graph = map { v1 ->
    v1.edges
            .filterValues(Edge::isColored)
            .mapKeys { (v2) -> indexOf(v2) }
            .mapValues { (_, edge) -> edge.weight }
}

private fun Graph.toVertices(): List<Vertex> {
    val vertices = List(size, ::Vertex)
    forEachIndexed { v1, weights ->
        vertices[v1].edges = weights
                .map { (v2, w) ->
                    val vertex2 = vertices[v2]
                    val edge = if (v1 < v2) Edge(w) else vertex2.edges[vertices[v1]]!!
                    vertex2 to edge
                }
                .toMap()
    }
    return vertices
}