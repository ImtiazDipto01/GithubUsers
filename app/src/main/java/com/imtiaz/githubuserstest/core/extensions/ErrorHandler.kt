package com.imtiaz.githubuserstest.core.extensions

import com.google.gson.annotations.SerializedName

data class ErrorHandler(

    @SerializedName("message")
    val msg: String,

    val exception: java.lang.Exception? = null
)