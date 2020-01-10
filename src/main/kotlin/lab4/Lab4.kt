package lab4

import util.*
import java.util.*

private fun getTestGraph1(random: Random): Graph = listOf(
        // @formatter:off
        //               a         b         c         d         e         f
        /* a */ listOf(            1,        2,        3,        4             ),
        /* b */ listOf(  0,                            3                       ),
        /* c */ listOf(  0,                                      4             ),
        /* d */ listOf(  0,        1,                            4,        5   ),
        /* e */ listOf(  0,                  2,        3,                  5   ),
        /* f */ listOf(                                3,        4             )
        // @formatter:on
).mapIndexed { v1, list ->
    list.dropWhile { it <= v1 }.map { it to random.nextInt(1..9) }.toMap()
}

fun main(args: Array<String>) {
//    val graph = randomGraph(8, 32, 1..9, Random(0))
    val graph = getTestGraph1(Random(1))
    val vertices = graph.toVertices()

    val source = vertices.first()
    val sink = vertices.last()

    println(graph.toString2())
    println()
    val maxFlow = getMaxFlow(source, sink)
    println(vertices.toGraph().toString2())
    println()
    println("Max flow = $maxFlow")
}

private class Vertex(val n: Int) {
    lateinit var edges: Map<Vertex, Edge>
}

private class Edge(var maxFlow: Int, val isReversed: Boolean) {
    var currentFlow = 0
    val isAcceptable
        get() = (isReversed && currentFlow > 0) || (!isReversed && currentFlow < maxFlow)
}

private fun getMaxFlow(source: Vertex, sink: Vertex): Int {

    fun findChain(): List<Vertex>? {
        fun visit(vertex: Vertex, chain: Set<Vertex>): Set<Vertex>? {
            if (vertex == sink) return chain

            for ((vertex2, edge) in vertex.edges) {
                if (!edge.isAcceptable) continue
                if (vertex2 in chain) continue
                visit(vertex2, chain + vertex2)?.let { return it }
            }
            return null
        }
        return visit(source, linkedSetOf(source))?.toList()
    }

    while (true) {
        val chain = findChain() ?: return source.edges.values.sumBy { it.currentFlow }

        val chainEdges = chain.zipWithNext { v1, v2 -> v1.edges[v2]!! }
        val delta = chainEdges.map { it.maxFlow - it.currentFlow }.min()!!

        for (edge in chainEdges) {
            if (edge.isReversed) {
                edge.currentFlow -= delta
            } else {
                edge.currentFlow += delta
            }
        }
    }
}

private fun Graph.toVertices(): List<Vertex> {
    val vertices = List(size) { Vertex(it) }
    val verticesEdges = List(size) { mutableMapOf<Vertex, Edge>() }
    for ((v1, edges) in withIndex()) {
        val vertex1 = vertices[v1]
        for ((v2, maxFlow) in edges) {
            val vertex2 = vertices[v2]
            verticesEdges[v1] += vertex2 to Edge(maxFlow, isReversed = false)
            verticesEdges[v2] += vertex1 to Edge(maxFlow, isReversed = true)
        }
    }
    vertices.zip(verticesEdges).forEach { (v, edges) -> v.edges = edges }
    return vertices
}

private fun List<Vertex>.toGraph(): Graph = map { v1 ->
    v1.edges
            .filterValues { it.currentFlow > 0 }
            .mapKeys { (v2) -> indexOf(v2) }
            .mapValues { (_, edge) -> edge.currentFlow }
}