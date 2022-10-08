package com.imtiaz.githubuserstest.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.usecase.FetchUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.UpdatePageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchUsersUseCase: FetchUsersUseCase,
    private val updatePageUseCase: UpdatePageUseCase,
) : ViewModel() {

    private var _fetchUsersStateFlow: MutableStateFlow<Resource<List<GithubUser>>> =
        MutableStateFlow(Resource.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    fun fetchUsers() {
        viewModelScope.launch {
            fetchUsersUseCase.execute(1).collect {
                _fetchUsersStateFlow.value = it
            }
        }
    }

    fun getUsers(): Flow<List<GithubUser>> = getUsersUseCase.execute()

    fun updateLastPage() = viewModelScope.launch { updatePageUseCase.execute() }

}