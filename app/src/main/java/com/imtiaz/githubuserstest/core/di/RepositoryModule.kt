package com.imtiaz.githubuserstest.core.di

import com.imtiaz.githubuserstest.data.mapper.GithubUserMapper
import com.imtiaz.githubuserstest.data.mapper.ProfileMapper
import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import com.imtiaz.githubuserstest.data.remote.repository.ProfileRepositoryImp
import com.imtiaz.githubuserstest.data.remote.repository.UsersRepositoryImp
import com.imtiaz.githubuserstest.data.remote.service.ApiService
import com.imtiaz.githubuserstest.data.remote.service.ProfileService
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
    fun provideUsersRepository(service: ApiService, mapper: GithubUserMapper): UsersRepository =
        UsersRepositoryImp(service, mapper)

    @Provides
    @Singleton
    fun provideUserProfile(service: ProfileService, mapper: ProfileMapper): ProfileRepository =
        ProfileRepositoryImp(service, mapper)
}