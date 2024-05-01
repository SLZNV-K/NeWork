package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.slznvk.nework.adapter.formatDateTime
import com.github.slznvk.nework.databinding.FragmentPostDetailsBinding
import com.github.slznvk.nework.ui.PostsFeedFragment.Companion.ID
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.PostViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding
    private val viewModel: PostViewModel by viewModels()
    private lateinit var map: Map

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        map = binding.mapView.mapWindow.map

        val id = arguments?.getInt(ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                while (true) {
                    id?.let { viewModel.getPostById(it) }
                    delay(16)
                }
            }

            viewModel.pickedPost.observe(viewLifecycleOwner) { post ->
                with(binding) {
                    author.text = post.author
                    published.text = formatDateTime(post.published)
                    avatar.load(post.authorAvatar, true)
                    content.text = post.content
                    likeButton.isChecked = post.likedByMe
                    likeButton.text = post.likeOwnerIds.toString()


                    if (post.attachment != null) {
                        attachment.visibility = View.VISIBLE
                        attachment.load(post.attachment!!.url)
                    } else attachment.visibility = View.GONE

                    likeButton.setOnClickListener {
                        //TODO()
                    }
                    if (post.coords != null) {
                        mapView.visibility = View.VISIBLE
                        startLocation(Point(post.coords!!.lat, post.coords!!.long))
                    } else {
                        mapView.visibility = View.GONE
                    }
                }
            }
        }

        return binding.root

    }

    private fun startLocation(location: Point?) {
        val startLocation = location ?: return
        map.move(
            CameraPosition(
                startLocation,
                START_ZOOM,
                START_AZIMUTH,
                START_TILT
            )
        )
    }

    companion object {

        const val START_AZIMUTH = 0.0f
        const val START_TILT = 0.0f
        const val START_ZOOM = 17.0f
    }
}