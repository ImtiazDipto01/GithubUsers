package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getUserProfile(loginId: String): Flow<State<GithubUser>>
}