package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    suspend fun execute(since: Int): Flow<State<List<GithubUserResponse>>> =
        repository.updateUsers(since)
}