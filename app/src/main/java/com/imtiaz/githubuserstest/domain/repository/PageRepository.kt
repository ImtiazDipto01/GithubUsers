package com.imtiaz.githubuserstest.domain.repository

import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    suspend fun updateLastPage()
    fun getLastPage(): Int
}