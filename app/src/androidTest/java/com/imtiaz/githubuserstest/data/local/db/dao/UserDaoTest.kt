package com.imtiaz.githubuserstest.data.local.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.imtiaz.githubuserstest.data.local.db.AppDatabase
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(InternalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }


    @Test
    fun insertUser_returnTrue() = runBlocking {
        val githubUser = GithubUser(
            id = 1,
            login = "mojombo",
            nodeId = "MDQ6VXNlcjE=",
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            url = "https://api.github.com/users/mojombo",
            since = 0,
            name = "Tom Preston-Werner",
            location = "San Francisco",
            note = null,
            publicRepos = 64,
            publicGists = 62,
            followers = 23209,
            following = 11
        )
        dao.insert(githubUser)

        dao.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {

                /*assertTrue(value.size == 0)
                value.size*/

                // confirming user available in local db
                assertThat(value).contains(githubUser)
            }
        })
    }


}