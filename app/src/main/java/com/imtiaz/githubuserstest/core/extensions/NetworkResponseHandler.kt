package com.imtiaz.githubuserstest.core.extensions

import com.google.gson.Gson
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <reified T> safeApiCall(
    crossinline processApiCall: suspend () -> Response<T>
): Flow<BaseState<T>> = flow {
    try {
        emit(BaseState.Loading())
        val response = processApiCall.invoke()
        val data = handleApiResponse(response)

        if (data is T) emit(BaseState.Success(data))
        else emit(BaseState.Error(data as ErrorHandler))
    } catch (e: Exception) {
        emit(BaseState.Error(getCustomErrorMessage(e)))
    }
}.flowOn(Dispatchers.IO)

fun <T> handleApiResponse(response: Response<T>): Any? {
    return try {
        if (response.isSuccessful) response.body()
        else {
            val json = response.errorBody()?.string()
            Gson().fromJson(json, ErrorHandler::class.java)
        }
    } catch (exp: Exception) {
        exp.printStackTrace()
        ErrorHandler("Something Went Wrong!", JSONException(exp.message), response.code())
    }
}

fun getCustomErrorMessage(error: Exception): ErrorHandler {
    return when (error) {
        is SocketTimeoutException -> ErrorHandler(
            "Oh! We couldn't capture your request in time. Please try again.",
            error
        )
        is MalformedJsonException -> ErrorHandler(
            "Oh! We hit an error. Try again later.",
            error
        )
        is FileNotFoundException -> ErrorHandler(
            "Oh! No such file or directory",
            error
        )
        is IOException -> ErrorHandler(
            "Oh! You are not connected to a wifi or cellular data network. Please connect and try again",
            error
        )
        is HttpException -> ErrorHandler(error.message(), error)
        else -> ErrorHandler("Something Went Wrong!", error)
    }
}
