package ru.wolfram.problems_client.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.wolfram.problems_client.di.AppComponent
import ru.wolfram.problems_client.domain.repository.AppRepository

class AppModel(
    private val repository: AppRepository
) {
    private val scope = AppComponent.provideScope()
    private val error = MutableSharedFlow<String>(replay = 1)
    private val result = MutableSharedFlow<String>(replay = 1)

    init {
        register()
    }

    fun register() {
        scope.launch {
            handleResult(repository.register())
        }
    }

    fun loadTasks() {
        scope.launch {
            handleResult(repository.loadTasks())
        }
    }

    fun solve(language: String, taskName: String) {
        scope.launch {
            handleResult(repository.solve(language, taskName))
        }
    }

    fun close() {
        repository.close()
    }

    fun observe(
        onResult: (result: String) -> Unit,
        onError: (error: String) -> Unit
    ) {
        scope.launch {
            result.collect {
                onResult(it)
            }
        }
        scope.launch {
            error.collect {
                onError(it)
            }
        }
    }

    private suspend fun handleResult(response: Result<String>) {
        if (response.isSuccess) {
            result.emit(response.getOrThrow())
        } else {
            error.emit(response.getOrThrow())
        }
    }
}