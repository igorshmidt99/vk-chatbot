package com.justai.vkchatbot_api

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageReq(var randomId: Int, var peerId: Int, var message: String, var groupId: String)
