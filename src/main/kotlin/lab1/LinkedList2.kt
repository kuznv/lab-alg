package lab1

class LinkedList2<E> : AbstractMutableList<E>() {
    private inner class Entry(var value: E, var next: Entry? = null)

    private var head: Entry? = null
    override var size = 0; private set

    private fun indexError(index: Int): Nothing = throw IndexOutOfBoundsException("Index is $index, size is $size")

    private fun getEntry(index: Int): Entry {
        var current = head ?: indexError(index)
        repeat(index) {
            current = current.next ?: indexError(index)
        }
        return current
    }

    override fun add(index: Int, element: E) {
        if (index == 0) {
            head = Entry(element)
        } else {
            val prev = getEntry(index - 1)
            val next = prev.next
            prev.next = Entry(element, next)
        }
        ++size
    }

    override fun get(index: Int): E = getEntry(index).value

    override fun removeAt(index: Int): E {
        val removed: Entry
        if (index == 0) {
            removed = head ?: indexError(index)
            head = removed.next
        } else {
            val prev = getEntry(index - 1)
            removed = prev.next ?: indexError(index)
            prev.next = removed.next
        }
        --size
        return removed.value
    }

    override fun set(index: Int, element: E): E {
        val entry = getEntry(index)
        val oldValue = entry.value
        entry.value = element
        return oldValue
    }
}