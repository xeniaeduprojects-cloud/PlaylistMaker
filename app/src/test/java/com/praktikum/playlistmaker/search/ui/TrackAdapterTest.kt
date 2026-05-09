package com.praktikum.playlistmaker.search.ui

import com.praktikum.playlistmaker.search.data.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackAdapterTest {
    @Test
    fun `diffCallback areItemsTheSame returns true for same trackId`() {
        val track1 = track(1, "Track 1")
        val track2 = track(1, "Track 1 Updated")
        val oldList = listOf(track1)
        val newList = listOf(track2)

        val diffCallback = createDiffCallback(oldList, newList)
        val result = diffCallback.areItemsTheSame(0, 0)

        assertTrue(result)
    }

    @Test
    fun `diffCallback areItemsTheSame returns false for different trackId`() {
        val track1 = track(1, "Track 1")
        val track2 = track(2, "Track 2")
        val oldList = listOf(track1)
        val newList = listOf(track2)

        val diffCallback = createDiffCallback(oldList, newList)
        val result = diffCallback.areItemsTheSame(0, 0)

        assertFalse(result)
    }

    @Test
    fun `diffCallback areContentsTheSame returns true for identical tracks`() {
        val track1 = track(1, "Track 1")
        val track2 = track(1, "Track 1")
        val oldList = listOf(track1)
        val newList = listOf(track2)

        val diffCallback = createDiffCallback(oldList, newList)
        val result = diffCallback.areContentsTheSame(0, 0)

        assertTrue(result)
    }

    @Test
    fun `diffCallback areContentsTheSame returns false for different content`() {
        val track1 = track(1, "Track 1")
        val track2 = track(1, "Track 1 Updated")
        val oldList = listOf(track1)
        val newList = listOf(track2)

        val diffCallback = createDiffCallback(oldList, newList)
        val result = diffCallback.areContentsTheSame(0, 0)

        assertFalse(result)
    }

    @Test
    fun `diffCallback getOldListSize returns correct size`() {
        val oldList = listOf(track(1, "Track 1"), track(2, "Track 2"))
        val newList = listOf(track(3, "Track 3"))

        val diffCallback = createDiffCallback(oldList, newList)
        val size = diffCallback.oldListSize

        assertEquals(2, size)
    }

    @Test
    fun `diffCallback getNewListSize returns correct size`() {
        val oldList = listOf(track(1, "Track 1"))
        val newList = listOf(track(2, "Track 2"), track(3, "Track 3"))

        val diffCallback = createDiffCallback(oldList, newList)
        val size = diffCallback.newListSize

        assertEquals(2, size)
    }

    private fun track(
        id: Long,
        name: String,
    ): Track =
        Track(
            trackId = id,
            trackName = name,
            artistName = "Artist",
            trackTime = "3:00",
            artworkUrl100 = "https://example.com/artwork.jpg",
        )

    private fun createDiffCallback(
        oldList: List<Track>,
        newList: List<Track>,
    ) = TrackAdapter::class.java
        .declaredClasses
        .first { it.simpleName == "TrackDiffCallback" }
        .getDeclaredConstructor(List::class.java, List::class.java)
        .newInstance(oldList, newList) as androidx.recyclerview.widget.DiffUtil.Callback
}
