package com.imtiaz.githubuserstest.core.di

import android.content.Context
import androidx.room.Room
import com.imtiaz.githubuserstest.data.local.db.AppDatabase
import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideUserDao(appDatabae: AppDatabase): UserDao = appDatabae.userDao()

    @Singleton
    @Provides
    fun providePageDao(appDatabae: AppDatabase): PageDao = appDatabae.pageDao()
}