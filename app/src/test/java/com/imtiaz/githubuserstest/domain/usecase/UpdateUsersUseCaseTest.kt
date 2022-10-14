package com.imtiaz.githubuserstest.domain.usecase

import com.google.gson.GsonBuilder
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.FETCH_AND_INSERT_USERS_SUCCESS
import com.imtiaz.githubuserstest.data.util.FETCH_AND_UPDATE_FAIL
import com.imtiaz.githubuserstest.data.util.FETCH_AND_UPDATE_USERS
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.data.util.getUpdatedUsersResponse
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
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
 * 2. Successfully fetched & data updating failed
 *      - fetch and parse updated user response
 *      - updating users info failed
 *      - confirm fetch updated users success
 *      - confirm user info updating in db failed
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

        // all instance created for fake repository
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

        // preset api response
        val updatedUsersFromApi = getUpdatedUsersResponse()

        // preset api mapping response
        val updatedUsersAfterMap = GithubUserMapper().mapFromEntityList(updatedUsersFromApi)

        // fetching updated user response from mock server
        updateUsersUseCase.execute(0)
            .collect(object : FlowCollector<BaseState<List<GithubUserResponse>>> {
                override suspend fun emit(value: BaseState<List<GithubUserResponse>>) {

                    // checking mock response size and preset data response size same
                    Assertions.assertTrue { (value as BaseState.Success).data.size == 3 }

                    // confirming our mock response and actual response same
                    Assertions.assertEquals((value as BaseState.Success).data, updatedUsersFromApi)
                }
            })

        // fetching users from local db
        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {
                for (updatedUser in updatedUsersAfterMap) {

                    // in every loop we're checking/matching everything updated properly
                    Assertions.assertTrue { value.contains(updatedUser) }
                }
            }
        })
    }

    @Test
    fun `fetch updated users and confirm failed to update in db`() = runBlocking {
        testTag = FETCH_AND_UPDATE_FAIL

        // preset api response
        val updatedUsersFromApi = getUpdatedUsersResponse()

        // preset api mapping response
        val updatedUsersAfterMap = GithubUserMapper().mapFromEntityList(updatedUsersFromApi)

        // fetching updated user response from mock server
        updateUsersUseCase.execute(0)
            .collect(object : FlowCollector<BaseState<List<GithubUserResponse>>> {
                override suspend fun emit(value: BaseState<List<GithubUserResponse>>) {

                    // checking mock response size and preset data response size same
                    Assertions.assertTrue { (value as BaseState.Success).data.size == 3 }

                    // confirming our mock response and actual preset data response equal
                    Assertions.assertEquals((value as BaseState.Success).data, updatedUsersFromApi)
                }
            })

        // fetching users from local db
        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {

                // checking is data updating failed
                Assertions.assertTrue { value[0] != updatedUsersAfterMap[0] }
                Assertions.assertTrue { value[1] != updatedUsersAfterMap[1] }
            }
        })
    }

    @Test
    fun `fetch updated users and confirm only basic information updated`() = runBlocking {
        testTag = FETCH_AND_UPDATE_USERS

        // preset api response
        val updatedUsersFromApi = getUpdatedUsersResponse()

        // preset api mapping response
        val updatedUsers = GithubUserMapper().mapFromEntityList(updatedUsersFromApi)

        // initially updating first user note
        val user = GithubUser(
            id = 1,
            login = "mojombo",
            note = "New Note Added",
            followers = 100
        )
        dao.updateUser(user)

        // fetching updated user response from mock server
        updateUsersUseCase.execute(0)
            .collect(object : FlowCollector<BaseState<List<GithubUserResponse>>> {
                override suspend fun emit(value: BaseState<List<GithubUserResponse>>) {

                    // checking mock response size and preset data response size same
                    Assertions.assertTrue { (value as BaseState.Success).data.size == 3 }

                    // confirming our mock response and actual preset data response equal
                    Assertions.assertEquals((value as BaseState.Success).data, updatedUsersFromApi)
                }
            })

        repository.getUsers().collect(object : FlowCollector<List<GithubUser>> {
            override suspend fun emit(value: List<GithubUser>) {
                // checking updated avatar and api response avatar matching
                Assertions.assertTrue { value[0].avatarUrl == updatedUsers[0].avatarUrl }

                // checking updated login and api response login matching
                Assertions.assertTrue { value[0].login == updatedUsers[0].login }

                // api response doesn't contain any note param that's why note value not updated
                Assertions.assertTrue { value[0].note != updatedUsers[0].note }

                // current note not replaced by api response, that's why note value remains same
                Assertions.assertTrue { value[0].note != null && value[0].note?.isNotEmpty() == true }
            }
        })
    }

}