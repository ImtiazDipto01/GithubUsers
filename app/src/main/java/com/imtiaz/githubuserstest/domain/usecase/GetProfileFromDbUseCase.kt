package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileFromDbUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(id: Int): Flow<GithubUser?> =
        repository.getUserProfileFromDB(id)
}
