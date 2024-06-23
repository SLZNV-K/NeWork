package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.domain.dto.AdditionalProp
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.AdapterDelegates.usersDelegate
import com.github.slznvk.nework.databinding.FragmentUsersBinding
import com.github.slznvk.nework.ui.NewEventFragment.Companion.FRAGMENT_EVENT
import com.github.slznvk.nework.ui.NewPostFragment.Companion.FRAGMENT_POST
import com.github.slznvk.nework.viewModel.EventViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import com.github.slznvk.nework.viewModel.UserViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UsersFragment : Fragment(R.layout.fragment_users) {
    private val binding by viewBinding(FragmentUsersBinding::bind)
    private val viewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isChooserScreen = arguments?.getBoolean(CHOOSER_SCREEN) ?: false
        val type = arguments?.getString(FRAGMENT_TYPE)
        val chooseUserMap = mutableMapOf<Long, AdditionalProp>()

        val adapter = ListDelegationAdapter(
            usersDelegate(
                itemClickedListener = { user ->
                    findNavController().navigate(
                        R.id.action_usersFragment_to_userDetailsFragment,
                        Bundle().apply {
                            putLong(USER_ID, user.id)
                            putString(USER_NAME, "${user.name} / ${user.login}")
                        })
                },
                isChoose = isChooserScreen,
                checkBoxClickListener = { user ->
                    if (chooseUserMap.containsKey(user.id)) {
                        chooseUserMap.remove(user.id)
                        viewModel.saveUser(user.copy(isChooser = false))
                    } else {
                        chooseUserMap[user.id] = AdditionalProp(user.name, user.avatar)
                        viewModel.saveUser(user.copy(isChooser = true))
                    }
                }
            )
        )

        viewModel.loadUsers()
        binding.apply {
            toolbar.isVisible = isChooserScreen
            recyclerUsers.adapter = adapter

            viewModel.dataState.observe(viewLifecycleOwner) {
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

            viewModel.data.observe(viewLifecycleOwner) {
                recyclerUsers.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter.items = it
            }
            saveUsersButton.setOnClickListener {
                when (type) {
                    FRAGMENT_POST -> postViewModel.addUsers(chooseUserMap)
                    FRAGMENT_EVENT -> eventViewModel.addUsers(chooseUserMap)
                    else -> {}
                }
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val FRAGMENT_TYPE = "FRAGMENT_TYPE"
        const val CHOOSER_SCREEN = "CHOOSER_SCREEN"
    }

    override fun onDestroy() {
        super.onDestroy()
        arguments?.clear()
    }
}