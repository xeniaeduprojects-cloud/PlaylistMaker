package com.praktikum.playlistmaker.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentSetTest {
    @Test
    fun `create with size 3, place 2 elements, check the order of list`() {
        val recentSet = RecentSet<Int, String>(capacity = 3) { it.toInt() }

        recentSet.put("1")
        recentSet.put("2")

        val result = recentSet.toList()

        assertEquals(2, result.size)
        assertEquals("2", result[0])
        assertEquals("1", result[1])
    }

    @Test
    fun `create with size 3, place 5 elements, check that only 3 last left`() {
        val recentSet = RecentSet<Int, String>(capacity = 3) { it.toInt() }

        recentSet.put("1")
        recentSet.put("2")
        recentSet.put("3")
        recentSet.put("4")
        recentSet.put("5")

        val result = recentSet.toList()

        assertEquals(3, result.size)
        assertEquals("5", result[0])
        assertEquals("4", result[1])
        assertEquals("3", result[2])
    }

    @Test
    fun `create with size 3, place 3, then add 2nd the second time, check that there are still 3 and order persist`() {
        val recentSet = RecentSet<Int, String>(capacity = 3) { it.toInt() }

        recentSet.put("1")
        recentSet.put("2")
        recentSet.put("3")
        recentSet.put("2")

        val result = recentSet.toList()

        assertEquals(3, result.size)
        assertEquals("2", result[0])
        assertEquals("3", result[1])
        assertEquals("1", result[2])
    }

    @Test
    fun `create from list with capacity 3`() {
        val list = listOf("1", "2", "3", "4", "5")

        val recentSet =
            RecentSet.fromList(
                list = list,
                capacity = 3,
                id = { it.toInt() },
            )

        val result = recentSet.toList()

        assertEquals(3, result.size)
        assertEquals("1", result[0])
        assertEquals("2", result[1])
        assertEquals("3", result[2])
    }

    @Test
    fun `test on empty recentSet`() {
        val recentSet = RecentSet<Int, String>(capacity = 3) { it.toInt() }

        val result = recentSet.toList()

        assertTrue(result.isEmpty())
        assertEquals(0, result.size)
    }
}
