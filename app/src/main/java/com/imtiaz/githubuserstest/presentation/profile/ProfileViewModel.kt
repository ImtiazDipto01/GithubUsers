package com.imtiaz.githubuserstest.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private var _profileStateFlow: MutableStateFlow<State<GithubUser>> =
        MutableStateFlow(State.Empty())
    val profileFlow = _profileStateFlow.asStateFlow()

    fun getUserProfile(loginId: String) {
        viewModelScope.launch {
            getProfileUseCase.execute(loginId).collect {
                _profileStateFlow.value = it
            }
        }
    }

}