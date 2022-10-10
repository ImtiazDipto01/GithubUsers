package com.imtiaz.githubuserstest.presentation.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.data.local.preference.PreferenceHelper
import com.imtiaz.githubuserstest.domain.usecase.FetchUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetUsersUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetPageUseCase
import com.imtiaz.githubuserstest.domain.usecase.UpdateUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val fetchUsersUseCase: FetchUsersUseCase,
    private val updateUsersUseCase: UpdateUsersUseCase,
    private val getPageUserCase: GetPageUseCase,
    private val pref: PreferenceHelper
) : ViewModel() {

    private var _fetchUsersStateFlow: MutableStateFlow<State<List<GithubUser>>> =
        MutableStateFlow(State.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    init {
        pref.clearList()
    }

    fun fetchUsers(since: Int) {
        viewModelScope.launch {
            /*fetchUsersUseCase.execute(since).collect {
                _fetchUsersStateFlow.value = it
            }*/
        }
    }

    fun startPaging() {
        viewModelScope.launch {
            val since = getPageUserCase.execute()
            fetchUsers(since)
        }
    }

    fun updateUsersData(since: Int) {
        if (!pref.isSinceContain(since))
            updateUsers(since)
    }

    private fun updateUsers(since: Int) =
        viewModelScope.launch {
            //updateUsersUseCase.execute(since)
        }

    fun getUsers(): Flow<List<GithubUser>> = getUsersUseCase.execute()

}
