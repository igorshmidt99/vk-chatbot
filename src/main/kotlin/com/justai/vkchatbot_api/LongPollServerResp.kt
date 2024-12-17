package com.justai.vkchatbot_api

import kotlinx.serialization.Serializable

@Serializable
data class LongPollServerResp(var response: MutableMap<String, String> = mutableMapOf()) {

    fun key(): String? {
        return response["key"]
    }

    fun server(): String? {
        return response["server"]
    }

    fun ts(): String? {
        return response["ts"]
    }

    fun setTs(newTs: String) {
        response.put("ts", newTs)
    }

}