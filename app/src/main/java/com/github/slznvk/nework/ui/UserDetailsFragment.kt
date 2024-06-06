package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.ViewPagerAdapter
import com.github.slznvk.nework.auth.AppAuth
import com.github.slznvk.nework.databinding.FragmentUserDetailsBinding
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_NAME
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailsBinding

    private val viewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater, container, false)

        val id = arguments?.getLong(USER_ID) ?: authViewModel.data.value.id
        val name = arguments?.getString(USER_NAME) ?: "You"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(16)
                viewModel.getUserById(id)
                viewModel.getUserJobs(id)
            }
        }

        val adapter = ViewPagerAdapter(id, this)

        binding.apply {
            viewModel.pickedUser.observe(viewLifecycleOwner) { user ->
                profileImage.load(user.avatar)
            }
            pager2.adapter = adapter
            TabLayoutMediator(tabsLayout, pager2) { tab, pos ->
                tab.text = when (pos) {
                    0 -> "Wall"
                    else -> "Jobs"
                }
            }.attach()

            toolbar.title = name
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            signOutButton.isVisible = name == "You"
            signOutButton.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.sign_out))
                    setMessage("Are you sure you want to log out of your account?")
                    setPositiveButton(getString(R.string.get_out_anyway)) { _, _ ->
                        appAuth.removeAuth()
                    }
                    setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    }
                    setCancelable(true)
                }.create().show()
            }

            return root
        }
    }

    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        mainActivity?.let { activity ->
            with(activity.binding) {
                bottomNavigationView.visibility = View.GONE
                toolbar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val mainActivity = activity as? MainActivity
        mainActivity?.let { activity ->
            with(activity.binding) {
                bottomNavigationView.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
            }
        }
    }
}