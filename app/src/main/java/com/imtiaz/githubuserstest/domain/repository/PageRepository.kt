package com.imtiaz.githubuserstest.domain.repository

interface PageRepository {
    suspend fun updateLastPage()
    fun getLastPage(): Int
}