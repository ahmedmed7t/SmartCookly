package com.nexable.smartcookly.netwrokUtils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

open class BaseNetworkClient(protected val httpClient: HttpClient) {

    /**
     * Execute a safe network request with automatic error handling
     */
    protected suspend inline fun <reified T> safeRequest(
        crossinline request: suspend () -> HttpResponse
    ): Result<T, NetworkError> {
        val response = try {
            request()
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }

        return when (response.status.value) {
            in 200..299 -> {
                try {
                    val data = response.body<T>()
                    Result.Success(data)
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            409 -> Result.Error(NetworkError.CONFLICT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    /**
     * GET request
     */
    protected suspend inline fun <reified T> get(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, NetworkError> = safeRequest {
        httpClient.get(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    /**
     * POST request
     */
    protected suspend inline fun <reified T> post(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, NetworkError> = safeRequest {
        httpClient.post(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    /**
     * PUT request
     */
    protected suspend inline fun <reified T> put(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, NetworkError> = safeRequest {
        httpClient.put(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    /**
     * DELETE request
     */
    protected suspend inline fun <reified T> delete(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, NetworkError> = safeRequest {
        httpClient.delete(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }

    /**
     * PATCH request
     */
    protected suspend inline fun <reified T> patch(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): Result<T, NetworkError> = safeRequest {
        httpClient.patch(urlString) {
            contentType(ContentType.Application.Json)
            block()
        }
    }
}
