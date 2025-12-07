package com.example.paging.infrastracture

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Module
@ComponentScan
class InfrastructureModule {
    @Single
    @Named("ktorGithubApiClient")
    fun ktorGithubApiClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            // Ktor type-safe requests
            // https://ktor.io/docs/client-resources.html#install_plugin
            install(Resources)

            engine {
                // this: CIOEngineConfig
                maxConnectionsCount = 1000
                endpoint {
                    // this: EndpointConfig
                    maxConnectionsPerRoute = 100
                    pipelineMaxSize = 20
                    keepAliveTime = 10000
                    connectTimeout = 30000
                    connectAttempts = 3
                }
            }
            // https://ktor.io/docs/client-response-validation.html#default
            expectSuccess = true

            // set default root url
            install(DefaultRequest)
            defaultRequest {
                url("https://api.github.com/")
            }
        }
    }
}