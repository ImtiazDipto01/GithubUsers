package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.core.extensions.handleApiResponse
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.mapper.ProfileMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.data.util.*
import com.imtiaz.githubuserstest.data.util.TestUtil.testTag
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.lang.Exception
import java.net.HttpURLConnection

class FakeProfileRepositoryImp(
    private val userDao: UserDao,
    private val service: ProfileService,
    private val mockServer: MockWebServer,
    private val mapper: ProfileMapper = ProfileMapper(),
) : ProfileRepository {

    override suspend fun getUserProfile(loginId: String): Flow<BaseState<GithubUser>> {
        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(profileResponse)
        mockServer.enqueue(expectedResponse)

        val actualResponse = service.getUserProfile(loginId)
        val result = handleApiResponse(actualResponse)

        val profile = mapper.mapFromEntity(result as UserProfileResponse)

        updateUser(profile)
        return flow { emit(BaseState.Success(profile)) }
    }

    override suspend fun updateUser(user: GithubUser) {
        userDao.updateUser(user)
    }

    override suspend fun getUserProfileFromDB(id: Int): Flow<GithubUser?> {
        return userDao.getUserFlowAble(id)
    }


}
