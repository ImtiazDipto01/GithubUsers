package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class UsersRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapper: GithubUserMapper,
    private val userDao: UserDao
) : UsersRepository {

    override suspend fun fetchUsers(): Flow<Resource<List<GithubUser>>> = safeApiCall {
        apiService.fetchUsers()
    }.transform {
        if (it is Resource.Success){
            //getUsersFromDb()
            emit(Resource.Success(mapper.mapFromEntityList(it.data)))
        }
        else emit(it as Resource<List<GithubUser>>)
    }

    override fun getUsers(): Flow<List<GithubUser>> = userDao.getUsers()

}