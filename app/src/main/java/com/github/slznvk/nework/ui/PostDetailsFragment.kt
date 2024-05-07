package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.slznvk.nework.R
import com.github.slznvk.nework.adapter.formatDateTime
import com.github.slznvk.nework.databinding.FragmentPostDetailsBinding
import com.github.slznvk.nework.ui.PostsFeedFragment.Companion.ID
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding
    private val viewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var map: Map

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        map = binding.mapView.mapWindow.map

        MapKitFactory.initialize(requireContext())

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
                    (post.authorJob
                        ?: getString(R.string.looking_for_a_job)).also { authorJob.text = it }
                    published.text = formatDateTime(post.published)
                    avatar.load(post.authorAvatar, true)
                    content.text = post.content
                    likeButton.isChecked = post.likedByMe
                    likeButton.text = post.likeOwnerIds.size.toString()
                    usersButton.text = post.mentionIds.size.toString()

                    if (post.attachment != null) {
                        attachment.visibility = View.VISIBLE
                        attachment.load(post.attachment!!.url)
                    } else attachment.visibility = View.GONE

                    likeButton.setOnClickListener {
                        if (authViewModel.authenticated) {
                            viewModel.likeById(post)
                        } else {
                            AlertDialog.Builder(requireActivity()).apply {
                                setTitle(getString(R.string.sign_in))
                                setMessage(getString(R.string.to_interact_with_posts_you_need_to_log_in))
                                setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                                    findNavController().navigate(R.id.loginFragment)
                                }
                                setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                                setCancelable(true)
                            }.create().show()
                        }
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

    private fun startLocation(location: Point) {
        map.move(
            CameraPosition(
                location,
                START_ZOOM,
                START_AZIMUTH,
                START_TILT
            )
        )
        setMarkerInLocation(location)
    }

    private fun setMarkerInLocation(location: Point) {
        val mapObjects = map.mapObjects
        val placeMark: PlacemarkMapObject = mapObjects.addPlacemark(location)
        placeMark.setIcon(
            ImageProvider.fromResource(
                requireContext(),
                R.drawable.location_icon
            )
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    companion object {

        const val START_AZIMUTH = 0.0f
        const val START_TILT = 0.0f
        const val START_ZOOM = 17.0f
    }
}