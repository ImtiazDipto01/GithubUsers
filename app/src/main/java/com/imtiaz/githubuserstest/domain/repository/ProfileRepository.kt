package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUserProfile(loginId: String): Flow<BaseState<GithubUser>>

    suspend fun getUserProfileFromDB(id: Int): Flow<GithubUser?>

    suspend fun updateUser(user: GithubUser)
}
