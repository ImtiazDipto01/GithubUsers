package com.imtiaz.githubuserstest.domain.repository

interface PageRepository {
    suspend fun getSince(): Int
}
