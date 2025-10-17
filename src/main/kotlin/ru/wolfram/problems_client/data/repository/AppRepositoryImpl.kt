package ru.wolfram.problems_client.data.repository

import ru.wolfram.problems_client.data.network.ApiService
import ru.wolfram.problems_client.domain.exception.UnsupportedLanguageException
import ru.wolfram.problems_client.domain.model.Configuration
import ru.wolfram.problems_client.domain.repository.AppRepository
import java.io.File

class AppRepositoryImpl(
    private val apiService: ApiService,
    private val configuration: Configuration
) : AppRepository {
    override suspend fun register(): Result<String> {
        return apiService.register(configuration.username, configuration.password)
    }

    override suspend fun loadTasks(): Result<String> {
        return apiService.loadTasks(configuration.username, configuration.password).mapCatching { nameToTask ->
            nameToTask.forEach { entry ->
                File("tasks/${entry.key}.md").writeText(entry.value)
            }
            "Tasks were loaded successfully!"
        }
    }

    override suspend fun solve(
        language: String,
        taskName: String
    ): Result<String> {
        return try {
            val solutionApiName = taskName.replace("_", "%20")
            val ext = when (language.trim()) {
                "java" -> "java"
                "scala" -> "scala"
                "kotlin" -> "kt"
                "c++" -> "cpp"
                "c" -> "c"
                "go", "golang" -> "go"
                else -> {
                    null
                }
            }
            if (ext == null) {
                Result.failure(UnsupportedLanguageException())
            } else {
                val solution = File("solutions/$taskName.$ext").readText()
                apiService.solve(configuration.username, configuration.password, language, solutionApiName, solution)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun close() {
        apiService.close()
    }
}