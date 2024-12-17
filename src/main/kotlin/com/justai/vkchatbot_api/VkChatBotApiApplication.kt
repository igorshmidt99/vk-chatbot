package com.justai.vkchatbot_api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VkChatBotApiApplication

fun main(args: Array<String>) {
    val app = runApplication<VkChatBotApiApplication>(*args)
    val loop = app.getBean(EventLoop::class.java)
    val client: HttpClient = app.getBean(HttpClient::class.java)
    val reqFactory = app.getBean(HttpRequestFactory::class.java)
    runBlocking {
        val response: HttpResponse = client.get(reqFactory.createLongPollServer())
        val resp: LongPollServerResp = response.body<LongPollServerResp>()
        loop.start(resp)
    }

}