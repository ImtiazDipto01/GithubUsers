package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import com.imtiaz.githubuserstest.data.local.preference.PreferenceHelper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlin.math.sin

class UsersRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapper: GithubUserMapper,
    private val userDao: UserDao,
    private val pageDao: PageDao,
    private val pref: PreferenceHelper
) : UsersRepository {

    override suspend fun fetchUsers(since: Int): Flow<State<List<GithubUser>>> = safeApiCall {
        apiService.fetchUsers(since)
    }.transform {
        if (it is State.Success) {
            val users = mapper.mapFromEntityList(it.data, since)

            // updating successfully fetched Since Id
            pref.saveCompletedSinceId(since)

            // saving next page since Id into db
            if (users.isNotEmpty())
                updateLastUserId(users[users.size - 1].id)

            insertUsers(users)
            emit(State.Success(users))
        } else emit(it as State<List<GithubUser>>)
    }

    override suspend fun updateUsers(since: Int): Flow<State<List<GithubUserResponse>>> {
        val resultFlow = safeApiCall { apiService.fetchUsers(since) }
        resultFlow.collect {
            if (it is State.Success) {
                val users = mapper.mapFromEntityList(it.data, since)

                // updating successfully fetched Since Id
                pref.saveCompletedSinceId(since)

                updateUsers(users)
            }
        }
        return resultFlow
    }

    override suspend fun updateUsers(users: List<GithubUser>) = userDao.updateUsers(users)

    override suspend fun insertUsers(users: List<GithubUser>) = userDao.insertUsers(users)

    override fun getUsers(): Flow<List<GithubUser>> = userDao.getUsers()

    private suspend fun updateLastUserId(id: Int) = pageDao.insert(Page(since = id))

}