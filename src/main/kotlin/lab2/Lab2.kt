package lab2

import chart.Chart
import chart.canvas.Canvas
import chart.canvas.CanvasElement
import chart.canvas.CanvasView
import chart.drawable.Drawable
import chart.drawable.TextStyle
import chart.drawable.drawables.ChartPoint
import chart.drawable.drawables.GraphAxis
import chart.drawable.drawables.GraphPanel
import util.*
import util.AnsiCode.Color.*
import util.AnsiCode.SANE
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

private fun getTestGraph1(): Graph = listOf(
        // @formatter:off
        //      1     2     3     4     5     6
        listOf(null, 7   , 9   , null, null, 14  ),
        listOf(7   , null, 10  , 15  , null, null),
        listOf(9   , 10  , null, 11  , null, 2   ),
        listOf(null, 15  , 11  , null, 6   , null),
        listOf(null, null, null, 6   , null, 9   ),
        listOf(14  , null, 2   , null, 9   , null)
        // @formatter:on
).toGraph()

private fun getTestGraph2(): Graph = listOf(
        // @formatter:off
        //      1     2     3     4     5     6     7     8      9
        listOf(null,    3, null, null,    7, null, null, null, null),
        listOf(   3, null,    4,    9, null, null, null, null, null),
        listOf(null,    4, null, null, null,    2,    8, null, null),
        listOf(null,    9, null, null, null,    7, null, null,    4),
        listOf(   7, null, null, null, null, null,    1, null, null),
        listOf(null, null,    2,    7, null, null,    1,    7, null),
        listOf(null, null,    8, null,    1,    1, null,    2, null),
        listOf(null, null, null, null, null,    7,    2, null, null),
        listOf(null, null, null,    4, null, null, null, null, null)
        // @formatter:on
).toGraph()

private fun List<List<Int?>>.toGraph() = map { it.mapIndexedNotNull { i, w -> w?.let { i to it } }.toMap() }

private fun getRandomGraph() = randomGraph(
        nVertices = 100,
        nEdges = 300,
        weightRange = 1..9,
        random = Random(0)
)

private const val randomSeed = 0L
private const val v0 = 0
private const val d = 3
private val weight = 1..1_000_000

private const val nVerticesMin = 10_000
private const val nVerticesMax = 10_000
private const val nVerticesStep = 1_000

fun main(args: Array<String>) {
//    solveGraph()
    plotGraph()
}

private fun solveGraph() {
//        val graph = getRandomGraph()
//    val graph = getTestGraph1()
    val graph = getTestGraph2()

    println(graph.toString2())
    solveLabels(graph)
    println()
    solveHeap(graph)
}

private fun solveHeap(graph: Graph) {
    printResult(dijkstraHeap(graph, d, v0), v0)
}

private fun solveLabels(graph: Graph) {
    printResult(dijkstraLabels(graph, v0), v0)
}

private fun plotGraph() {
    val heapStyle = CanvasElement("+", TextStyle(YELLOW))
    val labelsStyle = CanvasElement("*", TextStyle(MAGENTA))
    val genStyle = CanvasElement("·", TextStyle(WHITE))

    var totalGeneratingMillis = 0L
    var totalHeapMillis = 0L
    var totalLabelsMillis = 0L

    val drawables = ArrayList<Drawable>((nVerticesMax - nVerticesMin) / nVerticesStep * 3 + 2)

    val totalMillis = measureTimeMillis {
        for (nVertices in nVerticesMin..nVerticesMax step nVerticesStep) {
            val nHeapEdges = (nVertices * nVertices / 100).coerceIn(nVertices - 1..nVertices * nVertices)
            val nLabelsEdges = (nVertices * nVertices / 1000).coerceIn(nVertices - 1..nVertices * nVertices)
            lateinit var graphHeap: Graph
            lateinit var graphLabels: Graph
            val generatingMillis = measureTimeMillis {
                graphHeap = randomGraph(nVertices, nHeapEdges, weight, Random(randomSeed))
                graphLabels = randomGraph(nVertices, nLabelsEdges, weight, Random(randomSeed))
            }
            val heapMillis = measureTimeMillis {
                dijkstraHeap(graphHeap, d, v0)
            }
            val labelsMillis = measureTimeMillis {
                dijkstraLabels(graphLabels, v0)
            }
            totalGeneratingMillis += generatingMillis
            totalHeapMillis += heapMillis
            totalLabelsMillis += labelsMillis

            drawables += ChartPoint(
                    x = nVertices.toDouble(),
                    y = heapMillis.toDouble(),
                    canvasElement = heapStyle
            )
            drawables += ChartPoint(
                    x = nVertices.toDouble(),
                    y = labelsMillis.toDouble(),
                    canvasElement = labelsStyle
            )
            drawables += ChartPoint(
                    x = nVertices.toDouble(),
                    y = generatingMillis.toDouble(),
                    canvasElement = genStyle
            )
        }
    }
    println("""
        |Generating: $GREEN$totalGeneratingMillis$SANE ms.
        |Heap: $GREEN$totalHeapMillis$SANE ms.
        |Labels: $GREEN$totalLabelsMillis$SANE ms.
        |Total: $WHITE$totalMillis$SANE ms.
        """.trimMargin())

    drawables += GraphAxis(TextStyle(BLUE), decimalFormat, top = false, right = false)
    drawables += GraphPanel(canvasElements = listOf(
            heapStyle.toLabel("Heap time, ms"),
            labelsStyle.toLabel("Labels time, ms"),
            genStyle.toLabel("Generating time, ms")
    ))
    val view = CanvasView(
            Canvas(height = 41, width = 182),
            xRange = 0.0..nVerticesMax.toDouble(),
            yRange = -50.0..700.0
    )
    val chart = Chart(drawables, view)
    showChartMenu(chart, chartFunctions = emptyList())
}

private fun printResult(vertices: List<Vertex>, v0: Int) {
    vertices.forEach { (i, d) ->
        generateSequence(vertices[i], Vertex::previous).forEach {
            print("${it.index} <- ")
        }
        println("$v0 ($d)")
    }
}