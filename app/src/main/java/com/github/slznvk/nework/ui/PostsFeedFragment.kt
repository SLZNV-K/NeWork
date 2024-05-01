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
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.AdapterDelegates.postsDelegate
import com.github.slznvk.nework.adapter.OnInteractionListener
import com.github.slznvk.nework.databinding.FragmentPostsFeedBinding
import com.github.slznvk.nework.viewModel.PostViewModel
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostsFeedFragment : Fragment() {

    private lateinit var binding: FragmentPostsFeedBinding
    private val adapter = ListDelegationAdapter(
        postsDelegate(object : OnInteractionListener {
            override fun onLike(item: ListItem) {
                TODO("Not yet implemented")
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
    )
    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsFeedBinding.inflate(layoutInflater, container, false)
        viewModel.loadPosts()
        binding.apply {
            recyclerPosts.adapter = adapter

            viewModel.dataState.observe(viewLifecycleOwner) {
                errorText.isVisible = it.error
                retryButton.isVisible = it.error
            }

            viewModel.data.observe(viewLifecycleOwner) {
                recyclerPosts.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter.items = it
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