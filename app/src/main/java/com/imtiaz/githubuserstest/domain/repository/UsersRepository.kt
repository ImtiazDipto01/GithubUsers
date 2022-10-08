package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun fetchUsers(page: Int): Flow<Resource<List<GithubUser>>>
    suspend fun insertUsers(users: List<GithubUser>)
    fun getUsers(): Flow<List<GithubUser>>
}