package com.praktikum.playlistmaker.util

class RecentSet<K, T>(
    private val capacity: Int,
    private val id: (T) -> K,
) {
    private val map =
        object : LinkedHashMap<K, T>(capacity, LOAD_FACTOR, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, T>) = size > capacity
        }

    fun put(element: T) {
        map[id(element)] = element
    }

    fun toList(): List<T> = map.values.toList().reversed()

    companion object {
        private const val LOAD_FACTOR = 0.75f

        fun <K, T> fromList(
            list: List<T>,
            capacity: Int,
            id: (T) -> K,
        ): RecentSet<K, T> {
            val set = RecentSet<K, T>(capacity, id)
            list.reversed().forEach { set.put(it) }
            return set
        }
    }
}
