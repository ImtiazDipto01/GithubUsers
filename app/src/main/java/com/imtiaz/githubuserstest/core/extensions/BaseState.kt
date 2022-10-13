package com.imtiaz.githubuserstest.core.extensions

sealed class BaseState<T> {
  data class Success<T>(val data: T) : BaseState<T>()
  data class Loading<T>(val data: T? = null) : BaseState<T>()
  data class Error<T>(val err: ErrorHandler) : BaseState<T>()
  data class Empty<T>(val data: T? = null) : BaseState<T>()
}
