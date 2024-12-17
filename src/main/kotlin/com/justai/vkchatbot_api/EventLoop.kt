package com.justai.vkchatbot_api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.springframework.stereotype.Component
import kotlin.coroutines.EmptyCoroutineContext

@Component
class EventLoop(
    private val client: HttpClient,
    private val reqFactory: HttpRequestFactory,
    private val coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
) {

    suspend fun start(params: LongPollServerResp) {
        var fetchLastUpdates = reqFactory.longPollRequest(params)
        var prevTs = params.ts()
        var isNotFirstReq = false
        var lastUpdates: HttpResponse
        var asType: JsonObject

        while (true) {
            if (isNotFirstReq) {
                fetchLastUpdates = updateRequestToServerParams(prevTs, params)
            }
            isNotFirstReq = true
            lastUpdates = client.get(fetchLastUpdates)
            asType = lastUpdates.body<JsonObject>()
            val ts = (asType["ts"] as JsonPrimitive).content
            val updates = asType["updates"] as JsonArray
            if (prevTs != ts) {
                if (updates.isNotEmpty()) {
                    sendResponseMessage(updates)
                }
                prevTs = ts
            }
        }
    }

    private fun sendResponseMessage(updates: JsonArray) {
        updates.asSequence()
            .map { it.jsonObject }
            .filter { (it["type"] as JsonPrimitive).content == "message_new" }
            .map { message(it) }
            .map { reqFactory.sendMessage(it) }
            .forEach {
                coroutineScope.launch {
                    client.post(it)
                }
            }
    }

    private fun updateRequestToServerParams(
        prevTs: String?,
        params: LongPollServerResp,
    ): HttpRequestBuilder.() -> Unit {
        prevTs?.let { params.setTs(it) }
        return reqFactory.longPollRequest(params)
    }

    private fun message(update: Map<*, *>): SendMessageReq {
        val obj = update["object"] as JsonObject
        val message = obj["message"] as JsonObject
        val text = (message["text"] as JsonPrimitive).content
        val peerId = (message["peer_id"] as JsonPrimitive).content.toInt()
        val groupId = (update["group_id"] as JsonPrimitive).content
        return SendMessageReq(0, peerId, text, groupId)
    }

}