package com.justai.vkchatbot_api

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import org.springframework.stereotype.Component

@Component
class HttpRequestFactory(
    private val properties: Properties,
) {

    fun createLongPollServer(): HttpRequestBuilder.() -> Unit {
        return {
            url {
                host = properties.apiPath
                path("method/groups.getLongPollServer")
                parameters {
                    parameter(properties.tokenPrefix, properties.accessToken)
                    parameter(properties.vPrefix, properties.apiVersion)
                    parameter(properties.groupIdPrefix, properties.groupId)
                }
                protocol = URLProtocol.HTTPS
            }
            retry {
                timeout { HttpTimeoutConfig.INFINITE_TIMEOUT_MS }
                retryOnException(3, true)
            }
            headers {
                contentType(ContentType.Application.FormUrlEncoded)
            }
        }
    }

    fun longPollRequest(params: LongPollServerResp): HttpRequestBuilder.() -> Unit {
        return {
            url(Url.invoke("${params.server()}?act=a_check&key=${params.key()}&ts=${params.ts()}&wait=25"))
            headers {
                contentType(ContentType.Application.FormUrlEncoded)
            }
            retry {
                timeout { HttpTimeoutConfig.INFINITE_TIMEOUT_MS }
                retryOnException(3, true)
            }
        }
    }

    fun sendMessage(body: SendMessageReq): HttpRequestBuilder.() -> Unit {
        val path = "method/messages.send/"
        val content = MultiPartFormDataContent(
            getPartData().plus(formData {
                append("random_id", body.randomId)
                append("peer_id", body.peerId)
                append("message", body.message)
                append("group_id", body.groupId)
            })
        )
        return createRequest(content, path)
    }

    private fun getPartData(): List<PartData> {
        return formData {
            append(properties.vPrefix, properties.apiVersion)
            append(properties.tokenPrefix, properties.accessToken)
        }
    }

    private fun createRequest(content: MultiPartFormDataContent, path: String): HttpRequestBuilder.() -> Unit {
        return {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.vk.com"
                path(path)
            }
            headers {
                contentType(ContentType.MultiPart.FormData.withParameter("boundary", "delimiter"))
            }
            retry {
                timeout { requestTimeoutMillis = 25000 }
                retryOnException(5, true)
            }
            setBody(content)
        }
    }

}