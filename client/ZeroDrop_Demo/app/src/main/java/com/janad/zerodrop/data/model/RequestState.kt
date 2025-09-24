package com.janad.zerodrop.data.model

//state
sealed class RequestState{
    object Idle:RequestState()
    object Loading:RequestState()
    data class Success(val message:String):RequestState()
    data class Error(val message:String):RequestState()
}