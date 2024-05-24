package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.slznvk.nework.adapter.AdapterDelegates.jobsDelegate
import com.github.slznvk.nework.databinding.FragmentJobsBinding
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.viewModel.UserViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JobsFragment : Fragment() {
    private lateinit var binding: FragmentJobsBinding

    private val viewModel: UserViewModel by viewModels()
//    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(layoutInflater, container, false)

        val adapter = ListDelegationAdapter(
            jobsDelegate {
                viewModel.deleteJodById(it.id)
            }//authViewModel.authenticated)
        )

//        TODO: получить верный id
        val id = arguments?.getLong(USER_ID) ?: 1 //authViewModel.data.value.id

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                repeat(2) {
                    viewModel.getUserJobs(id)
                    delay(16)
                }
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
//            TODO: определить чей профиль смотрим (свой/чужой)
//            addJobButton.isVisible = authViewModel.authenticated
            addJobButton.setOnClickListener {
//                TODO: добавление работы
            }

            return root
        }
    }

    companion object {
        fun newInstance() = JobsFragment()
    }
}