package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.slznvk.nework.adapter.AdapterDelegates.eventsDelegate
import com.github.slznvk.nework.databinding.FragmentEventsFeedBinding
import com.github.slznvk.nework.viewModel.EventViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsFeedFragment : Fragment() {
    private lateinit var binding: FragmentEventsFeedBinding
    private val viewModel: EventViewModel by viewModels()
    private val adapter = ListDelegationAdapter(
        eventsDelegate()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventsFeedBinding.inflate(layoutInflater, container, false)

        viewModel.loadEvents()

        binding.apply {

            recyclerEvents.adapter = adapter

            viewModel.dataState.observe(viewLifecycleOwner){
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

            viewModel.data.observe(viewLifecycleOwner) {
                recyclerEvents.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter.items = it
            }


            return root
        }
    }
}