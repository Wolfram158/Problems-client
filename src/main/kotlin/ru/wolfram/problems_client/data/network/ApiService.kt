package ru.wolfram.problems_client.data.network

interface ApiService {
    suspend fun register(username: String, password: String): Result<String>

    suspend fun loadTasks(username: String, password: String): Result<Map<String, String>>

    suspend fun solve(
        username: String,
        password: String,
        language: String,
        taskName: String,
        solution: String
    ): Result<String>

    fun close()
}