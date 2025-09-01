package ru.wolfram.problems_client

import com.github.ajalt.mordant.markdown.Markdown
import com.github.ajalt.mordant.terminal.Terminal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.reflect.Type

class Application {
    companion object {
        private val gson = Gson()
        private var tasks = listOf<Task>()
        private var taskNameToDescription = HashMap<String, String>()
        private val listType: Type? = object : TypeToken<List<Task?>?>() {}.type
        private val client = HttpClient {}
        private val configuration = getConfiguration()
        private val terminal = Terminal()

        @JvmStatic
        @OptIn(InternalAPI::class)
        fun main(args: Array<String>) = runBlocking {
            try {
                register()
                while (true) {
                    val input = readlnOrNull()?.trim()
                    if (input != null) {
                        when (input) {
                            "get" -> {
                                handleGet()
                            }

                            "quit", "exit" -> break

                            else -> {
                                val parts = input.split("\\s+".toRegex())
                                when (parts[0]) {
                                    "solve" -> {
                                        if (parts.size > 1) {
                                            handleSolve(parts[1], parts[2])
                                        }
                                    }

                                    "show" -> {
                                        if (parts.size > 1) {
                                            handleShow(parts[1])
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                client.close()
            }
        }

        private fun getConfiguration(): Configuration {
            val configurationString = File("src/main/resources/configuration.json").readText()
            return gson.fromJson(configurationString, Configuration::class.java)
        }

        private fun handleShow(taskName: String) {
            terminal.println(Markdown(taskNameToDescription[taskName] ?: TODO()))
        }

        @OptIn(InternalAPI::class)
        private suspend fun register() {
            client.post("${configuration.prefixApi}/register") {
                body = "{\"username\": \"${configuration.username}\", \"password\": \"${configuration.password}\"}"
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }.rawContent.readBuffer.readText().also(::println)
        }

        @OptIn(InternalAPI::class)
        private suspend fun handleGet() {
            tasks = gson.fromJson(client.get("${configuration.prefixApi}/tasks") {
                basicAuth(configuration.username, configuration.password)
            }.rawContent.readBuffer.readText(), listType)
            taskNameToDescription = HashMap(tasks.associateBy({ it.name.replace(" ", "_") }, { it.descriptionMarkdown }))
            println(tasks.joinToString(separator = ", ") { task -> task.name })
        }

        @OptIn(InternalAPI::class)
        private suspend fun handleSolve(language: String, taskName: String) {
            val solutionApiName = taskName.replace("_", "%20")
            val ext = when (language.trim()) {
                "java" -> "java"
                "scala" -> "scala"
                else -> {
                    println("Unsupported language: $language")
                    return
                }
            }
            val solution = File("solutions/$taskName.$ext").readText()
            client.post("${configuration.prefixApi}/solve?name=$solutionApiName") {
                timeout {
                    requestTimeoutMillis = 1000 * 60 * 60
                }
                basicAuth(configuration.username, configuration.password)
                body = Json.encodeToString(Solution(language, solution))
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }.rawContent.readBuffer.readText().also(::println)
        }
    }
}