package com.imtiaz.githubuserstest.core.di

import com.imtiaz.githubuserstest.data.local.db.dao.PageDao
import com.imtiaz.githubuserstest.data.local.db.dao.UserDao
import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.mapper.ProfileMapper
import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import com.imtiaz.githubuserstest.data.repository.ProfileRepositoryImp
import com.imtiaz.githubuserstest.data.repository.UsersRepositoryImp
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
import com.imtiaz.githubuserstest.data.repository.PageRepositoryImp
import com.imtiaz.githubuserstest.domain.repository.PageRepository
import com.imtiaz.githubuserstest.domain.repository.ProfileRepository
import com.imtiaz.githubuserstest.domain.repository.UsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUsersRepository(
        service: ApiService,
        mapper: GithubUserMapper,
        userDao: UserDao,
        pageDao: PageDao
    ): UsersRepository = UsersRepositoryImp(service, mapper, userDao, pageDao)

    @Provides
    @Singleton
    fun provideUserProfile(service: ProfileService, mapper: ProfileMapper): ProfileRepository =
        ProfileRepositoryImp(service, mapper)

    @Provides
    @Singleton
    fun providePage(pageDao: PageDao): PageRepository =
        PageRepositoryImp(pageDao)
}