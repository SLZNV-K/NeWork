package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.OnInteractionListener
import com.github.slznvk.nework.adapter.PostsAdapter
import com.github.slznvk.nework.databinding.FragmentPostsFeedBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostsFeedFragment : Fragment() {
    private lateinit var binding: FragmentPostsFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsFeedBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by activityViewModels()
        val authViewModel: AuthViewModel by activityViewModels()

        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(item: ListItem) {
                if (authViewModel.authenticated) {
                    viewModel.likeById(item as Post)
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
                viewModel.removePostById(item.id)
            }

            override fun onEdit(item: ListItem) {
                findNavController()
                    .navigate(
                        R.id.action_postsFeedFragment_to_newPostFragment,
                        Bundle().apply {
                            putString(POST_CONTENT, (item as Post).content)
                            putInt(POST_ID, item.id)
                        }
                    )
                viewModel.edit(item as Post)
            }

            override fun onItem(item: ListItem) {
                findNavController().navigate(
                    R.id.action_postsFeedFragment_to_postDetailsFragment,
                    Bundle().apply {
                        putInt(POST_ID, item.id)
                    })
            }
        }, observer = mediaObserver)

        binding.apply {
            recyclerPosts.adapter = adapter
            recyclerPosts.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            swiperefresh.setOnRefreshListener(adapter::refresh)

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
                        swiperefresh.isRefreshing =
                            state.refresh is LoadState.Loading
                    }
                }
            }

            addPostButton.setOnClickListener {
                if (authViewModel.authenticated) {
                    findNavController().navigate(R.id.action_postsFeedFragment_to_newPostFragment)
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
            return root
        }
    }

    companion object {
        const val POST_ID = "ID"
        const val POST_CONTENT = "CONTENT"
    }
}