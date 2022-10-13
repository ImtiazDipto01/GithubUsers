package com.imtiaz.githubuserstest.presentation.profile.ui.state

import androidx.compose.runtime.mutableStateOf
import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser

class ProfileState {
    val isLoading = mutableStateOf<Boolean>(false)
    val user = mutableStateOf<GithubUser?>(null)
    val err = mutableStateOf<ErrorHandler?>(null)
}