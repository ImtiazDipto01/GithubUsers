package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.domain.model.GithubUser
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUsers(): Flow<Resource<List<GithubUser>>>
}