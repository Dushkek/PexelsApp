package com.adush.pexelsapp.domain

sealed class Response<out R> {
    class Success<T>(val data: T) : Response<T>()
    class Error(val errorMessage: String) : Response<Nothing>()
    object Empty : Response<Nothing>()
}