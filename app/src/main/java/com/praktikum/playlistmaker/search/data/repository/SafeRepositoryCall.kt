package com.praktikum.playlistmaker.search.data.repository

import com.praktikum.playlistmaker.search.data.model.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeRepositoryCall(block: suspend () -> T): Result<T> =
    try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Result.Error("Server error: ${e.message()}")
    } catch (e: IOException) {
        Result.Error("Network connection error: ${e.message ?: ""}")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown error occurred: ${e.message ?: ""}")
    }
