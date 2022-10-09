package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.mapper.ProfileMapper
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val apiService: ProfileService,
    private val mapper: ProfileMapper
) : ProfileRepository {

    override suspend fun getUserProfile(loginId: String): Flow<State<GithubUser>> = safeApiCall {
        apiService.getUserProfile(loginId)
    }.transform {
        if (it is State.Success)
            emit(State.Success(mapper.mapFromEntity(it.data)))
        else emit(it as State<GithubUser>)
    }

}