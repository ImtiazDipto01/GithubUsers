package com.imtiaz.githubuserstest.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
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
import com.imtiaz.githubuserstest.presentation.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException


@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var userAdapter: UsersAdapter
    private lateinit var _binding: FragmentUsersBinding

    private var isLoading = false
    private var searchJob: Job? = null
    private var isNightMode = false

    private val users: MutableList<GithubUser> by lazy { mutableListOf() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.getUsers()
            viewModel.userListFlow.collect {  }
        }
    }

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
            isNavIconEnable = false,
            menu = R.menu.menu_profile
        )
        initSearchTextChangeListener()
        setToolbarMenuClickListener()

        if (viewModel.searchText.isNotEmpty()) etSearch.setText(viewModel.searchText)
        searchClear.setOnClickListener { etSearch.setText("") }
    }

    private fun setToolbarMenuClickListener() {
        _binding.apply {
            layoutAppBar.toolBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_change_mode -> {
                        isNightMode = !isNightMode

                        if(isNightMode)
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        true
                    }
                    else -> false
                }
            }
        }
    }

    /**
     * Initializing search text listener and
     * picking search related text when user typing on Search field
     */
    private fun initSearchTextChangeListener() = _binding.apply {
        etSearch.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                text?.let {
                    searchClear.isVisible = it.toString().isNotEmpty()
                    viewModel.searchText = it.toString()
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
        // canceling previous job so that new started coroutine job
        // don't get overlap with previous job
        searchJob?.cancel()

        if (text.length > 1) {
            // if search text contains at least 2 characters then we'll start processing the search
            searchJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(200)

                val result = viewModel.searchUsers("%$text%")
                userAdapter.submitList(result)
            }
        }
        if (text.isEmpty()) {
            // if search text is empty we're clearing the full list
            // showing previous full users list
            userAdapter.apply {
                clearList()
                submitList(users)
            }
        }
    }

    /**
     * here we're collecting the full cached user list from db
     * and updating our UI. this is our only data source for showing users in recyclerview
     *
     */
    private fun collectUsersFromDb() {
        lifecycleScope.launchWhenStarted {
            viewModel.userListFlow.collectLatest { list ->
                list?.let {
                    users.apply {
                        clear()
                        addAll(it)
                        userAdapter.submitList(it)
                    }
                    checkIfNeedInitialFetch()
                }
            }
        }
    }

    /**
     * This function only observe the loading and error state
     * when we're fetching user list from API
     *
     */
    private fun observeFetchUsers() {
        lifecycleScope.launchWhenStarted {
            viewModel.fetchUsersFlow.collect {
                when (it) {
                    is BaseState.Loading -> handleLoadingState(true)
                    is BaseState.Success -> handleLoadingState(false)
                    is BaseState.Error -> {
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

        Log.d("checkCall", "handleLoadingState")

        // Hiding all Error view
        layoutError.parentLayout.isVisible = false
        layoutBottom.parentLayout.isVisible = false

        if (isLoading) {
            if (users.size == 0) {
                // showing Initial Loader
                handleInitialLoading(isLoading)
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
        handleInitialLoading(isLoading)
        layoutBottom.parentLayout.isVisible = isLoading
        recyclerview.isVisible = !isLoading
        viewModel.errorHandler = null
    }

    private fun handleError(err: ErrorHandler) = _binding.apply {
        viewModel.errorHandler = err
        if (users.size == 0) {
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
                    val hasNoDataAvailable = users.size == 0

                    // checking If we need to fetch first page otherwise fetch next page
                    if (isIOException && hasNoDataAvailable) {
                        viewModel.fetchUsers(FIRST_PAGE)
                        return@collect
                    } else if (isIOException) viewModel.startPaging()
                }
            }
        }
    }

    /**
     * This function will start show & hide the skeletons only for initial loading.
     *
     * @param isLoading -> return [true] if intial loading started otherwise [false]
     */
    private fun handleInitialLoading(isLoading: Boolean) = _binding.apply {
        layoutSkeleton.parentLayout.isVisible = isLoading
        if (isLoading)
            layoutSkeleton.shimmerViewContainer.startShimmer()
        else layoutSkeleton.shimmerViewContainer.stopShimmer()
    }

    /**
     * If there is no any data in user adapter that means,
     * we need to fetch the first page data.
     *
     */
    private fun checkIfNeedInitialFetch() {
        Log.d("checkCall", "checkIfNeedInitialFetch")
        if (users.size == 0)
            viewModel.fetchUsers(FIRST_PAGE)
    }

    /**
     * when App user click any Github users from user list
     * this lambda function will invoke and will start profile activity
     *
     * @return Unit
     */
    private fun onItemClick(): (GithubUser) -> Unit = {
        //requireActivity().navigateTo(usersToProfile(it))

        // here we're starting Profile Activity, because
        // this is only activity written in compose, otherwise will launch a fragment

        val isDarkTheme = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        Intent(requireActivity(), ProfileActivity::class.java).apply {
            putExtra(CURRENT_USER, it)
            putExtra(APP_THEME, isDarkTheme)
            startActivity(this)
        }
    }

    /**
     * on user list scrolling, In parallel we'll fetch updated user info
     * this lambda function will invoke when a new page(SinceId) will found that
     * we need to fetch updated user info for that page.
     *
     * @return
     */
    private fun onScrollUpdateData(): (Int) -> Unit = { viewModel.updateUsersData(it) }
}
