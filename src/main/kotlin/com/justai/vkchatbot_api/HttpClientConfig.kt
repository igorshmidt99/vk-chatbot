package com.justai.vkchatbot_api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpClientConfig {

    @Bean
    fun client(): HttpClient {
        return HttpClient(CIO) {
            install(HttpTimeout) {
                connectTimeoutMillis = 25000
            }
            install(HttpRequestRetry)
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            engine {
                endpoint {
                    connectAttempts = 4
                }
            }
        }
    }

}