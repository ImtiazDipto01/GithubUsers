package com.imtiaz.githubuserstest.presentation.profile.ui.state

import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser

data class ProfileState(
    val isLoading: Boolean = false,
    val user: GithubUser? = null,
    val err: ErrorHandler? = null
)