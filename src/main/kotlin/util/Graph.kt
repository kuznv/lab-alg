package util

import util.AnsiCode.Color.BLUE
import util.AnsiCode.Color.GREEN
import util.AnsiCode.Intensity.HIGH_INTENSITY
import util.AnsiCode.SANE
import java.util.*

typealias Graph = List<Map<Int, Int>>

fun Graph.toString2(): String {
    var row = 0
    return joinToString(
            prefix = indices.joinToString(prefix = "$BLUE$HIGH_INTENSITY\t", separator = "\t", postfix = "$SANE\n"),
            separator = "\n",
            transform = { v1 ->
                indices.joinToString(
                        prefix = "$BLUE$HIGH_INTENSITY${row++}$SANE\t",
                        separator = "\t",
                        transform = { v2 -> v1[v2]?.toString()?.let { GREEN(it) } ?: "∞" }
                )
            }
    )
}

fun randomGraph(nVertices: Int, nEdges: Int, weightRange: IntRange, random: Random): Graph {
    require(nEdges >= nVertices) { "edges < vertices" }
    require(nEdges <= nVertices * (nVertices - 1)) { "edges > vertices * (vertices - 1)" }

    val graph = List(nVertices) { mutableMapOf<Int, Int>() }

    val makeConnection = { v1: Int, v2: Int ->
        val w = random.nextInt(weightRange)
        graph[v1][v2] = w
        graph[v2][v1] = w
    }

    (0 until nVertices).shuffled(random).zipWithNext(makeConnection)

    val randomVertices = generateSequence { random.nextInt(nVertices) }.iterator()

    val edgesNow = nVertices - 1
    val edgesToAdd = (nEdges - edgesNow) / 2
    graph.foldIndexed(edgesToAdd) { v1, edgesLeft, vs ->
        val edges = edgesLeft / (graph.size - v1)

        randomVertices
                .asSequence()
                .filter { v2 -> v2 != v1 && v2 !in vs }
                .take(edges)
                .forEach { v2 -> makeConnection(v1, v2) }

        edgesLeft - edges
    }

    return graph.map { TreeMap(it) }
}