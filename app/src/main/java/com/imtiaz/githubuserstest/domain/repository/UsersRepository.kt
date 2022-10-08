package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun fetchUsers(): Flow<Resource<List<GithubUser>>>
    fun getUsers(): Flow<List<GithubUser>>
}