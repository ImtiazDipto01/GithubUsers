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

    override suspend fun fetchUsers(page: Int): Flow<Resource<List<GithubUser>>> = safeApiCall {
        apiService.fetchUsers(page)
    }.transform {
        if (it is Resource.Success){
            val users = mapper.mapFromEntityList(it.data, page)
            insertUsers(users)
            emit(Resource.Success(users))
        }
        else emit(it as Resource<List<GithubUser>>)
    }

    override suspend fun insertUsers(users: List<GithubUser>) = userDao.insertUsers(users)

    override fun getUsers(): Flow<List<GithubUser>> = userDao.getUsers()

}