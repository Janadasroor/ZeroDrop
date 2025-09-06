package com.janad.zerodrop.data.api

import okhttp3.Interceptor
import okhttp3.Response

// AuthInterceptor class to add authorization token to requests
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    // Intercepts the request and adds authorization token if available
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Exclude login and register endpoints from token addition
        // Skip adding token for login/register endpoints
        val path = request.url.encodedPath
        if (path.contains("login") || path.contains("register")) {
            return chain.proceed(request)
        }

        // Otherwise, add the token
        // Retrieve token using tokenProvider
        val token = tokenProvider()
        val newRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
