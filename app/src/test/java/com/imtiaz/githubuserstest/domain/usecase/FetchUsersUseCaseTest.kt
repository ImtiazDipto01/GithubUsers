package com.imtiaz.githubuserstest.domain.usecase

import com.google.gson.GsonBuilder
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.HTTP_ERROR
import com.imtiaz.githubuserstest.data.util.INSERT_FAIL
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1.Successful fetch user and Successful Insert
 *      - fetch and parse user response
 *      - insert user
 *      - confirm fetch user success
 *      - confirm insert user success
 *
 * 2. Http Response error and no data inserted
 *      - hit a Http error api
 *      - no insertion in db
 *      - confirm Bad response with error state
 *      - confirm no data inserted
 *
 * 3. successful users fetch but Insert failed
 *      - Fetch a successful response
 *      - Failed to insert in db
 *      - confirm successful fetch
 *      - confirm failed to insert
 *
 *
 * @constructor Create empty Fetch users use case test
 */
@OptIn(InternalCoroutinesApi::class)
class FetchUsersUseCaseTest {

    // system in test
    private lateinit var fetchUsersUseCase: FetchUsersUseCase

    private lateinit var repository: UsersRepository
    private lateinit var dao: UserDao

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @BeforeEach
    fun setup() {
        //created mock server
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseurl = mockWebServer.url("/")

        // created api services
        apiService = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        // all instance created for Use case
        dao = FakeUserDaoImp()
        repository = FakeUsersRepositoryImp(dao, apiService, mockWebServer)
        fetchUsersUseCase = FetchUsersUseCase(
            repository = repository
        )
    }

    @Test
    fun `fetch and insert users and confirm fetch and insert users is successful`() = runBlocking {
        testTag = FETCH_AND_INSERT_USERS_SUCCESS

        val userListFromApi = mutableListOf<GithubUser>()

        // fetching user list response from mock server
        fetchUsersUseCase.execute(0).collect(object : FlowCollector<BaseState<List<GithubUser>>> {
            override suspend fun emit(value: BaseState<List<GithubUser>>) {

                // adding full user list what we got server
                userListFromApi.addAll((value as BaseState.Success).data)

                // confirming our mock response is not empty
                assertTrue { (value as BaseState.Success).data.isNotEmpty() }

                // confirming our mock response data size is 3, means successful fetch
                assertTrue { (value as BaseState.Success).data.size == 3 }
            }
        })

        delay(200)

        // fetching data from local db
        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {

                // confirming our local db contains 3 users
                assertTrue { value.size == 3 }

                // confirming our local db user info matches with the server user info
                assertTrue { value[0] == userListFromApi[0] }
            }
        })
    }

    @Test
    fun `hit a http error and confirm fetch is unsuccessful and no insert happen`() {
        runBlocking {
            testTag = HTTP_ERROR

            // fetching user list response from mock server
            fetchUsersUseCase.execute(0).collect(object : FlowCollector<BaseState<List<GithubUser>>> {
                override suspend fun emit(value: BaseState<List<GithubUser>>) {

                    // confirming that we got HTTP ERROR
                    assertTrue { (value as BaseState.Error).err.code == HttpURLConnection.HTTP_INTERNAL_ERROR }
                }
            })

            // fetching data from local db
            repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
                override suspend fun emit(value: List<GithubUser>) {

                    // confirming our local db user list is empty, means no data inserted
                    assertTrue { value.isEmpty() }
                }
            })
        }
    }

    @Test
    fun `fetch users successfully fetched but inserting users failed`() {
        runBlocking {
            testTag = INSERT_FAIL

            val userlist = mutableListOf<GithubUser>()

            // fetching user list response from mock server
            fetchUsersUseCase.execute(0).collect(object : FlowCollector<BaseState<List<GithubUser>>> {
                override suspend fun emit(value: BaseState<List<GithubUser>>) {

                    // confirming our response is not empty
                    assertTrue { (value as BaseState.Success).data.isNotEmpty() }

                    // confirming our mock response data size is 3, means successful fetch
                    assertTrue { (value as BaseState.Success).data.size == 3 }

                    // adding full user list what we got server
                    userlist.addAll((value as BaseState.Success).data)
                }
            })

            delay(200)

            repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
                override suspend fun emit(value: List<GithubUser>) {

                    // confirming our local db user list size not matching with response list
                    assertTrue { value.size != userlist.size }
                }
            })

            val searchedUsers = repository.searchUsersByLoginOrNote(userlist[2].login)

            // confirming local db user list empty, insertion failed
            assertTrue { searchedUsers.isEmpty() }
        }
    }
}