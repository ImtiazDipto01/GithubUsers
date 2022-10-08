package com.imtiaz.githubuserstest.data.repository

import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.entity.Page
import com.imtiaz.githubuserstest.domain.repository.PageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PageRepositoryImp @Inject constructor(
    val pageDao: PageDao
) : PageRepository {

    override suspend fun updateLastPage() {
        withContext(Dispatchers.IO) {
            val lastPage = pageDao.getLastPage()
            pageDao.insert(Page(lastPage = lastPage + 1))
        }
    }

    override fun getLastPage(): Int = pageDao.getLastPage()

}
