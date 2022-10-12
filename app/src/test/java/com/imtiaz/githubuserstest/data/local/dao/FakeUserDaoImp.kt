package com.imtiaz.githubuserstest.data.local.dao

import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserDaoImp(
    val usersList: MutableList<GithubUser> = mutableListOf()
) : UserDao {

    override suspend fun insert(user: GithubUser) {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsers(user: List<GithubUser>) {
        usersList.addAll(user)
    }

    override suspend fun updateUsers(user: List<GithubUser>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> {
        TODO("Not yet implemented")
    }

    override fun getUsers(): Flow<List<GithubUser>> = flow { emit(usersList) }
}