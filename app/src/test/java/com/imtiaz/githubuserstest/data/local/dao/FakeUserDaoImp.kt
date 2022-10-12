package com.imtiaz.githubuserstest.data.local.dao

import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.util.INSERT_FAIL
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUserDaoImp(
    val users: MutableList<GithubUser> = mutableListOf()
) : UserDao {

    override suspend fun insert(user: GithubUser) {
        TODO("Not yet implemented")
    }

    override suspend fun insertUsers(user: List<GithubUser>) {
        if(testTag == INSERT_FAIL) return
        else users.addAll(user)
    }

    override suspend fun updateUsers(user: List<GithubUser>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> {
        val list = mutableListOf<GithubUser>()
        for (user in users) {
            if(user.login.contains(searchText) || user.note?.contains(searchText) == true){
                list.add(user)
            }
        }
        return list
    }

    override fun getUsers(): Flow<List<GithubUser>> = flow { emit(users) }
}