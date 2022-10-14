package com.imtiaz.githubuserstest.data.util

const val FETCH_AND_INSERT_USERS_SUCCESS = "fetchAndInsertUsersSuccess"
const val FETCH_AND_UPDATE_USER_SUCCESS = "fetchAndInsertUsersSuccess"
const val HTTP_ERROR = "httpError"
const val INSERT_FAIL = "insertFail"
const val FETCH_AND_UPDATE_USERS = "fetchAndUpdateUsers"
const val FETCH_AND_UPDATE_FAIL = "fetchAndUpdateFail"
const val SEARCH_FAIL = "searchFail"
const val SEARCH_USERS = "searchUsers"

object TestUtil {
    var testTag: String = FETCH_AND_INSERT_USERS_SUCCESS
}