package com.imtiaz.githubuserstest.data.local.dao

import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePageDaoImp(
    private val page: Page? = null,
) : PageDao {
    override suspend fun insert(updatedPage: Page) {
        page?.copy(key = updatedPage.key, since = updatedPage.since)
    }

    override suspend fun getSince(pageKey: String): Int {
        return page?.let {
            if(it.key == pageKey) it.since
            else -1
        } ?: -1
    }

}