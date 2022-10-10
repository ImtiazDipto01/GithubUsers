package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    suspend fun execute(searchText: String): List<GithubUser> =
        repository.searchUsersByLoginOrNote(searchText)
}
