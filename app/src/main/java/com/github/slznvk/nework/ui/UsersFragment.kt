package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.AdapterDelegates.usersDelegate
import com.github.slznvk.nework.databinding.FragmentUsersBinding
import com.github.slznvk.nework.viewModel.UserViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding

    private val viewModel: UserViewModel by viewModels()

    private val adapter = ListDelegationAdapter(
        usersDelegate {
            findNavController().navigate(
                R.id.action_usersFragment_to_userDetailsFragment,
                Bundle().apply {
                    putLong(USER_ID, it.id)
                    putString(USER_NAME, "${it.name} / ${it.login}")
                })
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(layoutInflater, container, false)

        viewModel.loadUsers()

        binding.apply {
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

            return root
        }
    }

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
    }
}