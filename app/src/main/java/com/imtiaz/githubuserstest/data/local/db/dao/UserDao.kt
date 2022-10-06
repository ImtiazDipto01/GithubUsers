package com.imtiaz.githubuserstest.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GithubUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(user: List<GithubUser>)

    @Query("delete from user")
    suspend fun deleteUsers()

    @Query("select * from user")
    suspend fun getUser(): List<GithubUser>
}