package com.janad.zerodrop.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janad.zerodrop.data.repositories.AuthRepository
import com.janad.zerodrop.data.repositories.CmdRepository
import com.janad.zerodrop.data.UserPreferences
import com.janad.zerodrop.data.api.LoginRes
import com.janad.zerodrop.data.api.LogoutRes
import com.janad.zerodrop.data.api.RefreshRes
import com.janad.zerodrop.data.api.RegisterRes
import com.janad.zerodrop.data.model.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cmdRepository: CmdRepository,
    private val authRepository: AuthRepository,
    val userPreferences: UserPreferences
) : ViewModel() {

    private val _token =MutableStateFlow("")
    val token : StateFlow<String> =_token
      private val _accessToken =MutableStateFlow("")
    private val _refreshToken =MutableStateFlow("")
    val refreshToken : StateFlow<String> =_refreshToken


    private  val _loginResult =MutableStateFlow<LoginRes?>(null)
    open val loginResult: StateFlow<LoginRes?> = _loginResult
    private  val _logoutResult =MutableStateFlow<LogoutRes?>(null)
    val logoutResult: StateFlow<LogoutRes?> = _logoutResult

    private  val _registerResult =MutableStateFlow<RegisterRes?>(null)
    val registerResult: StateFlow<RegisterRes?> = _registerResult
    private  val _refreshResult =MutableStateFlow<RefreshRes?>(null)
    val refreshResult: StateFlow<RefreshRes?> = _refreshResult

    private val _authError = MutableStateFlow("")
    val authError :StateFlow<String> =_authError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting
    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress: StateFlow<Int> = _uploadProgress/**
     * Clears the access and refresh tokens from both the local state and persistent storage.
     */
    private val _commandRequestState =MutableStateFlow<RequestState>(RequestState.Idle)
    val commandRequestState : StateFlow<RequestState> = _commandRequestState

    private val _queryRequestState =MutableStateFlow<RequestState>(RequestState.Idle)
    val queryRequestState : StateFlow<RequestState> = _queryRequestState
    private suspend fun clearTokens() {
        _accessToken.value = ""
        _refreshToken.value = ""
        userPreferences.clearLoginInfo()
    }
    /**
     * Refreshes the access token using the provided refresh token.
     * Updates the local state and persistent storage with the new access token.
     */
    fun refreshToken(refreshToken:String){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val response = authRepository.refreshToken(refreshToken)
                if (response.isSuccessful) {
                    _refreshResult.value = response.body()
                    //    userPreferences.saveRefreshToken(response.body()?.refreshToken ?: "")
                    userPreferences.saveAccessToken(response.body()?.accessToken ?: "")
                } else {
                    userPreferences.saveRefreshToken("")
                    _authError.value = response.errorBody()?.string() ?: "Unknown error"
                }
            }
            catch (e:Exception){

                _authError.value ="Network error: ${e.message}"
            }
        }
    }


    /**
     * Logs out the current user by clearing tokens and making a logout API call.
     * Updates the UI state accordingly.
     */
    fun logout(refreshToken: String) {
        viewModelScope.launch {
            try {
                userPreferences.clearLoginInfo()
                _loginResult.value = null
                _registerResult.value = null
                _authError.value = ""
                _accessToken.value = ""
                _refreshToken.value = ""
                val response = authRepository.logout(refreshToken)
                if (response.isSuccessful) {
                    _logoutResult.value = response.body()
                } else {

                    _authError.value = "Unknown error"
                }

            }
            catch (e:Exception){

                _authError.value ="Network error: ${e.message}"
            }
        }
    }
    /**
     * Logs in a user with the given email and password.
     * Saves the access and refresh tokens upon successful login.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful) {
                    val loginRes = response.body()
                    _loginResult.value = loginRes
                    // Save tokens
                    _accessToken.value = loginRes?.accessToken ?: ""
                    _refreshToken.value = loginRes?.refreshToken ?: ""
                    userPreferences.saveAccessToken(_accessToken.value)
                    userPreferences.saveRefreshToken(_refreshToken.value)
                } else {
                    clearTokens()
                    _authError.value = response.body()?.error ?: "Unknown error"
                }
            } catch (e: Exception) {
                clearTokens()
                _authError.value = "Network error: ${e.message}"
            }
        }
    }
    /**
     * Registers a new user with the given email and password.
     * Saves the access and refresh tokens upon successful registration.
     */
    fun register(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = authRepository.register(email, password)
                if (response.isSuccessful) {
                    val registerRes = response.body()
                    _registerResult.value = registerRes
                    // Save tokens
                    _accessToken.value = registerRes?.accessToken ?: ""
                    _refreshToken.value = registerRes?.refreshToken ?: ""
                    userPreferences.saveAccessToken(_accessToken.value)
                    userPreferences.saveRefreshToken(_refreshToken.value)
                } else {
                    clearTokens()
                    _authError.value = response.body()?.error ?: "Unknown error"
                }
            } catch (e: Exception) {
                clearTokens()
                _authError.value = "Network error: ${e.message}"
            }
        }
    }
    /**
     * Fetches a list of products from the server.
     * Updates the UI state with the fetched products or an error message.
     */

    fun executeCommand(command: String) {
        viewModelScope.launch {
            try {
                _commandRequestState.value = RequestState.Loading
                val response = cmdRepository.executeCommand(command)
                if (response.isSuccessful) {
                    _commandRequestState.value = RequestState.Success("Command executed successfully\n${response.body()?.output ?: "No output"}")
                } else {
                     _commandRequestState.value = RequestState.Error(response.body()?.error ?: "Unknown error")
                   }
            } catch (e: Exception) {
                _commandRequestState.value = RequestState.Error(e.message ?: "Unknown error")

            }
        }
    }
    fun executeQuery(query: String) {
        viewModelScope.launch {
            try {

                _queryRequestState.value = RequestState.Loading
                val response = cmdRepository.executeQuery(query)
                if (response.isSuccessful) {
                    val result = response.body()
                    val stringRes =result.use { it?.string() ?: "Empty" }
                    Log.e("Daya",stringRes)
                    _queryRequestState.value = RequestState.Success(stringRes)
                } else {
                        Log.e("Query", "HTTP Error: ${response.code()}")
                        _queryRequestState.value = RequestState.Error( "Unknown error")
                }
            } catch (e: Exception) {
                Log.e("Query", "Network error: ${e.message}")
                _queryRequestState.value=RequestState.Error(e.message ?: "Unknown error")
                }
        }
    }

   }
