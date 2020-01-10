package lab3.prim

import lab3.boruwka.Graph
import lab3.boruwka.Weight
import java.util.*

private data class Edge(val weight: Weight)

private class Vertex(var priority: Int) : Comparable<Vertex> {
    lateinit var edges: Map<Vertex, Edge>

    var parent: Vertex? = null
    override fun compareTo(other: Vertex) = compareValues(priority, other.priority)

}

fun prim(graph: Graph): Graph {
    val vertices = graph.toVertices()
    vertices.firstOrNull()?.priority = 0

    val queue = PriorityQueue<Vertex>()
    queue += vertices

    for (v1 in generateSequence(queue::poll)) {
        for ((v2, edge) in v1.edges) {
            val w = edge.weight
            if (v2.priority > w && v2 in queue) {
                v2.parent = v1
                queue -= v2
                v2.priority = w
                queue += v2
            }
        }
    }

    return vertices.toGraph()
}

private fun List<Vertex>.toGraph(): Graph = map { v1 ->
    v1.edges
            .filterKeys { v2 -> v1.parent == v2 || v2.parent == v1 }
            .mapKeys { (v) -> indexOf(v) }
            .mapValues { (_, edge) -> edge.weight }
}

private fun Graph.toVertices(): List<Vertex> {
    val vertices = List(size) { index -> Vertex(priority = Int.MAX_VALUE) }
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