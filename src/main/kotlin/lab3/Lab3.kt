package lab3

import chart.Chart
import chart.canvas.Canvas
import chart.canvas.CanvasElement
import chart.canvas.CanvasView
import chart.drawable.Drawable
import chart.drawable.TextStyle
import chart.drawable.drawables.ChartPoint
import chart.drawable.drawables.GraphAxis
import chart.drawable.drawables.GraphPanel
import lab3.boruwka.boruwka
import lab3.prim.prim
import util.*
import util.AnsiCode.Color.*
import util.AnsiCode.SANE
import java.util.*
import kotlin.system.measureTimeMillis

private fun getTestGraph1(): Graph = listOf(
        // @formatter:off
        //              a         b         c         d         e         f         g
        /* a */ mapOf(          1 to  7,            3 to  4                              ),
        /* b */ mapOf(0 to  7,            2 to 11,  3 to  9,  4 to 10                    ),
        /* c */ mapOf(          1 to 11,                      4 to  5                    ),
        /* d */ mapOf(0 to  4,  1 to  9,                      4 to 15,  5 to  6          ),
        /* e */ mapOf(          1 to 10,  2 to  5,  3 to 15,            5 to 12,  6 to  8),
        /* f */ mapOf(                              3 to  6,  4 to 12,            6 to 13),
        /* g */ mapOf(                                        4 to  8,  5 to 13          )
        // @formatter:on
)

private fun getRandomGraph() = randomGraph(
        nVertices = 10,
        nEdges = 30,
        weightRange = 1..9,
        random = Random(0)
)

private const val randomSeed = 0L
private val weight = 1..1_000_000

private const val nVerticesMin = 100
private const val nVerticesMax = 10_000
private const val nVerticesStep = 100

private val nEdgesBoruwka = { nVertices: Int -> nVertices * nVertices / 100 }
private val nEdgesPrim = { nVertices: Int -> nVertices * nVertices / 1_000 }

fun main(args: Array<String>) {
//    solve()
    plot()
}

private fun solve() {
//    val graph = getRandomGraph()
    val graph = getTestGraph1()
    println(graph.toString2())

    lateinit var resultGraph1: Graph
    lateinit var resultGraph2: Graph
    val time1 = measureTimeMillis {
        resultGraph1 = boruwka(graph)
    }
    val time2 = measureTimeMillis {
        resultGraph2 = prim(graph)
    }

    println()
    println("Boruwka: $time1 ms.")
    println(resultGraph1.toString2())
    println("Prim: $time2 ms.")
    println(resultGraph2.toString2())

    val totalWeight1 = resultGraph1.sumBy { it.values.sum() }
    val totalWeight2 = resultGraph2.sumBy { it.values.sum() }

    println("Boruwka weight: $totalWeight1")
    println("Prim weight: $totalWeight2")
}

private fun plot() {
    val boruwkaStyle = CanvasElement("+", TextStyle(YELLOW))
    val primStyle = CanvasElement("*", TextStyle(MAGENTA))
    val genStyle = CanvasElement("·", TextStyle(WHITE))

    var totalGeneratingMillis = 0L
    var totalBoruwkaMillis = 0L
    var totalPrimMillis = 0L

    val drawables = ArrayList<Drawable>((nVerticesMax - nVerticesMin) / nVerticesStep * 3 + 2)

    val totalMillis = measureTimeMillis {
        for (nVertices in nVerticesMin..nVerticesMax step nVerticesStep) {
            val nBoruwkaEdges = nEdgesBoruwka(nVertices).coerceIn(nVertices..nVertices * nVertices)
            val nPrimEdges = nEdgesPrim(nVertices).coerceIn(nVertices..nVertices * nVertices)
            lateinit var graphHeap: Graph
            lateinit var graphLabels: Graph
            val generatingMillis = measureTimeMillis {
                graphHeap = randomGraph(nVertices, nBoruwkaEdges, weight, Random(randomSeed))
                graphLabels = randomGraph(nVertices, nPrimEdges, weight, Random(randomSeed))
            }
            val heapMillis = measureTimeMillis {
                boruwka(graphHeap)
            }
            val labelsMillis = measureTimeMillis {
                prim(graphLabels)
            }
            totalGeneratingMillis += generatingMillis
            totalBoruwkaMillis += heapMillis
            totalPrimMillis += labelsMillis

            drawables += ChartPoint(
                    x = nVertices.toDouble(),
                    y = heapMillis.toDouble(),
                    canvasElement = boruwkaStyle
            )
            drawables += ChartPoint(
                    x = nVertices.toDouble(),
                    y = labelsMillis.toDouble(),
                    canvasElement = primStyle
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
        |Boruwka: $GREEN$totalBoruwkaMillis$SANE ms.
        |Prim: $GREEN$totalPrimMillis$SANE ms.
        |Total: $WHITE$totalMillis$SANE ms.
        """.trimMargin())

    drawables += GraphAxis(TextStyle(BLUE), decimalFormat, top = false, right = false)
    drawables += GraphPanel(canvasElements = listOf(
            boruwkaStyle.toLabel("Boruwka time, ms"),
            primStyle.toLabel("Prim time, ms"),
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
