package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.EventsAdapter
import com.github.slznvk.nework.adapter.OnInteractionListener
import com.github.slznvk.nework.databinding.FragmentEventsFeedBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventsFeedFragment : Fragment(R.layout.fragment_events_feed) {

    private val binding by viewBinding(FragmentEventsFeedBinding::bind)
    private val viewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)

        val adapter = EventsAdapter(object : OnInteractionListener {
            override fun onLike(item: ListItem) {
                if (authViewModel.authenticated) {
                    viewModel.likeEventById(item as Event)
                } else {
                    AlertDialog.Builder(requireActivity()).apply {
                        setTitle(getString(R.string.sign_in))
                        setMessage(getString(R.string.to_interact_with_publications_you_need_to_log_in))
                        setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                            findNavController().navigate(R.id.loginFragment)
                        }
                        setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                }
            }

            override fun onRemove(item: ListItem) {
                viewModel.removeEventById(item.id)
            }

            override fun onEdit(item: ListItem) {
                findNavController()
                    .navigate(
                        R.id.action_eventsFeedFragment_to_newEventFragment,
                        Bundle().apply {
                            putString(EVENT_CONTENT, (item as Event).content)
                            putLong(EVENT_ID, item.id)
                        }
                    )
                viewModel.edit(item as Event)
            }

            override fun onItem(item: ListItem) {
                findNavController().navigate(R.id.action_eventsFeedFragment_to_eventDetailsFragment,
                    Bundle().apply {
                        putLong(EVENT_ID, item.id)
                    })
            }

        }, observer = mediaObserver)

        with(binding) {

            recyclerEvents.adapter = adapter
            recyclerEvents.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            swipeRefresh.setOnRefreshListener(adapter::refresh)

            viewModel.dataState.observe(viewLifecycleOwner) {
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

            lifecycleScope.launch {
                viewModel.data.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    adapter.loadStateFlow.collectLatest { state ->
                        swipeRefresh.isRefreshing =
                            state.refresh is LoadState.Loading
                    }
                }
            }

            addEventButton.setOnClickListener {
                findNavController().navigate(R.id.action_eventsFeedFragment_to_newEventFragment)
            }
        }
    }

    companion object {
        const val EVENT_ID = "EVENT_ID"
        const val EVENT_CONTENT = "EVENT_CONTENT"
    }
}