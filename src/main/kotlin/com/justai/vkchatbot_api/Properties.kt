package com.justai.vkchatbot_api

import org.springframework.stereotype.Component

@Component
class Properties(
    val accessToken: String = System.getenv("ACCESS_TOKEN").trim(),
    val apiVersion: String = System.getenv("API_VERSION").trim(),
    val groupId: String = System.getenv("GROUP_ID").trim(),
    val groupIdPrefix: String = "group_id",
    val vPrefix: String = "v",
    val tokenPrefix: String = "access_token",
    val apiPath: String = "api.vk.com",
)