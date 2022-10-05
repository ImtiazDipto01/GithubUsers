package com.imtiaz.githubuserstest.data.mapper

import com.imtiaz.githubuserstest.core.extensions.EntityMapper
import com.imtiaz.githubuserstest.data.remote.dto.GithubUserResponse
import com.imtiaz.githubuserstest.domain.model.GithubUser
import javax.inject.Inject

class GithubUserMapper @Inject constructor(): EntityMapper<GithubUserResponse, GithubUser>() {

    override fun mapFromEntity(entity: GithubUserResponse): GithubUser {
        return GithubUser(
            login = entity.login,
            avatarUrl = entity.avatarUrl,
            nodeId = entity.nodeId,
            url = entity.url
        )
    }

    override fun mapToEntity(domainModel: GithubUser): GithubUserResponse {
        return GithubUserResponse(
            login = domainModel.login,
            avatarUrl = domainModel.avatarUrl,
            nodeId = domainModel.nodeId,
            url = domainModel.url
        )
    }

    override fun mapFromEntityList(entityList: List<GithubUserResponse>): List<GithubUser> =
        entityList.map { mapFromEntity(it) }

}