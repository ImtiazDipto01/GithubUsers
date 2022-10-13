package com.imtiaz.githubuserstest.data.repository

import android.util.Log
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.core.extensions.GET_USER_EXP
import com.imtiaz.githubuserstest.core.extensions.SEARCH_EXP
import com.imtiaz.githubuserstest.core.extensions.safeApiCall
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.ProfileMapper
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ProfileRepositoryImp @Inject constructor(
    private val apiService: ProfileService,
    private val userDao: UserDao,
    private val mapper: ProfileMapper
) : ProfileRepository {

    override suspend fun getUserProfile(loginId: String): Flow<BaseState<GithubUser>> =
        safeApiCall {
            apiService.getUserProfile(loginId)
        }.transform {
            if (it is BaseState.Success)
                emit(BaseState.Success(mapper.mapFromEntity(it.data)))
            else emit(it as BaseState<GithubUser>)
        }

    override suspend fun getUserProfileFromDB(loginId: String): Flow<GithubUser?> {
        return try {
            userDao.getUser(loginId)
        } catch (e: Exception) {
            Log.e(GET_USER_EXP, e.toString())
            flow<GithubUser?> { emit(null) }
        }
    }

}

