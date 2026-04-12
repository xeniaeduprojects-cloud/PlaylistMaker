package com.praktikum.playlistmaker.search.data.repository

import kotlinx.coroutines.CancellationException

@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeRepositoryCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
