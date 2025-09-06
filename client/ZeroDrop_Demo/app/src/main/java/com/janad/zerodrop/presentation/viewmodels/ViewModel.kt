package com.janad.zerodrop.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janad.zerodrop.data.repositories.AuthRepository
import com.janad.zerodrop.data.repositories.CmdRepository
import com.janad.zerodrop.data.UserPreferences
import com.janad.zerodrop.data.api.ExecCmdRes
import com.janad.zerodrop.data.api.LoginRes
import com.janad.zerodrop.data.api.RegisterRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val cmdRepository: CmdRepository,
    private val authRepository: AuthRepository,
    val userPreferences: UserPreferences
) : ViewModel() {

    // --- Command execution state ---
    private val _token =MutableStateFlow("")
    val token : StateFlow<String> =_token
    private val _cmdResult = MutableStateFlow<ExecCmdRes?>(null)
    val cmdResult: StateFlow<ExecCmdRes?> = _cmdResult

    private val _cmdError = MutableStateFlow("")

    val cmdError: StateFlow<String> = _cmdError
    private  val _loginResult =MutableStateFlow<LoginRes?>(null)
    val loginResult: StateFlow<LoginRes?> = _loginResult

    private  val _registerResult =MutableStateFlow<RegisterRes?>(null)
    val registerResult: StateFlow<RegisterRes?> = _registerResult

    private val _authError = MutableStateFlow("")
    val authError :StateFlow<String> =_authError
    fun saveToken(username: String, token: String) {
        viewModelScope.launch {
            userPreferences.saveLoginInfo(username, token)
        }
    }


    fun logout() {
        viewModelScope.launch {
            userPreferences.clearLoginInfo()
        }
    }
    fun login(username : String,password:String){
        viewModelScope.launch (Dispatchers.IO){
           try {
                val response = authRepository.login(username, password)
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                    saveToken(username, _loginResult.value?.token ?: "")
                } else {
                    saveToken("",  "")

                    _authError.value = response.body()?.error?:"Unknown error"
                }
            }
           catch (e:Exception){
               saveToken("",  "")

               _authError.value ="Network error: ${e.message}"
           }
        }
    }
    fun register(username : String,password:String){
        viewModelScope.launch (Dispatchers.IO){
          try  {
                val response = authRepository.register(username, password)
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                    saveToken(username, _registerResult.value?.token ?: "")

                } else {
                    saveToken("",  "")

                    _authError.value = response.body()?.error ?: "Unknown error"
                }
            }
          catch (e:Exception){
              _authError.value ="Network error: ${e.message}"
              saveToken("",  "")

          }
        }
    }

    fun executeCommand(command: String) {
        viewModelScope.launch {
            try {
                val response = cmdRepository.executeCommand(command)
                if (response.isSuccessful) {
                    _cmdResult.value = response.body()
                    _cmdError.value = ""
                } else {
                    _cmdResult.value = null
                    _cmdError.value = response.body()?.error ?: "Unknown error"
                }
            } catch (e: Exception) {
                _cmdResult.value = null
                _cmdError.value = "Network error: ${e.message}"
            }
        }
    }

    // --- Query execution state ---
    private val _queryResult = MutableStateFlow<String>("")
    val queryResult: StateFlow<String> = _queryResult

    private val _queryError = MutableStateFlow("")
    val queryError: StateFlow<String> = _queryError

    fun executeQuery(query: String) {
        viewModelScope.launch {
            try {

                val response = cmdRepository.executeQuery(query)
                if (response.isSuccessful) {
                    val result = response.body()
                    val stringRes =result.use { it?.string() ?: "Empty" }
                    Log.e("Daya",stringRes)
                    _queryResult.value =stringRes
                } else {
                        Log.e("Query", "HTTP Error: ${response.code()}")
                        _queryError.value ="error"
                }
            } catch (e: Exception) {
                Log.e("Query", "Network error: ${e.message}")
                _queryResult.value = "Error"
                _queryError.value = "Network error: ${e.message}"
            }
        }
    }

   }
