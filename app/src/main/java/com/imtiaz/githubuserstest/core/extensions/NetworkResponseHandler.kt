package com.imtiaz.githubuserstest.core.extensions

import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <reified T> safeApiCall(
    crossinline processApiCall: suspend () -> Response<T>
): Flow<State<T>> = flow {
    try {
        emit(State.Loading())
        val response = processApiCall.invoke()
        val data = handleApiResponse(response)

        if (data is T) emit(State.Success(data))
        else emit(State.Error(Throwable(data as Exception)))
    } catch (e: Exception) {
        emit(State.Error(Throwable(Exception(getCustomErrorMessage(e)))))
    }
}.flowOn(Dispatchers.IO)

fun <T> handleApiResponse(response: Response<T>): Any? {
    return try {
        if (response.isSuccessful) response.body()
        else {
            // TODO Need to get error from here
            val json = response.errorBody()?.string()
            Exception(json)
        }
    } catch (exp: Exception) {
        exp.printStackTrace()
        exp
    }
}

fun getCustomErrorMessage(error: Exception): String {
    return when (error) {
        is SocketTimeoutException -> "Oh! We couldn't capture your request in time. Please try again."
        is MalformedJsonException -> "Oh! We hit an error. Try again later."
        is IOException -> "Oh! You are not connected to a wifi or cellular data network. Please connect and try again"
        is FileNotFoundException -> "Oh! No such file or directory"
        is HttpException -> error.message()
        else -> "Something Went Wrong!"
    }
}