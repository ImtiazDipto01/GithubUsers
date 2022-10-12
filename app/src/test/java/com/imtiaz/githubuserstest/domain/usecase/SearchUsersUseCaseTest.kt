package com.imtiaz.githubuserstest.domain.usecase

import com.google.gson.GsonBuilder
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.*
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * 1. Search Users in db using sub string from login id and successfully returns users
 *      - search user in db by giving sub string of login id
 *      - confirm get a successful list of users
 *
 * 2. Search Users in db using sub string from note and successfully returns users
 *      - search user in db by giving sub string from notes as search key
 *      - confirm get a successful list of users
 *
 * @constructor Create empty Update users use case test
 */
@OptIn(InternalCoroutinesApi::class)
class SearchUsersUseCaseTest {

    // system in test
    private lateinit var searchUsersUseCase: SearchUsersUseCase

    private lateinit var repository: UsersRepository
    private lateinit var dao: UserDao

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseurl = mockWebServer.url("/")
        apiService = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        val users = GithubUserMapper().mapFromEntityList(getInitialUserResponse()).map {
            it.copy(note = if (it.id == 1) "Backend Developer" else "Frontend Developer")
        }

        dao = FakeUserDaoImp(users.toMutableList())
        repository = FakeUsersRepositoryImp(dao, apiService, mockWebServer)
        searchUsersUseCase = SearchUsersUseCase(repository)

    }

    @Test
    fun `search users by login id as searchKey and confirm successful user list`() = runBlocking {
        testTag = SEARCH_USERS

        val list = searchUsersUseCase.execute("mojombo")
        Assertions.assertTrue { list.size == 1 }
        Assertions.assertTrue { list[0].login == "mojombo" }

        val list2 = searchUsersUseCase.execute("fun")
        Assertions.assertTrue { list2.size == 1 }
        Assertions.assertTrue { list2[0].login == "defunkt" }
    }

    @Test
    fun `search users by using substring from note as searchKey and confirm successful user list`() {
        runBlocking {
            testTag = SEARCH_USERS

            val list = searchUsersUseCase.execute("end")
            Assertions.assertTrue { list.size == 3 }
            Assertions.assertTrue { list[0].login == "mojombo" }
            Assertions.assertTrue { list[2].login == "pjhyett" }
            Assertions.assertTrue { list[1].note == "Frontend Developer" }
        }
    }

    @Test
    fun `search users by using searchKey but result failed`() {
        runBlocking {
            testTag = SEARCH_FAIL

            val list = searchUsersUseCase.execute("end")
            Assertions.assertTrue { list.isEmpty() }
        }
    }

    @Test
    fun `search users by using searchKey and return empty List`() {
        runBlocking {
            testTag = SEARCH_USERS

            val list = searchUsersUseCase.execute("hull")
            Assertions.assertTrue { list.size == 0 }
        }
    }

}

