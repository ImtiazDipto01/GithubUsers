package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class UsersRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapper: GithubUserMapper,
    private val userDao: UserDao,
    private val pageDao: PageDao,
) : UsersRepository {

    override suspend fun fetchUsers(page: Int): Flow<State<List<GithubUser>>> = safeApiCall {
        apiService.fetchUsers(page)
    }.transform {
        if (it is State.Success){
            val users = mapper.mapFromEntityList(it.data, page)
            insertUsers(users)
            updatePageNumber(page)
            emit(State.Success(users))
        }
        else emit(it as State<List<GithubUser>>)
    }

    override suspend fun insertUsers(users: List<GithubUser>) = userDao.insertUsers(users)

    override fun getUsers(): Flow<List<GithubUser>> = userDao.getUsers()

    private suspend fun updatePageNumber(page: Int) = pageDao.insert(Page(lastPage = page))

}