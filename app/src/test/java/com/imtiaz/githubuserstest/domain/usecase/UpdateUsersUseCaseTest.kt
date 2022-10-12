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
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.FETCH_AND_UPDATE_USERS
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.data.util.getUpdatedUsersResponse
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
 * 1. Successfully fetched & updated user updated data and
 *      - fetch and parse updated user response
 *      - updating users info in db
 *      - confirm fetch updated users success
 *      - confirm user info updating in db success
 *
 * @constructor Create empty Update users use case test
 */
@OptIn(InternalCoroutinesApi::class)
class UpdateUsersUseCaseTest {

    // system in test
    private lateinit var updateUsersUseCase: UpdateUsersUseCase

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
        updateUsersUseCase = UpdateUsersUseCase(repository)

        //before checking update user info, we need to fetch and insert users
        testTag = FETCH_AND_INSERT_USERS_SUCCESS
        runBlocking {
            repository.fetchUsers(0)
        }
    }

    @Test
    fun `fetch updated users and confirm fetch and update successful`() = runBlocking {
        testTag = FETCH_AND_UPDATE_USERS

        val updatedUsersFromApi = getUpdatedUsersResponse()
        val updatedUsersAfterMap = GithubUserMapper().mapFromEntityList(updatedUsersFromApi)

        updateUsersUseCase.execute(0).collect(object : FlowCollector<State<List<GithubUserResponse>>> {
            override suspend fun emit(value: State<List<GithubUserResponse>>) {
                Assertions.assertTrue { (value as State.Success).data.size == 3 }
                Assertions.assertEquals((value as State.Success).data, updatedUsersFromApi)
            }
        })

        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {
                for(updatedUser in updatedUsersAfterMap) {
                    Assertions.assertTrue { value.contains(updatedUser) }
                }
            }
        })
    }

}