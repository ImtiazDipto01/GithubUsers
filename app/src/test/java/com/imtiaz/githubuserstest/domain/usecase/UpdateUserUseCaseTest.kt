package com.imtiaz.githubuserstest.domain.usecase

import com.google.gson.GsonBuilder
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.dao.FakeUserDaoImp
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.data.repository.FakeProfileRepositoryImp
import com.imtiaz.githubuserstest.data.repository.FakeUsersRepositoryImp
import com.imtiaz.githubuserstest.data.util.*
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
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
 * 1. Successfully update user note info
 *      - update user info with note, publicRepo, followers
 *      - confirm user info successfully updated
 *
 * @constructor Create empty Update users use case test
 */
@OptIn(InternalCoroutinesApi::class)
class UpdateUserUseCaseTest {

    // system in test
    private lateinit var updateUserUseCase: UpdateUserUseCase

    private lateinit var repository: ProfileRepository
    private lateinit var userRepo: UsersRepository
    private lateinit var dao: UserDao

    private lateinit var mockWebServer: MockWebServer
    private lateinit var profileService: ProfileService
    private lateinit var apiService: ApiService

    @BeforeEach
    fun setup() {

        //created mock server
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseurl = mockWebServer.url("/")

        // created api services
        profileService = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ProfileService::class.java)
        apiService = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)

        // all instance created for Use case
        dao = FakeUserDaoImp()
        repository = FakeProfileRepositoryImp(dao, profileService, mockWebServer)
        userRepo = FakeUsersRepositoryImp(dao, apiService, mockWebServer)
        updateUserUseCase = UpdateUserUseCase(repository)

        //before test, we need to fetch and insert users
        testTag = FETCH_AND_INSERT_USERS_SUCCESS
        runBlocking {
            userRepo.fetchUsers(0)
        }
    }

    @Test
    fun `updated user info and confirm fetch and update successful`() = runBlocking {
        testTag = UPDATE_USER_NOTE_SUCCESS

        // valid preset data
        val githubUser = GithubUser(
            id = 1,
            login = "mojombo",
            nodeId = "MDQ6VXNlcjE=",
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            url = "https://api.github.com/users/mojombo",
            since = 0,
            name = "Tom Preston-Werner",
            location = "San Francisco",
            note = "New Note",
            publicRepos = 64,
            publicGists = 62,
            followers = 23209,
            following = 11
        )

        updateUserUseCase.execute(githubUser)

        // fetching users from local db
        repository.getUserProfileFromDB(githubUser.id).collect(object : FlowCollector<GithubUser?> {

            override suspend fun emit(value: GithubUser?) {

                // confirm that we found that user from db
                Assertions.assertTrue { value != null }

                // confirm note updated successfully
                Assertions.assertTrue { value?.note == "New Note" }

                // confirm note updated successfully
                Assertions.assertEquals(value, githubUser)
            }
        })
    }

}