package com.imtiaz.githubuserstest.data.local.preference

interface PreferenceHelper {
    fun saveCompletedSinceId(since: Int)

    fun getSavedSinceList() : List<Int>

    fun clearList()

    fun isSinceContain(since: Int): Boolean
}