
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
    suspend fun register(@Body request: RegisterReq):Response<RegisterRes>
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
data class User(
    val id :String,
    val username:String
)
@Serializable
data class LoginRes(
    val  token:String,
    val  user:User,
    val error:String? =null
)
@Serializable
data class LoginReq(
    val username :String ,
    val password :String

)
@Serializable
data class RegisterRes(
    val  token:String,
    val  user:User,
    val error:String? =null
)
@Serializable
data class RegisterReq(
    val username :String ,
    val password :String,

)