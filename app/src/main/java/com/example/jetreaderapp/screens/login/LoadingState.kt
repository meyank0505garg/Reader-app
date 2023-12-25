package com.example.jetreaderapp.screens.login

data class LoadingState(
    val status : Status,
    val message : String? = null,
    ){

    enum class Status{
        SUCCESS,
        FAILED,
        LOADING,
        IDLE

    }
    companion object{

        val IDLE = LoadingState(status = Status.IDLE)
        val SUCCESS = LoadingState(status = Status.SUCCESS)
        val FAILED = LoadingState(status = Status.FAILED)
        val LOADING = LoadingState(status = Status.LOADING)


    }


}