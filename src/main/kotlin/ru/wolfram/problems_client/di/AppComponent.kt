package ru.wolfram.problems_client.di

import com.github.ajalt.mordant.terminal.Terminal
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import ru.wolfram.problems_client.data.network.ApiService
import ru.wolfram.problems_client.data.network.ApiServiceImpl
import ru.wolfram.problems_client.data.network.NetworkClient
import ru.wolfram.problems_client.data.repository.AppRepositoryImpl
import ru.wolfram.problems_client.domain.model.Configuration
import ru.wolfram.problems_client.domain.repository.AppRepository
import ru.wolfram.problems_client.presentation.AppModel
import java.io.File

object AppComponent {
    val gson: Gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }

    val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    val terminal by lazy {
        Terminal()
    }

    val configuration: Configuration by lazy {
        val configurationString = File("src/main/resources/configuration.json").readText()
        gson.fromJson(configurationString, Configuration::class.java)
    }

    fun provideNetworkClient(): NetworkClient = NetworkClient(client, configuration)

    fun provideApiService(): ApiService = ApiServiceImpl(provideNetworkClient())

    fun provideAppRepository(): AppRepository = AppRepositoryImpl(provideApiService(), configuration)

    fun provideAppModel(): AppModel {
        return AppModel(provideAppRepository())
    }

    fun provideScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}