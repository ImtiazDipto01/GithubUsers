package com.imtiaz.githubuserstest.data.local.dao

import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.util.FETCH_AND_UPDATE_FAIL
import com.imtiaz.githubuserstest.data.util.INSERT_FAIL
import com.imtiaz.githubuserstest.data.util.SEARCH_FAIL
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

    override suspend fun updateUsers(updatedUsers: List<GithubUser>) {
        if(testTag == FETCH_AND_UPDATE_FAIL) return

        for(updatedUser in updatedUsers) {
            val isUserAlreadyExist = users.any { it.login == updatedUser.login }
            val currUser = users.find { it.login == updatedUser.login }
            if(isUserAlreadyExist && currUser != null && currUser != updatedUser){
                val index = users.indexOf(currUser)
                users[index] = updatedUser
            }
            else if(!isUserAlreadyExist)
                users.add(updatedUser)
        }
    }

    override suspend fun updateUser(user: GithubUser) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUsers() {
        TODO("Not yet implemented")
    }

    override fun getUser(searchText: String): Flow<GithubUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> {
        val list = mutableListOf<GithubUser>()

        if(testTag == SEARCH_FAIL) return list

        for (user in users) {
            if(user.login.contains(searchText) || user.note?.contains(searchText) == true){
                list.add(user)
            }
        }
        return list
    }

    override fun getUsers(): Flow<List<GithubUser>> = flow { emit(users) }
}