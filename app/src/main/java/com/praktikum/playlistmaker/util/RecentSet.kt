package com.praktikum.playlistmaker.util

class RecentSet<T>(
    private val capacity: Int,
    private val id: (T) -> String,
) {
    private val map =
        object : LinkedHashMap<String, T>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, T>) = size > capacity
        }

    fun put(element: T) {
        map[id(element)] = element
    }

    fun toList(): List<T> = map.values.toList().reversed()

    companion object {
        fun <T> fromList(
            list: List<T>,
            capacity: Int,
            id: (T) -> String,
        ): RecentSet<T> {
            val set = RecentSet<T>(capacity, id)
            list.reversed().forEach { set.put(it) }
            return set
        }
    }
}
