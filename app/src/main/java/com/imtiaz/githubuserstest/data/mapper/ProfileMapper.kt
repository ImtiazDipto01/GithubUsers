package com.imtiaz.githubuserstest.data.mapper

import com.imtiaz.githubuserstest.core.extensions.EntityMapper
import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import javax.inject.Inject

class ProfileMapper @Inject constructor(): EntityMapper<UserProfileResponse, GithubUser>() {

    override fun mapFromEntity(entity: UserProfileResponse): GithubUser {
        return GithubUser(
            login = entity.login!!,
            avatarUrl = entity.avatarUrl,
            nodeId = entity.nodeId,
            url = entity.url,
            name = entity.name,
            location = entity.location,
            publicRepos = entity.publicRepos ?: 0,
            publicGists = entity.publicGists ?: 0,
            followers = entity.followers ?: 0,
            following = entity.following ?: 0
        )
    }

    override fun mapToEntity(domainModel: GithubUser): UserProfileResponse {
        return UserProfileResponse(
            login = domainModel.login,
            avatarUrl = domainModel.avatarUrl,
            nodeId = domainModel.nodeId,
            url = domainModel.url,
            name = domainModel.name,
            location = domainModel.location,
            publicRepos = domainModel.publicRepos,
            publicGists = domainModel.publicGists,
            followers = domainModel.followers,
            following = domainModel.following
        )
    }

}