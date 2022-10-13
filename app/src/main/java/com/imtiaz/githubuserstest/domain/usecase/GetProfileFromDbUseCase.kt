package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileFromDbUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(loginId: String): GithubUser? = repository.getUserProfileFromDB(loginId)
}
