package com.imtiaz.githubuserstest.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.usecase.FetchUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchUsersUseCase: FetchUsersUseCase,
) : ViewModel() {

    private var _fetchUsersStateFlow: MutableStateFlow<Resource<List<GithubUser>>> =
        MutableStateFlow(Resource.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    var usersFlow: Flow<List<GithubUser>> = MutableStateFlow(listOf())

    fun getUsers(): Flow<List<GithubUser>> = getUsersUseCase.execute()

    fun fetchUsers() {
        viewModelScope.launch {
            fetchUsersUseCase.execute().collect {
                _fetchUsersStateFlow.value = it
            }
        }
    }

}