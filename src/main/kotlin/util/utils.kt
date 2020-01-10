package util

fun <T, S : MutableSet<T>> T.dfsTo(destination: S, transform: (T) -> Iterable<T>): S {
    if (destination.add(this)) {
        transform(this).forEach { it.dfsTo(destination, transform) }
    }
    return destination
}

fun <T> T.dfs(transform: (T) -> Iterable<T>): Set<T> = dfsTo(mutableSetOf(), transform)