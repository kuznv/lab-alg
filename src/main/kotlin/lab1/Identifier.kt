package lab1

class Identifier(private val chars: CharSequence) : CharSequence by chars {
    override fun equals(other: Any?) = other is Identifier && other.chars == this.chars
    override fun hashCode() = this[0].toInt() + this[1].toInt() + last().toInt()
    override fun toString() = chars.toString()
}