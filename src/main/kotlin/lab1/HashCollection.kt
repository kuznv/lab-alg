package lab1

import java.util.*

class HashCollection<E>(tableSize: Int) : AbstractMutableCollection<E>() {
    private val hashTable = arrayOfNulls<LinkedList2<E>>(tableSize)
    override var size = 0; private set

    internal var comparisons = 0; private set
    internal fun getCollisions() = hashTable.filterNotNull().sumBy { it.size - 1 }

    override fun contains(element: E): Boolean {
        val hash = Objects.hashCode(element)
        val cell = hashTable[hash % hashTable.size] ?: return false
        return cell.any { ++comparisons; it == element }
    }

    override fun add(element: E): Boolean {
        val hash = Objects.hashCode(element)
        val index = hash % hashTable.size
        val cell = hashTable[index]
        if (cell != null)
            cell.add(element)
        else
            hashTable[index] = LinkedList2<E>().apply { add(element) }
        ++size
        return true
    }

    override fun iterator(): MutableIterator<E> =
            object : MutableIterator<E>, Iterator<E> by hashTable.filterNotNull().flatten().iterator() {
                override fun remove() = throw NotImplementedError()
            }

    override fun clear() {
        hashTable.fill(null)
        size = 0
        comparisons = 0
    }

    override fun toString() = hashTable.joinToString(separator = "\n") { cell ->
        cell?.joinToString() ?: "-"
    }
}