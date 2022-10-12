package com.imtiaz.githubuserstest.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.data.util.successfullResponse
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUsersRepositoryImp(
    private val dao: UserDao,
    private val mapper: GithubUserMapper = GithubUserMapper(),
) : UsersRepository {

    override suspend fun fetchUsers(since: Int): Flow<State<List<GithubUser>>> {
        /*return when(testTag) {
            FETCH_AND_INSERT_USERS_SUCCESS -> {

            }
            else ->
        }*/
        val type = object : TypeToken<List<GithubUserResponse>>() {}.type
        val list = Gson().fromJson(successfullResponse, type) ?: listOf<GithubUserResponse>()

        val users = mapper.mapFromEntityList(list, since)

        insertUsers(users)
        return flow { emit(State.Success(users)) }
    }

    override suspend fun updateUsers(since: Int): Flow<State<List<GithubUserResponse>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUsers(users: List<GithubUser>) {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsers(users: List<GithubUser>) {
        dao.insertUsers(users)
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> {
        TODO("Not yet implemented")
    }

    override fun getUsers(): Flow<List<GithubUser>> = dao.getUsers()

}
