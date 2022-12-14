package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(loginId: String): Flow<BaseState<GithubUser>> =
        repository.getUserProfile(loginId)
}
