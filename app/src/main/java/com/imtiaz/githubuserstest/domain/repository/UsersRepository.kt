package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun fetchUsers(since: Int): Flow<BaseState<List<GithubUser>>>

    suspend fun updateUsers(since: Int): Flow<BaseState<List<GithubUserResponse>>>

    suspend fun insertUsers(users: List<GithubUser>)

    suspend fun updateUsers(users: List<GithubUser>)

    suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser>

    fun getUsers(): Flow<List<GithubUser>>
}
