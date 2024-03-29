package com.example.mymoviesapp.data.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKeyHeader = ApiKeyQuery.fromApiKey()

        val request = chain.request()

        val url = request.url.newBuilder()
            .addQueryParameter(apiKeyHeader.key, apiKeyHeader.value)
            .build()

        val newRequest = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(newRequest)
    }
}