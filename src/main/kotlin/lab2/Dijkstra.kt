package lab2

import util.Graph
import java.util.*

internal fun dijkstraHeap(graph: Graph, d: Int, v0: Int): List<Vertex> {
    val vertex0 = graph[v0]
    val vertices = List(graph.size) { v1 -> Vertex(v1, distance = vertex0[v1]) }
    vertices[v0].distance = 0

    val queue = DHeap<Vertex>(d)
    queue += vertices

    val visited = BitSet(graph.size)

    for (vertex1 in generateSequence(queue::poll)) {
        val (v1, d1) = vertex1
        visited[v1] = true

        for ((v2, e) in graph[v1]) {
            if (visited[v2]) continue

            val vertex2 = vertices[v2]
            val d2 = vertex2.distance
            val newDistance = (d1 ?: 0) + e

            if (d2 == null || d2 > newDistance) {
                vertex2.distance = newDistance
                vertex2.previous = vertex1
                queue.siftUp(queue.indexOf(vertex2))
            }
        }
    }

    return vertices
}

internal fun dijkstraLabels(graph: Graph, v0: Int): List<Vertex> {
    val vertex0 = graph[v0]
    val vertices = List(graph.size) { v1 -> Vertex(v1, distance = vertex0[v1]) }
    vertices[v0].distance = 0

    val visited = BitSet(graph.size)

    val maxNotVisitedVertices = generateSequence { vertices.filter { (v) -> !visited[v] }.max() }
    for (vertex1 in maxNotVisitedVertices) {
        val (v1, d1) = vertex1
        visited[v1] = true

        for ((v2, e) in graph[v1]) {
            if (visited[v2]) continue

            val vertex2 = vertices[v2]
            val d2 = vertex2.distance
            val newDistance = (d1 ?: 0) + e

            if (d2 == null || d2 > newDistance) {
                vertex2.distance = newDistance
                vertex2.previous = vertex1
            }
        }
    }

    return vertices
}