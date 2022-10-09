package com.imtiaz.githubuserstest.data.local.preference

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : PreferenceHelper {

    override fun saveCompletedSinceId(since: Int) {
        val list = getSavedSinceList().toMutableList()
        if (!list.contains(since)) list.add(since)

        val json = gson.toJson(list)
        sharedPreferences.edit().putString(KEY_SINCE_LIST, json).apply()
    }

    override fun getSavedSinceList(): List<Int> {
        val idsJson: String? = sharedPreferences.getString(KEY_SINCE_LIST, null)
        if (idsJson != null) {
            val type = object : TypeToken<List<Int>>() {}.type
            return gson.fromJson(idsJson, type) ?: emptyList<Int>()
        }
        return emptyList()
    }

    override fun clearList() =
        sharedPreferences.edit().putString(KEY_SINCE_LIST, null).apply()

    override fun isSinceContain(since: Int): Boolean =
        getSavedSinceList().toMutableList().contains(since)

    companion object {
        private const val KEY_SINCE_LIST = "since_list"
    }

}