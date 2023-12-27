package com.example.jetreaderapp.data


sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,


){
    class Success<T>(data: T,message: String? = "Success"):Resource<T>(data)
    class Error<T>(message : String? = "Error",data: T? = null) : Resource<T>(data,message)
    class Loading<T>(data: T? = null,message: String? = "checking"):Resource<T>(data)

}
