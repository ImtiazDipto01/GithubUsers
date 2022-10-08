package com.imtiaz.githubuserstest.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imtiaz.githubuserstest.core.extensions.PAGE_KEY
import com.imtiaz.githubuserstest.data.local.db.entity.Page

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Page)

    @Query("select last_page from page where `key` = :pageKey")
    fun getLastPage(pageKey: String = PAGE_KEY): Int
}