package com.imtiaz.githubuserstest.core.extensions

sealed class State<T> {
  data class Success<T>(val data: T) : State<T>()
  data class Loading<T>(val data: T? = null) : State<T>()
  data class Error<T>(val throwable: Throwable) : State<T>()
  data class Empty<T>(val data: T? = null) : State<T>()
}
