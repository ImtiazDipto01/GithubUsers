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
import com.imtiaz.githubuserstest.core.extensions.Resource
import com.imtiaz.githubuserstest.core.extensions.navigateTo
import com.imtiaz.githubuserstest.core.extensions.setup
import com.imtiaz.githubuserstest.core.extensions.usersToProfile
import com.imtiaz.githubuserstest.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    private lateinit var userAdapter: UsersAdapter
    private lateinit var _binding: FragmentUsersBinding

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
                Log.d("isListEmpty", it.isEmpty().toString())
            }
        }
    }

    private fun observeFetchUsers() {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchUsersFlow.collect {
                when (it) {
                    is Resource.Loading -> {
                        _binding.pbLoading.isVisible = true
                    }
                    is Resource.Success -> {
                        /*//Timber.e("UserFragment: ${it.data}")
                        _binding.apply {
                            pbLoading.isVisible = false
                            recyclerview.isVisible = true
                            userAdapter.submitList(it.data)
                        }*/
                        _binding.pbLoading.isVisible = false
                        viewModel.updateLastPage()
                    }
                    is Resource.Error -> {
                        _binding.pbLoading.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerView() = _binding.apply {
        recyclerview.apply {
            userAdapter = UsersAdapter {
                requireActivity().navigateTo(usersToProfile(it))
            }
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
        viewModel.fetchUsers()
    }
}