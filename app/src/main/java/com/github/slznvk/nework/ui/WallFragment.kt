package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.slznvk.nework.databinding.FragmentWallBinding
import com.github.slznvk.nework.ui.UsersFragment.Companion.USER_ID
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WallFragment : Fragment() {
    private lateinit var binding: FragmentWallBinding
    private val authViewModel: AuthViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWallBinding.inflate(layoutInflater, container, false)

        val id = arguments?.getInt(USER_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                repeat(2) {
                    id?.let { postViewModel.loadUserWall(id) }
                    delay(16)
                }
            }
        }
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
                        R.id.action_postsFeedFragment_to_newPostFragment,
                        Bundle().apply {
                            putString(PostsFeedFragment.POST_CONTENT, (item as Post).content)
                            putInt(PostsFeedFragment.POST_ID, item.id)
                        }
                    )
                postViewModel.edit(item as Post)
            }

            override fun onItem(item: ListItem) {
                findNavController().navigate(
                    R.id.action_postsFeedFragment_to_postDetailsFragment,
                    Bundle().apply {
                        putInt(PostsFeedFragment.POST_ID, item.id)
                    })
            }
        })
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
        return binding.root
    }

    companion object {
        fun newInstance() = WallFragment()
    }
}