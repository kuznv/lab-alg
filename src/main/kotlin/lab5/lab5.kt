package lab5

import java.util.*
import kotlin.coroutines.experimental.buildIterator

fun main(args: Array<String>) {
    val officials = randomOfficials(
            random = Random(0),
            maxVisaCost = 5,
            nRequiredOfficials = 15,
            officialsCount = 30,
            visasCount = 3
    )
    officials.first().cost
    printResult(officials)
    printResult2(officials)
}

private fun randomOfficials(
        random: Random,
        maxVisaCost: Int,
        nRequiredOfficials: Int,
        officialsCount: Int,
        visasCount: Int
): List<Official> {
    val officials = List(officialsCount, ::Official)
    for ((i, official) in officials.withIndex()) {
        official.visas += List(visasCount) {
            Official.Visa(
                    cost = random.nextInt(maxVisaCost),
                    requiredOfficials = officials.drop(i + 1).shuffled(random).take(nRequiredOfficials).toSet()
            )
        }
    }
    return officials
}

private fun printResult(officials: List<Official>) {
    val visited = BitSet(officials.size)

    fun printOfficial(official: Official, indent: Int) {
        if (visited[official.n]) return
        visited[official.n] = true
        val visa = official.minVisa
        println(" ".repeat(indent * 3) +
                "official #${official.n}: " +
                "visa = ${visa?.cost}" +
                "${visa?.requiredOfficials?.takeIf { it.isNotEmpty() }?.joinToString(prefix = " + ") { "#${it.n}(${it.cost})" }
                        ?: ""} ")
        visa?.requiredOfficials?.forEach { printOfficial(it, indent + 1) }
    }

    printOfficial(officials.first(), indent = 0)
}

private fun printResult2(officials: List<Official>) {
    val visited = BitSet(officials.size)

    fun visit(official: Official) {
        if (visited[official.n]) return
        visited[official.n] = true
        official.minVisa?.requiredOfficials?.forEach(::visit)
        println("official #${official.n}: cost=${official.cost}")
    }
    visit(officials.first())
}

class Official(val n: Int) {
    val visas = mutableListOf<Visa>()
    var minVisa: Visa? = null //; private set

    val cost: Int by lazy {
        if (visas.isEmpty()) 0
        else {
            while (true) {
                val minVisa = visas.minBy { it.fullCost }!!
                this@Official.minVisa = minVisa
                if (!minVisa.currentCost.hasNext()) break
                minVisa.currentCost.next()
            }
            minVisa!!.fullCost
        }
    }

    class Visa(val cost: Int, val requiredOfficials: Set<Official>) {
        var fullCost = cost //; private set

        val currentCost = buildIterator {
            for (official in requiredOfficials) {
                fullCost += official.cost
                yield(fullCost)
            }
        }
    }
}
