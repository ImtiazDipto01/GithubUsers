package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.core.extensions.handleApiResponse
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.util.*
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.lang.Exception
import java.net.HttpURLConnection

class FakeUsersRepositoryImp(
    private val userDao: UserDao,
    private val service: ApiService,
    private val mockServer: MockWebServer,
    private val mapper: GithubUserMapper = GithubUserMapper(),
) : UsersRepository {

    override suspend fun fetchUsers(since: Int): Flow<State<List<GithubUser>>> {
        return when (testTag) {
            FETCH_AND_INSERT_USERS_SUCCESS -> successfulFetchAndInsert(since)
            INSERT_FAIL -> successfulFetchAndInsertFailed(since)
            HTTP_ERROR -> badResponse(since)
            else -> successfulFetchAndInsert(since)
        }
    }

    override suspend fun updateUsers(since: Int): Flow<State<List<GithubUserResponse>>> {
        return when (testTag) {
            FETCH_AND_UPDATE_USERS -> successfulFetchAndUpdateUsers(since)
            FETCH_AND_UPDATE_FAIL -> successfulFetchAndFailedToUpdate(since)
            else -> successfulFetchAndUpdateUsers(since)
        }
    }

    override suspend fun updateUsers(users: List<GithubUser>) {
        userDao.updateUsers(users)
    }

    override suspend fun insertUsers(users: List<GithubUser>) {
        userDao.insertUsers(users)
    }

    override suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser> {
        return userDao.searchUsersByLoginOrNote(searchText)
    }

    override fun getUsers(): Flow<List<GithubUser>> = userDao.getUsers()

    private suspend fun successfulFetchAndInsert(since: Int): Flow<State<List<GithubUser>>> {

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(successfullResponse)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.fetchUsers(since)
        val result = handleApiResponse(actualResponse)

        val users = mapper.mapFromEntityList(result as List<GithubUserResponse>, since)

        insertUsers(users)
        return flow { emit(State.Success(users)) }
    }

    private suspend fun successfulFetchAndInsertFailed(since: Int): Flow<State<List<GithubUser>>> {

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(successfullResponse)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.fetchUsers(since)
        val result = handleApiResponse(actualResponse)

        val users = mapper.mapFromEntityList(result as List<GithubUserResponse>, since)

        insertUsers(users)
        return flow { emit(State.Success(users)) }
    }

    private suspend fun badResponse(since: Int): Flow<State<List<GithubUser>>> {
        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.fetchUsers(since)

        return flow {
            emit(
                State.Error(
                    ErrorHandler("Http Error", Exception("Http Error"), actualResponse.code())
                )
            )
        }
    }

    private suspend fun successfulFetchAndUpdateUsers(since: Int): Flow<State<List<GithubUserResponse>>> {

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(updatedUsersResponse)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.fetchUsers(since)
        val result = handleApiResponse(actualResponse)

        val users = mapper.mapFromEntityList(result as List<GithubUserResponse>, since)

        updateUsers(users)
        return flow { emit(State.Success(result)) }
    }

    private suspend fun successfulFetchAndFailedToUpdate(since: Int): Flow<State<List<GithubUserResponse>>> {

        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(updatedUsersResponse)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.fetchUsers(since)
        val result = handleApiResponse(actualResponse)

        val users = mapper.mapFromEntityList(result as List<GithubUserResponse>, since)

        updateUsers(users)
        return flow { emit(State.Success(result)) }
    }

}
