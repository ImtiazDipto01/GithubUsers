package com.imtiaz.githubuserstest.core.extensions

import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.presentation.user.UsersFragmentDirections

fun usersToProfile(user: GithubUser) =
    UsersFragmentDirections.actionStartFragmentToProfileFragment(user)
