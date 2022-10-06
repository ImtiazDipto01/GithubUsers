package com.imtiaz.githubuserstest.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.db.entity.UserEntity

@Database(
    entities = [GithubUser::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME: String = "app_db"
    }
}