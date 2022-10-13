package com.imtiaz.githubuserstest.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.ErrorHandler
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.preference.PreferenceHelper
import com.imtiaz.githubuserstest.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchUsersUseCase: FetchUsersUseCase,
    private val updateUsersUseCase: UpdateUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getPageUserCase: GetPageUseCase,
    private val pref: PreferenceHelper
) : ViewModel() {

    private var _fetchUsersStateFlow: MutableStateFlow<BaseState<List<GithubUser>>> =
        MutableStateFlow(BaseState.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    var errorHandler: ErrorHandler? = null

    init {
        pref.clearList()
    }

    fun fetchUsers(since: Int) {
        viewModelScope.launch {
            fetchUsersUseCase.execute(since).collect {
                _fetchUsersStateFlow.value = it
            }
        }
    }

    fun startPaging() {
        viewModelScope.launch {
            val since = getPageUserCase.execute()
            fetchUsers(since)
        }
    }

    suspend fun searchUsers(searchText: String): List<GithubUser> =
        withContext(Dispatchers.IO) { searchUsersUseCase.execute(searchText) }

    fun updateUsersData(since: Int) {
        if (!pref.isSinceContain(since))
            updateUsers(since)
    }

    private fun updateUsers(since: Int) =
        viewModelScope.launch {
            updateUsersUseCase.execute(since)
        }

    fun getUsers(): Flow<List<GithubUser>> = getUsersUseCase.execute()

}
