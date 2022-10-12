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
    suspend fun insert(updatedPage: Page)

    @Query("select since from page where `key` = :pageKey")
    suspend fun getSince(pageKey: String = PAGE_KEY): Int
}
