package com.imtiaz.githubuserstest.data.repository

import android.util.Log
import com.imtiaz.githubuserstest.core.extensions.*
import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import com.imtiaz.githubuserstest.data.local.preference.PreferenceHelper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class UsersRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val mapper: GithubUserMapper,
    private val userDao: UserDao,
    private val pageDao: PageDao,
    private val pref: PreferenceHelper
) : UsersRepository {

    override suspend fun fetchUsers(since: Int): Flow<BaseState<List<GithubUser>>> = safeApiCall {
        apiService.fetchUsers(since)
    }.transform {
        if (it is BaseState.Success) {
            val users = mapper.mapFromEntityList(it.data, since)

            // updating successfully fetched Since Id
            pref.saveCompletedSinceId(since)

            // saving next page since Id into db
            if (users.isNotEmpty())
                updateLastUserId(users[users.size - 1].id)

            insertUsers(users)
            emit(BaseState.Success(users))
        } else emit(it as BaseState<List<GithubUser>>)
    }

    override suspend fun updateUsers(since: Int): Flow<BaseState<List<GithubUserResponse>>> {
        val resultFlow = safeApiCall { apiService.fetchUsers(since) }
        resultFlow.collect {
            if (it is BaseState.Success) {
                val users = mapper.mapFromEntityList(it.data, since)

                // updating successfully fetched Since Id
                pref.saveCompletedSinceId(since)

                updateUsers(users)
            }
        }
        return resultFlow
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> = try {
        userDao.searchUsersByLoginOrNote(searchText)
    } catch (e: Exception) {
        Log.e(SEARCH_EXP, e.toString())
        listOf<GithubUser>()
    }

    override suspend fun updateUsers(users: List<GithubUser>) {
        try {
            userDao.updateUsers(users)
        } catch (e: Exception) {
            Log.e(UPDATE_EXP, e.toString())
        }
    }

    override suspend fun insertUsers(users: List<GithubUser>) {
        try {
            userDao.insertUsers(users)
        } catch (e: Exception) {
            Log.e(INSERT_EXP, e.toString())
        }
    }

    override fun getUsers(): Flow<List<GithubUser>> = try {
        userDao.getUsers()
    } catch (e: Exception){
        Log.e(GET_USER_EXP, e.toString())
        flow { listOf<GithubUser>() }
    }

    private suspend fun updateLastUserId(id: Int) {
        try {
            pageDao.insert(Page(since = id))
        } catch (e: Exception) {
            Log.e(PAGE_EXP, e.toString())
        }
    }

}
