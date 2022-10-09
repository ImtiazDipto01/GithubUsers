package com.imtiaz.githubuserstest.presentation.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.*
import com.imtiaz.githubuserstest.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    private lateinit var userAdapter: UsersAdapter
    private lateinit var _binding: FragmentUsersBinding

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initRecyclerView()
        observeFetchUsers()
        collectUsersFromDb()
    }

    private fun initUi() = _binding.apply {
        layoutAppBar.toolBar.setup(
            requireActivity(),
            title = getString(R.string.app_name),
            isNavIconEnable = false
        )
    }

    private fun collectUsersFromDb() {
        lifecycleScope.launchWhenStarted {
            viewModel.getUsers().collect {
                userAdapter.submitList(it)
                checkIfNeedInitialFetch()
            }
        }
    }

    private fun observeFetchUsers() {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchUsersFlow.collect {
                when (it) {
                    is State.Loading -> handleLoadingState(true)
                    is State.Success -> handleLoadingState(false)
                    is State.Error -> {
                        handleLoadingState(false)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerView() = _binding.apply {
        recyclerview.apply {
            userAdapter = UsersAdapter { requireActivity().navigateTo(usersToProfile(it)) }
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
            PaginateRecyclerview(this, layoutManager) {
                if(!isLoading){
                    isLoading = true
                    viewModel.startPaging()
                }
            }
        }
    }

    private fun handleLoadingState(isLoading: Boolean) = _binding.apply {
        this@UsersFragment.isLoading = isLoading

        if(isLoading){
            if(userAdapter.itemCount == 0){
                pbLoading.isVisible = isLoading
                recyclerview.isVisible = !isLoading
            }
            else {
                loadingBottom.parentLayout.isVisible = isLoading
            }
            return@apply
        }
        pbLoading.isVisible = isLoading
        loadingBottom.parentLayout.isVisible = isLoading
        recyclerview.isVisible = !isLoading
    }

    private fun checkIfNeedInitialFetch() {
        if (userAdapter.itemCount == 0)
            viewModel.fetchUsers(FIRST_PAGE)
    }
}
