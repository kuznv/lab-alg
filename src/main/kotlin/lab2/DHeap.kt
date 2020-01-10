package lab2

import sun.text.normalizer.UTF16.append
import util.pow
import util.swap
import java.util.*
import kotlin.math.log

open class DHeap<E : Comparable<E>>(val d: Int) : AbstractQueue<E>() {
    val heap = mutableListOf<E>()
    override val size get() = heap.size

    override fun offer(e: E): Boolean {
        heap += e
        siftUp(heap.lastIndex)
        return true
    }

    override fun peek(): E? = heap.firstOrNull()
    override fun poll(): E? = if (isEmpty()) null else remove(0)

    override fun iterator() = object : MutableIterator<E> {
        private var currentIndex = 0

        override fun hasNext() = currentIndex < heap.size
        override fun next() = heap[currentIndex++]

        override fun remove() {
            remove(--currentIndex)
        }
    }

    private fun parentIndex(index: Int) = (index - 1) / d

    tailrec fun siftUp(index: Int) {
        val parentIndex = parentIndex(index)
        val parent = heap[parentIndex]
        val element = heap[index]
        if (parent < element) {
            heap.swap(index, parentIndex)
            siftUp(parentIndex)
        }
    }

    tailrec fun siftDown(index: Int) {
        val element = heap[index]
        val childrenMinIndex = (index * d + 1).coerceAtMost(heap.lastIndex)
        val childrenHighIndex = (index * d + d).coerceAtMost(heap.lastIndex)
        val children = heap.slice(childrenMinIndex..childrenHighIndex)
        val maxChildren = children.withIndex().maxBy { it.value } ?: return
        if (element < maxChildren.value) {
            val childrenIndex = childrenMinIndex + maxChildren.index
            heap.swap(index, childrenIndex)
            siftDown(childrenIndex)
        }
    }

    fun sift(index: Int) {
        val parentIndex = parentIndex(index)
        val parent = heap[parentIndex]
        val current = heap[index]
        if (parent < current) siftUp(index)
        else siftDown(index)
    }

    private fun remove(index: Int): E {
        val last = heap.removeAt(heap.lastIndex)
        if (index == heap.lastIndex + 1) return last
        val removed = heap[index]
        heap[index] = last
        sift(index)
        return removed
    }
}

fun DHeap<Int>.toString2() = buildString {
    val width = 4
    val depth = log(size.toDouble(), d.toDouble()).toInt() + 1
    val last = d pow (depth - 1)
    val values = iterator()
    var curr = 1
    for (layer in 1..depth) {
        val layerValues = values.asSequence().take(curr)
        val indent = width * last / curr - width
        append(" ".repeat(indent / 2))
        var remaining = last * width
        layerValues.forEachIndexed { i, num ->
            val spaces = remaining / (curr - i) - width
            remaining -= spaces + width
            append("% ${width - 1}d".format(num))
            append(" ".repeat(spaces + 1))
        }
        appendln()
        curr *= d
    }
}