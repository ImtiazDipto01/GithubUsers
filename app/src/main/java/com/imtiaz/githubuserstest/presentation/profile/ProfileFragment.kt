package com.imtiaz.githubuserstest.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.setup
import com.imtiaz.githubuserstest.databinding.FragmentProfileBinding
import com.imtiaz.githubuserstest.presentation.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

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
    }

    private fun collectProfileInfo() {

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
}