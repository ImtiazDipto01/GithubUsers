package com.imtiaz.githubuserstest.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.usecase.GetProfileUseCase
import com.imtiaz.githubuserstest.presentation.profile.ui.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private var _state = mutableStateOf<ProfileState>(ProfileState())
    val state: State<ProfileState> = _state

    var profileState = ProfileState()

    fun setInitialState(githubUser: GithubUser) {
        profileState = ProfileState(user = githubUser)
    }

    fun getUserProfile(loginId: String) {
        viewModelScope.launch {
            getProfileUseCase.execute(loginId).collect {
                when (it) {
                    is BaseState.Loading -> _state.value = profileState.copy(isLoading = true)
                    is BaseState.Success -> _state.value = profileState.copy(isLoading = false)
                    is BaseState.Error -> _state.value = profileState.copy(
                        isLoading = false, err = it.err
                    )
                    else -> Unit
                }
            }
        }
    }

}
