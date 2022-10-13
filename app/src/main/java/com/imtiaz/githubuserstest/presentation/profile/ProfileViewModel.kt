package com.imtiaz.githubuserstest.presentation.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imtiaz.githubuserstest.core.extensions.BaseState
import com.imtiaz.githubuserstest.domain.usecase.GetProfileFromDbUseCase
import com.imtiaz.githubuserstest.domain.usecase.GetProfileUseCase
import com.imtiaz.githubuserstest.domain.usecase.UpdateUserUseCase
import com.imtiaz.githubuserstest.presentation.profile.ui.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getUserFromDbUseCase: GetProfileFromDbUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _profileState = ProfileState()
    var profileState = _profileState

    val userNote = mutableStateOf("")

    fun getUser(id: Int, loginId: String) {
        viewModelScope.launch {
            getUserFromDbUseCase.execute(id).collect {
                _profileState.user.value = it
                userNote.value = it?.note ?: ""
            }
        }
        fetchUserProfile(loginId)
    }

    private fun fetchUserProfile(loginId: String) {
        viewModelScope.launch {
            getProfileUseCase.execute(loginId).collect {
                when (it) {
                    is BaseState.Loading -> _profileState.isLoading.value = true
                    is BaseState.Success -> _profileState.isLoading.value = false
                    is BaseState.Error -> _profileState.apply {
                        isLoading.value = false
                        _profileState.err.value = it.err
                    }
                    else -> Unit
                }
            }
        }
    }

    fun updateUser() {
        val user = _profileState.user.value
        user?.let {
            val updated = it.copy(note = userNote.value)
            viewModelScope.launch { updateUserUseCase.execute(updated) }
        }
    }

}
