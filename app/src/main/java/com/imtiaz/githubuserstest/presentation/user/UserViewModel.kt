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

    // observing only loading and error state, Ui doesn't consume any data from this flow
    private var _fetchUsersStateFlow: MutableStateFlow<BaseState<List<GithubUser>>> =
        MutableStateFlow(BaseState.Empty())
    val fetchUsersFlow = _fetchUsersStateFlow.asStateFlow()

    // only data source that provide data to UI
    private var _usersListStateFlow: MutableStateFlow<List<GithubUser>?> =
        MutableStateFlow(null)
    val userListFlow = _usersListStateFlow.asStateFlow()

    // If any error happens when fetching data
    // errorHandle save that error as last error so that,
    // when network connectivity get change ui can determine by checking error
    // that any data need fetch or not
    var errorHandler: ErrorHandler? = null

    // contains user search text, useful for screen rotation
    // if any searchText available at that time
    var searchText: String = ""

    init {
        // as App get started, clearing recent successfully fetched since id
        pref.clearList()
    }

    // fetching user list from server
    fun fetchUsers(since: Int) {
        viewModelScope.launch {
            fetchUsersUseCase.execute(since).collect {
                _fetchUsersStateFlow.value = it
            }
        }
    }

    // for paging
    fun startPaging() {
        viewModelScope.launch {
            // getting last since id from db
            val since = getPageUserCase.execute()
            fetchUsers(since)
        }
    }

    // for searching & fetching user list
    suspend fun searchUsers(searchText: String): List<GithubUser> =
        withContext(Dispatchers.IO) { searchUsersUseCase.execute(searchText) }

    // for fetching updated user data from server
    fun updateUsersData(since: Int) {

        // checking is this page fetched in current app runtime,
        // if yes then we're not fetching for multiple time
        viewModelScope.launch {
            if (!pref.isSinceContain(since)) {
                updateUsersUseCase.execute(since)
            }
        }
    }

    // fetch all the users from our local database and
    // [_usersListStateFlow] collect that data for UI
    suspend fun getUsers() {
        return withContext(Dispatchers.Main) {
            getUsersUseCase.execute().collect {
                _usersListStateFlow.value = it
            }
        }
    }

}
