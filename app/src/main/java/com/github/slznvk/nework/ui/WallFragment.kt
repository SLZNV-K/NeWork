package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.domain.dto.ListItem
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.OnInteractionListener
import com.github.slznvk.nework.adapter.PostsAdapter
import com.github.slznvk.nework.databinding.FragmentWallBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WallFragment : Fragment(R.layout.fragment_wall) {
    private val binding by viewBinding(FragmentWallBinding::bind)
    private val authViewModel: AuthViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(USER_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                id?.let { postViewModel.loadUserWall(id) }
            }
        }

        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(item: ListItem) {
                if (authViewModel.authenticated) {
                    postViewModel.likeById(item as Post)
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
                postViewModel.removePostById(item.id)
            }

            override fun onEdit(item: ListItem) {
                findNavController()
                    .navigate(
                        R.id.action_userDetailsFragment_to_newPostFragment,
                        Bundle().apply {
                            putString(PostsFeedFragment.POST_CONTENT, (item as Post).content)
                            putLong(PostsFeedFragment.POST_ID, item.id)
                        }
                    )
                postViewModel.edit(item as Post)
            }

            override fun onItem(item: ListItem) {
                findNavController().navigate(
                    R.id.action_userDetailsFragment_to_postDetailsFragment,
                    Bundle().apply {
                        putLong(PostsFeedFragment.POST_ID, item.id)
                    })
            }
        }, observer = mediaObserver)

        binding.apply {
            recyclerWall.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerWall.adapter = adapter

        }

        lifecycleScope.launch {
            postViewModel.userWall?.collectLatest {
                adapter.submitData(it)
                println(it.toString())
            }
        }
    }

    companion object {
        fun newInstance() = WallFragment()
    }
}