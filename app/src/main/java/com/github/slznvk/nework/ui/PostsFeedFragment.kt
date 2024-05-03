package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.OnInteractionListener
import com.github.slznvk.nework.adapter.PostsAdapter
import com.github.slznvk.nework.databinding.FragmentPostsFeedBinding
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

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(item: ListItem) {
                viewModel.likeById(item as Post)
            }

            override fun onRemove(item: ListItem) {
                TODO("Not yet implemented")
            }

            override fun onEdit(item: ListItem) {
                TODO("Not yet implemented")
            }

            override fun onItem(item: ListItem) {
                findNavController().navigate(
                    R.id.action_postsFeedFragment_to_postDetailsFragment,
                    Bundle().apply {
                        putInt(ID, (item as Post).id)
                    })
            }
        })

        binding.apply {
            recyclerPosts.adapter = adapter
            recyclerPosts.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.dataState.observe(viewLifecycleOwner) {
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

//            viewLifecycleOwner.lifecycleScope.launch {
//                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    println("Coroutine started")
//                    viewModel.data.collectLatest {pagingData ->
//                        println("Coroutine started DATA")
//                        adapter.submitData(pagingData)
//                    }
//                }
//            }
            lifecycleScope.launch {
                viewModel.data.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }

            addPostButton.setOnClickListener {
                findNavController().navigate(R.id.action_postsFeedFragment_to_newPostFragment)
            }

            return root
        }
    }

    companion object {
        const val ID = "ID"
    }
}