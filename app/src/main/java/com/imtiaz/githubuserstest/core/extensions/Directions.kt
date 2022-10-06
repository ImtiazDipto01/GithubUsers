package com.imtiaz.githubuserstest.core.extensions

import com.imtiaz.githubuserstest.domain.model.GithubUser
import com.imtiaz.githubuserstest.presentation.user.UsersFragmentDirections

fun usersToProfile(user: GithubUser) =
    UsersFragmentDirections.actionStartFragmentToProfileFragment(user)