package ru.wolfram.problems_client.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.*
import io.ktor.http.headers
import ru.wolfram.problems_client.domain.model.Configuration

class NetworkClient(
    val client: HttpClient,
    val configuration: Configuration
) {
    fun close() {
        client.close()
    }

    suspend inline fun <reified T> request(
        path: String,
        method: Method,
        headers: List<Header> = listOf(),
        parameters: List<Parameter> = listOf(),
        requestTimeoutMillis: Long? = null,
        socketTimeoutMillis: Long? = null,
        username: String? = null,
        password: String? = null,
        body: Any? = null
    ): Result<T> {
        val url = "${configuration.prefixApi}$path"
        return try {
            val data = when (method) {
                Method.GET -> {
                    client.get(url) {
                        setUp(headers, parameters, username, password, requestTimeoutMillis, socketTimeoutMillis)
                    }
                }

                Method.POST -> {
                    client.post(url) {
                        setUp(headers, parameters, username, password, requestTimeoutMillis, socketTimeoutMillis)
                        if (body != null) {
                            setBody(body)
                        }
                    }
                }
            }
            val result = data.body<T>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun HttpRequestBuilder.setUp(
        headers: List<Header> = listOf(),
        parameters: List<Parameter> = listOf(),
        username: String? = null,
        password: String? = null,
        requestTimeoutMillis: Long? = null,
        socketTimeoutMillis: Long? = null
    ) {
        if (username != null && password != null) {
            basicAuth(username, password)
        }
        headers {
            headers.forEach {
                header(it.name, it.value)
            }
        }
        parameters {
            parameters.forEach {
                parameter(it.name, it.value)
            }
        }
        timeout {
            requestTimeoutMillis?.let {
                this.requestTimeoutMillis = it
            }
            socketTimeoutMillis?.let {
                this.socketTimeoutMillis = it
            }
        }
    }
}