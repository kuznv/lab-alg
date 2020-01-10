package lab1

import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis

val loremFile = File("lorem.txt")
val inputFile = File("ids.txt")
//val inputFile = loremFile
val alphabet = ('a'..'z').toList() + ('A'..'Z').toList()
val random = Random(0)
const val lineLength = 3
const val tableSize = 100

fun main(args: Array<String>) {
    generateIdentifiers(1000)

    val ids = inputFile.readLines().map(::Identifier)
    val linesCount = ids.size
    val collection = HashCollection<Identifier>(tableSize)

    val write = measureTimeMillis {
        collection += ids
    }
    println("Write = $write ms, avg = ${write.toDouble() / linesCount} ms")
    val collisions = collection.getCollisions()
    println("Collisions = $collisions, avg = ${collisions.toDouble() / linesCount}")

    val read = measureTimeMillis {
        for (id in ids) {
            assert(id in collection)
        }
    }
    println("Read = $read ms, avg = ${read.toDouble() / linesCount} ms")
    val comparisons = collection.comparisons
    println("Comparisons = $comparisons, avg = ${comparisons.toDouble() / linesCount}")

    println(collection)
    searchForId(collection)
}

private fun searchForId(collection: HashCollection<Identifier>) {
    print("Enter id to search: ")
    generateSequence { readLine()?.takeIf { it.isNotBlank() } }.map(::Identifier).forEach { id ->
        val comparisonsBefore = collection.comparisons
        println("'$id' in collection: ${id in collection}")
        val comparisonsAfter = collection.comparisons
        println("Comparisons=${comparisonsAfter - comparisonsBefore}")
        print("Enter next id: ")
    }
}

private fun generateIdentifiers(linesCount: Int) {
    loremFile.delete()
    loremFile.bufferedWriter().use { writer ->
        repeat(linesCount) {
            val line = List(lineLength) {
                alphabet[random.nextInt().absoluteValue % alphabet.size]
            }.joinToString(separator = "")
            writer.appendln(line)
        }
    }
}