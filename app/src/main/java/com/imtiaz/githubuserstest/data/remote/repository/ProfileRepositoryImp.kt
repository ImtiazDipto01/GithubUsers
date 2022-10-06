package com.imtiaz.githubuserstest.data.remote.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.mapper.ProfileUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.domain.model.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val apiService: ProfileService,
    private val mapper: ProfileUserMapper
) : ProfileRepository {

    override suspend fun getUserProfile(loginId: String): Flow<Resource<GithubUser>> = safeApiCall {
        apiService.getUserProfile(loginId)
    }.transform {
        if (it is Resource.Success)
            emit(Resource.Success(mapper.mapFromEntity(it.data)))
        else emit(it as Resource<GithubUser>)
    }

}