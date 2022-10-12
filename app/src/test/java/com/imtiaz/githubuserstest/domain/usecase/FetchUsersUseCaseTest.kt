package com.imtiaz.githubuserstest.domain.usecase

import com.google.gson.GsonBuilder
import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.HTTP_ERROR
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1.Successful fetch user and Successful Insert
 *      - fetch and parse user response
 *      - insert note
 *      - confirm fetch user success
 *      - confirm insert user success
 *
 * 2. Http Response error and no data inserted
 *      - hit a Http error api
 *      - no insertion in db
 *      - confirm Bad response with error state
 *      - confirm no data inserted
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
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseurl = mockWebServer.url("/")
        apiService = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        dao = FakeUserDaoImp()
        repository = FakeUsersRepositoryImp(dao, apiService, mockWebServer)
        fetchUsersUseCase = FetchUsersUseCase(
            repository = repository
        )
    }

    @Test
    fun `fetch and insert users and confirm fetch and insert users is successful`() = runBlocking {
        testTag = FETCH_AND_INSERT_USERS_SUCCESS

        fetchUsersUseCase.execute(0).collect(object : FlowCollector<State<List<GithubUser>>> {
            override suspend fun emit(value: State<List<GithubUser>>) {
                assertTrue { (value as State.Success).data.isNotEmpty() }
                assertTrue { (value as State.Success).data.size == 3 }
            }
        })

        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {
                assertTrue { value.size == 3 }
            }
        })
    }

    @Test
    fun `hit a http error and confirm fetch is unsuccessful and no insert happen`() =

        runBlocking {
            testTag = HTTP_ERROR

            fetchUsersUseCase.execute(0).collect(object : FlowCollector<State<List<GithubUser>>> {
                override suspend fun emit(value: State<List<GithubUser>>) {
                    assertTrue { (value as State.Error).err.code == HttpURLConnection.HTTP_INTERNAL_ERROR }
                }
            })

            repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
                override suspend fun emit(value: List<GithubUser>) {
                    assertTrue { value.isEmpty() }
                }
            })
        }
}