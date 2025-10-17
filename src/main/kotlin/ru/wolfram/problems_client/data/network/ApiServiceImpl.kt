package ru.wolfram.problems_client.data.network

import io.ktor.http.*
import ru.wolfram.problems_client.domain.model.Solution
import ru.wolfram.problems_client.domain.model.Task
import ru.wolfram.problems_client.domain.model.User

class ApiServiceImpl(
    private val networkClient: NetworkClient
) : ApiService {
    override suspend fun register(username: String, password: String): Result<String> {
        return networkClient.request(
            path = "/register",
            method = Method.POST,
            headers = listOf(HEADER_CONTENT_TYPE),
            body = User(username, password)
        )
    }

    override suspend fun loadTasks(username: String, password: String): Result<Map<String, String>> {
        val result = networkClient.request<List<Task>>(
            path = "/tasks",
            method = Method.GET,
            headers = listOf(HEADER_CONTENT_TYPE),
            username = username,
            password = password
        )
        return result.mapCatching {
            it.associateBy({ it1 ->
                it1.name.replace(" ", "_")
            }, { it2 -> it2.descriptionMarkdown })
        }
    }

    override suspend fun solve(
        username: String,
        password: String,
        language: String,
        taskName: String,
        solution: String
    ): Result<String> {
        return networkClient.request(
            path = "/solve",
            method = Method.POST,
            headers = listOf(HEADER_CONTENT_TYPE),
            parameters = listOf(Parameter("name", taskName)),
            requestTimeoutMillis = ONE_HOUR_MILLIS,
            socketTimeoutMillis = ONE_HOUR_MILLIS,
            username = username,
            password = password,
            body = Solution(language, solution)
        )
    }

    override fun close() {
        networkClient.close()
    }

    companion object {
        val HEADER_CONTENT_TYPE = Header(HttpHeaders.ContentType, "application/json")
        const val ONE_HOUR_MILLIS = 1000 * 60 * 60L
    }
}