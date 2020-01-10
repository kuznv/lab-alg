package lab2

internal data class Vertex(var index: Int, var distance: Int?) : Comparable<Vertex> {
    var previous: Vertex? = null; internal set

    operator fun component3() = previous
    override fun compareTo(other: Vertex) = comparator.compare(this, other)
    override fun equals(other: Any?) = other is Vertex && other.index == this.index
    override fun hashCode() = index
}

private val comparator = compareByDescending(nullsLast(), Vertex::distance)