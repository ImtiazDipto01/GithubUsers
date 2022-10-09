package com.imtiaz.githubuserstest.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.usecase.FetchUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchUsersUseCase: FetchUsersUseCase,
    private val getPageUserCase: GetPageUseCase,
) : ViewModel() {

    private var _fetchUsersStateFlow: MutableStateFlow<State<List<GithubUser>>> =
        MutableStateFlow(State.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    fun fetchUsers(page: Int) {
        viewModelScope.launch {
            fetchUsersUseCase.execute(page).collect {
                _fetchUsersStateFlow.value = it
            }
        }
    }

    fun getUsers(): Flow<List<GithubUser>> = getUsersUseCase.execute()

    fun startPaging() {

    }

}