package com.imtiaz.githubuserstest.data.local.preference

interface PreferenceHelper {
    fun saveUserName(name: String)

    fun getUserName(key: String): String?
}