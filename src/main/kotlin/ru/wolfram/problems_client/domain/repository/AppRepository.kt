package ru.wolfram.problems_client.domain.repository

interface AppRepository {
    suspend fun register(): Result<String>

    suspend fun loadTasks(): Result<String>

    suspend fun solve(
        language: String,
        taskName: String
    ): Result<String>

    fun close()
}