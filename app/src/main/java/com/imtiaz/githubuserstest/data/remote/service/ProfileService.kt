package com.imtiaz.githubuserstest.data.remote.service

import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {

    @GET("users/{loginId}")
    suspend fun getUserProfile(@Path("loginId") loginId: String): Response<UserProfileResponse>
}
