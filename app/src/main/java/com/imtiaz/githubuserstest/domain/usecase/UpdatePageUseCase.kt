package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.domain.repository.PageRepository
import javax.inject.Inject

class UpdatePageUseCase @Inject constructor(
    private val repository: PageRepository
) {
    suspend fun execute() = repository.updateLastPage()
}