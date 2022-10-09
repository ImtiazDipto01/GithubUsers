package com.imtiaz.githubuserstest.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.State
import com.imtiaz.githubuserstest.core.extensions.loadImage
import com.imtiaz.githubuserstest.core.extensions.setup
import com.imtiaz.githubuserstest.databinding.FragmentProfileBinding
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()
    val args: ProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        collectProfileInfo()
        viewModel.getUserProfile(args.user.login ?: "")
    }

    private fun collectProfileInfo() {
        lifecycleScope.launchWhenStarted {
            viewModel.profileFlow.collect {
                when (it) {
                    is State.Loading -> {
                        //_binding.pbLoading.isVisible = true
                    }
                    is State.Success -> {
                        Timber.e("ProfileFragment: ${it.data}")
                        updateProfileInfo(it.data)
                    }
                    is State.Error -> {
                        //_binding.pbLoading.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initUi() {
        _binding.apply {
            layoutAppBar.toolBar.setup(requireActivity(), getString(R.string.user_profile))
            layoutUserRepoInfo.apply {
                textLeftTitle.text = getString(R.string.text_public_repo)
                textRightTitle.text = getString(R.string.text_public_gist)
                /*textLeftTitleCount.text = "110"
                textRightTitleCount.text = "20"*/
            }
            layoutUserFollowerInfo.apply {
                textLeftTitle.text = getString(R.string.text_followers)
                textRightTitle.text = getString(R.string.text_followings)
                /*textLeftTitleCount.text = "110"
                textRightTitleCount.text = "20"*/
            }
        }
    }

    private fun updateProfileInfo(user: GithubUser) = _binding.apply {
        user.apply {
            imgUser.loadImage(user.avatarUrl)
            textUserLogin.text = login ?: ""
            textUserName.text = name ?: getString(R.string.name_not_found)
            textUserLocation.text = location ?: getString(R.string.location_not_found)
            layoutUserRepoInfo.apply {
                textLeftTitleCount.text = publicRepos.toString()
                textRightTitleCount.text = publicGists.toString()
            }
            layoutUserFollowerInfo.apply {
                textLeftTitleCount.text = followers.toString()
                textRightTitleCount.text = following.toString()
            }
        }
    }
}