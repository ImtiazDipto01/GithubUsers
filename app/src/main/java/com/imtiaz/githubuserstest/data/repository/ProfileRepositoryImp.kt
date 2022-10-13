package com.imtiaz.githubuserstest.data.repository

import android.util.Log
import com.imtiaz.githubuserstest.core.extensions.*
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
            if (it is BaseState.Success){
                val users = mapper.mapFromEntity(it.data)

                updateUserFromApi(users)
                emit(BaseState.Success(users))
            }
            else emit(it as BaseState<GithubUser>)
        }

    override suspend fun getUserProfileFromDB(id: Int): Flow<GithubUser?> {
        return try {
            userDao.getUserFlowAble(id)
        } catch (e: Exception) {
            Log.e(GET_USER_EXP, e.toString())
            flow<GithubUser?> { emit(null) }
        }
    }

    override suspend fun updateUser(user: GithubUser) {
        try {
            userDao.updateUser(user)
        } catch (e: Exception) {
            Log.e(UPDATE_EXP, e.toString())
        }
    }

    override suspend fun updateUserFromApi(user: GithubUser) {
        try {
            val prevUser = userDao.getUser(user.id)
            prevUser?.let {
                val updatedUser = user.copy(note = prevUser.note)
                userDao.updateUser(updatedUser)
            }
        } catch (e: Exception) {
            Log.e(UPDATE_EXP, e.toString())
        }
    }

}

