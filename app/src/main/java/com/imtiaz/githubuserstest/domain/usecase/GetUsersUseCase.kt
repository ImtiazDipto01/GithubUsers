package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.domain.model.GithubUser
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    suspend fun execute(): Flow<Resource<List<GithubUser>>> = repository.getUsers()
}