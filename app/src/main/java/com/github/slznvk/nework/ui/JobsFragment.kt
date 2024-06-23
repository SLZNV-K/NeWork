package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.AdapterDelegates.jobsDelegate
import com.github.slznvk.nework.databinding.FragmentJobsBinding
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.UserViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobsFragment : Fragment(R.layout.fragment_jobs) {
    private val binding by viewBinding(FragmentJobsBinding::bind)
    private val viewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(USER_ID) ?: authViewModel.data.value.id

        val adapter = ListDelegationAdapter(
            jobsDelegate(
                itemClickedListener = { viewModel.deleteJodById(id) },
                isAuthorized = authViewModel.data.value.id == id
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getUserJobs(id)
            }
        }

        binding.apply {

            recyclerJobs.adapter = adapter

            viewModel.jobsState.observe(viewLifecycleOwner) {
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

            viewModel.jobs.observe(viewLifecycleOwner) {
                recyclerJobs.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter.items = it
            }

            addJobButton.isVisible = authViewModel.data.value.id == id
            addJobButton.setOnClickListener {
                findNavController().navigate(R.id.action_userDetailsFragment_to_newJobFragment)
            }
        }
    }

    companion object {
        fun newInstance() = JobsFragment()
    }
}