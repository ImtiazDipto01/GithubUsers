package com.imtiaz.githubuserstest.data.mapper

import com.imtiaz.githubuserstest.core.extensions.EntityMapper
import com.imtiaz.githubuserstest.data.remote.dto.UserProfileResponse
import com.imtiaz.githubuserstest.domain.model.GithubUser
import javax.inject.Inject

class ProfileUserMapper @Inject constructor(): EntityMapper<UserProfileResponse, GithubUser>() {

    override fun mapFromEntity(entity: UserProfileResponse): GithubUser {
        return GithubUser(
            login = entity.login,
            avatarUrl = entity.avatarUrl,
            nodeId = entity.nodeId,
            url = entity.url,
            name = entity.name,
            location = entity.location,
            public_repo = entity.publicRepos ?: 0,
            public_gist = entity.publicGists ?: 0,
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
            publicRepos = domainModel.public_repo,
            publicGists = domainModel.public_gist,
            followers = domainModel.followers,
            following = domainModel.following
        )
    }

}