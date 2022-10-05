package com.imtiaz.githubuserstest.data.remote.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.domain.model.GithubUser
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class UsersRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapper: GithubUserMapper
) : UsersRepository {

    override suspend fun getUsers(): Flow<Resource<List<GithubUser>>> = safeApiCall {
        apiService.getUsers()
    }.transform {
        if (it is Resource.Success)
            emit(Resource.Success(mapper.mapFromEntityList(it.data)))
        else emit(it as Resource<List<GithubUser>>)
    }

}