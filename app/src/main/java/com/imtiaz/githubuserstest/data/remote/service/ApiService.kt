package com.imtiaz.githubuserstest.data.remote.service

import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    suspend fun fetchUsers(@Query("since") since: Int = 0): Response<List<GithubUserResponse>>
}