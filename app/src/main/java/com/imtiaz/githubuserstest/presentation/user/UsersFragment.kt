package com.imtiaz.githubuserstest.presentation.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.*
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.databinding.FragmentUsersBinding
import com.imtiaz.githubuserstest.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var userAdapter: UsersAdapter
    private lateinit var _binding: FragmentUsersBinding

    private val users: MutableList<GithubUser> by lazy { mutableListOf() }

    private var isLoading = false
    private var searchJob: Job? = null

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

        collectUsersFromDb()
        observeFetchUsers()
        observeNetworkState()
    }

    /**
     * Initialize Ui elements here
     */
    private fun initUi() = _binding.apply {
        layoutAppBar.toolBar.setup(
            requireActivity(),
            title = getString(R.string.app_name),
            isNavIconEnable = false
        )
        initSearchTextChangeListener()
    }

    /**
     * Initializing search text listener and
     * picking search related text when user typing on Search field
     */
    private fun initSearchTextChangeListener() = _binding.apply {
        etSearch.addTextChangedListener(
            onTextChanged = { text, _, _, _->
                text?.let {
                    searchClear.isVisible = it.toString().isNotEmpty()
                    val text = it.toString().lowercase()
                    delayAndStartSearch(text)
                }
            }
        )
    }

    /**
     * Before start searching delay the process a bit
     * because of preventing search for continuous user typing.
     * After that Start search by checking on Search Text length.
     *
     * @param text - contains search text
     */
    private fun delayAndStartSearch(text: String) {
        searchJob?.cancel()
        if(text.length > 1){
            searchJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(200)

                val result = viewModel.searchUsers("%$text%")
                userAdapter.submitList(result)
            }
        }
        if(text.isEmpty()) {
            Log.e("isEmptyText", "True")
            userAdapter.apply {
                clearList()
                submitList(users)
            }
        }
    }

    private fun collectUsersFromDb() {
        lifecycleScope.launchWhenStarted {
            viewModel.getUsers().collect {
                users.apply {
                    clear()
                    addAll(it)
                    userAdapter.submitList(it)
                }
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
                        handleError(it.err)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initRecyclerView() = _binding.apply {
        recyclerview.apply {
            userAdapter = UsersAdapter(onItemClick(), onScrollUpdateData())
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter

            // paginating recyclerview
            PaginateRecyclerview(this, layoutManager) {
                val isSearchFieldEmpty = etSearch.text.isEmpty()
                if (!isLoading && isSearchFieldEmpty) {
                    isLoading = true
                    viewModel.startPaging()
                }
            }
        }
    }

    private fun handleLoadingState(isLoading: Boolean) = _binding.apply {
        this@UsersFragment.isLoading = isLoading

        // Hiding all Error view
        layoutError.parentLayout.isVisible = false
        layoutBottom.parentLayout.isVisible = false

        if (isLoading) {
            if (userAdapter.itemCount == 0) {
                // showing Initial Loader
                pbLoading.isVisible = isLoading
                recyclerview.isVisible = !isLoading
            } else {
                layoutBottom.apply {
                    // showing Paging Loader
                    parentLayout.isVisible = isLoading
                    pbLoading.isVisible = isLoading
                    textErrorMsg.isVisible = false
                }
            }
            return@apply
        }
        // Hiding Loader, Showing recyclerview because of [isLoading = false]
        pbLoading.isVisible = isLoading
        layoutBottom.parentLayout.isVisible = isLoading
        recyclerview.isVisible = !isLoading
        viewModel.errorHandler = null
    }

    private fun handleError(err: ErrorHandler) = _binding.apply {
        viewModel.errorHandler = err
        if (userAdapter.itemCount == 0) {
            // showing error view center of view
            layoutError.parentLayout.isVisible = true
            layoutError.textErrorMsg.text = err.msg
            return@apply
        }
        layoutBottom.apply {
            // showing error bottom of the view
            parentLayout.isVisible = true
            pbLoading.isVisible = false
            textErrorMsg.isVisible = true
            textErrorMsg.text = err.msg
        }
    }

    private fun observeNetworkState() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.networkStateFlow.collect { isNetworkAvailable ->
                val hasError = viewModel.errorHandler != null

                if (isNetworkAvailable && hasError) {
                    val isIOException = viewModel.errorHandler?.exception is IOException
                    val hasNoDataAvailable = userAdapter.itemCount == 0

                    // checking If we need to fetch first page otherwise fetch next page
                    if (isIOException && hasNoDataAvailable) {
                        viewModel.fetchUsers(FIRST_PAGE)
                        return@collect
                    }
                    else if(isIOException) viewModel.startPaging()
                }
            }
        }
    }

    private fun checkIfNeedInitialFetch() {
        if (userAdapter.itemCount == 0)
            viewModel.fetchUsers(FIRST_PAGE)
    }

    private fun onItemClick(): (GithubUser) -> Unit = {
        requireActivity().navigateTo(usersToProfile(it))
    }

    private fun onScrollUpdateData(): (Int) -> Unit = { viewModel.updateUsersData(it) }
}
