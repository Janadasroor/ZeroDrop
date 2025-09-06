package com.janad.zerodrop.data.repositories

import com.janad.zerodrop.data.api.ApiService
import com.janad.zerodrop.data.api.LoginReq
import com.janad.zerodrop.data.api.LoginRes
import com.janad.zerodrop.data.api.RegisterReq
import com.janad.zerodrop.data.api.RegisterRes
import retrofit2.Response

import javax.inject.Inject

/**
 * Repository class for handling authentication-related operations.
 *
 * This class interacts with the [ApiService] to perform login and registration requests.
 * It is injected with an instance of [ApiService] using Dagger Hilt.
 *
 * @property api The [ApiService] instance used to make network requests.
 */
class AuthRepository @Inject constructor(private val api: ApiService){

    /**
     * Performs a login request to the API.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A [Response] object containing the [LoginRes] if the request is successful, or an error otherwise.
     */
    suspend fun  login( username:String ,password:String): Response<LoginRes> {
       return  api.login(LoginReq(username,password))
    }
    // This function is currently unused but might be used in the future for registration functionality.
  suspend fun  register(username:String, password:String): Response<RegisterRes> {
       return  api.register(RegisterReq(username,password))
    }
}