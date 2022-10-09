package com.imtiaz.githubuserstest.domain.usecase

import com.imtiaz.githubuserstest.domain.repository.PageRepository
import javax.inject.Inject

class GetPageUseCase @Inject constructor(
    private val repository: PageRepository
) {
    suspend fun execute() = repository.getSince()
}