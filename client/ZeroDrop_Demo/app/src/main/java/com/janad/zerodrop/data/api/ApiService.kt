
// API Service
package com.janad.zerodrop.data.api


import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("run/query")
    suspend fun executeQuery(@Body request: ExecQueryReq): Response<ResponseBody>

    @POST("run/cmd")
    suspend fun executeCommand(
        @Body request: ExecCmdReq
    ): Response<ExecCmdRes>
    @POST("auth/login")
    suspend fun login(@Body request: LoginReq):Response<LoginRes>
    @POST("auth/register")
    suspend fun register(@Body request:RegisterReq):Response<RegisterRes>
    @POST("auth/logout")
    suspend fun logout(@Body request:LogoutReq):Response<LogoutRes>
    @POST("auth/refresh")
    fun refreshToken(@Body request:RefreshReq): Response<RefreshRes>

}
@Serializable
data class ExecCmdReq(
    val cmd: String
)

@Serializable
data class ExecCmdRes(
    val output: String? = null,
    val error: String? = null
)
@Serializable
data class ExecQueryReq(
    val query: String
)

@Serializable
data class ExecQueryRes(
    val result: List<Map<String, String>>? = null, // Each row as a key-value map
    val error: String? = null
)
@Serializable
data class ProductRequest(
    val title: String,
    val description: String,
    val price: Double,
    val mediaIds: List<Int>
)
@Serializable
data class ModifyProductRequest(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val mediaIds: List<Int>
)
@Serializable
data class ProductResponse(
    val message: String
)


@Serializable
data class UploadResponse(
    val message: String,
    val mediaId: Int
)
@Serializable
data class User(
    val id :String,
    val email:String
)
@Serializable
data class LoginRes(
    val  accessToken:String,
    val  refreshToken:String,
    val  user:User,
    val error:String? =null
)
@Serializable
data class LoginReq(
    val email :String,
    val password :String

)
@Serializable
data class LogoutReq(
    val refreshToken :String,

    )
@Serializable
data class RefreshReq (
    val refreshToken :String,

    )
@Serializable
data class RegisterRes(
    val  accessToken:String,
    val  refreshToken:String,
    val  user:User,
    val error:String? =null
)
@Serializable
data class LogoutRes(
    val  message:String,
)
@Serializable
data class RefreshRes(
    val  accessToken:String
//    val  refreshToken:String
    ,)
@Serializable
data class RegisterReq(
    val email :String,
    val password :String,

    )