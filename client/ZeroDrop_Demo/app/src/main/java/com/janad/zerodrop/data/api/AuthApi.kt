package com.janad.zerodrop.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/refresh")
    fun refreshToken(@Body req: RefreshReq): Call<RefreshRes>}