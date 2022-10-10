package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    fun execute(): Flow<List<GithubUser>> = repository.getUsers()
}
