package ru.wolfram.problems_client.presentation

import com.github.ajalt.mordant.terminal.danger
import com.github.ajalt.mordant.terminal.info
import kotlinx.coroutines.runBlocking
import ru.wolfram.problems_client.di.AppComponent

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val terminal = AppComponent.terminal
            val model = AppComponent.provideAppModel()
            model.observe(
                onResult = { result: String -> println(result) },
                onError = { error: String -> terminal.danger(error, stderr = true) }
            )
            while (true) {
                val input = readlnOrNull()?.trim()
                if (input != null) {
                    when (input.lowercase()) {
                        "quit", "exit" -> {
                            model.close()
                            break
                        }

                        "register" -> {
                            model.register()
                        }

                        else -> {
                            val parts = input.split("\\s+".toRegex())
                            when (parts[0]) {
                                "solve" -> {
                                    if (parts.size > 2) {
                                        model.solve(parts[1], parts[2])
                                    } else {
                                        terminal.info("Usage: solve [language] [taskName]")
                                    }
                                }

                                "load" -> {
                                    model.loadTasks()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}