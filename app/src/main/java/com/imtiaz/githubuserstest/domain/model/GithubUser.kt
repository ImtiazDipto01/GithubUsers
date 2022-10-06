package com.imtiaz.githubuserstest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GithubUser(
    val login: String? = null,
    val avatarUrl: String? = null,
    val nodeId: String? = null,
    val url: String? = null,
    val name: String? = null,
    val location: String? = null,
    val public_repo: Int = 0,
    val public_gist: Int = 0,
    val followers: Int = 0,
    val following: Int = 0
) : Parcelable
