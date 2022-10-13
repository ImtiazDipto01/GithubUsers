package com.imtiaz.githubuserstest.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GithubUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(user: List<GithubUser>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUsers(user: List<GithubUser>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: GithubUser)

    @Query("delete from user")
    suspend fun deleteUsers()

    @Query("select * from user where login like :searchText or note like :searchText order by id asc")
    suspend fun searchUsersByLoginOrNote(searchText: String): List<GithubUser>

    @Query("select * from user where login = :searchText")
    fun getUser(searchText: String): Flow<GithubUser?>

    @Query("select * from user order by id asc")
    fun getUsers(): Flow<List<GithubUser>>
}
