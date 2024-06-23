package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.domain.dto.AttachmentType
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentPostDetailsBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.ui.PostsFeedFragment.Companion.POST_ID
import com.github.slznvk.nework.utills.ViewExtension.createImageView
import com.github.slznvk.nework.utills.ViewExtension.createSeeMoreUsersButton
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@AndroidEntryPoint
class PostDetailsFragment : Fragment(R.layout.fragment_post_details) {

    private val binding by viewBinding(FragmentPostDetailsBinding::bind)
    private val viewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var map: Map
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placeMarkMapObject: PlacemarkMapObject

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)

        map = binding.mapView.mapWindow.map

        val id = arguments?.getLong(POST_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                id?.let { viewModel.getPostById(it) }
            }

            viewModel.pickedPost.observe(viewLifecycleOwner) { post ->
                with(binding) {
                    author.text = post.author
                    (post.authorJob
                        ?: getString(R.string.looking_for_a_job)).also { authorJob.text = it }
                    published.text = post.published.formatDateTime()
                    avatar.load(post.authorAvatar, true)
                    content.text = post.content
                    content.isVisible = post.content != ""
                    likeButton.isChecked = post.likedByMe
                    likeButton.text = post.likeOwnerIds.size.toString()
                    usersButton.text = post.mentionIds.size.toString()

                    toolbar.setNavigationOnClickListener {
                        findNavController().navigateUp()
                    }

                    imageAttachment.visibility = View.GONE
                    videoAttachment.visibility = View.GONE
                    audioAttachment.visibility = View.GONE

                    if (post.attachment != null) {
                        when (post.attachment?.type) {
                            AttachmentType.IMAGE -> imageAttachment.apply {
                                visibility = View.VISIBLE
                                load(post.attachment?.url)
                            }

                            AttachmentType.VIDEO -> videoAttachment.apply {
                                visibility = View.VISIBLE
                                setMediaController(MediaController(context))
                                setVideoURI(Uri.parse(post.attachment?.url))
                                setOnPreparedListener {
                                    start()
                                }
                                setOnCompletionListener {
                                    stopPlayback()
                                }
                            }

                            AttachmentType.AUDIO -> {
                                audioAttachment.visibility = View.VISIBLE

                                playPauseButton.setOnClickListener {
                                    mediaObserver.playSong(post.attachment!!.url, post.songPlaying)
                                    post.songPlaying = !post.songPlaying
                                    playPauseButton.setImageResource(
                                        if (post.songPlaying) {
                                            R.drawable.pause_icon
                                        } else {
                                            R.drawable.play_icon
                                        }
                                    )
                                }
                            }

                            else -> error("Unknown attachment type: ${post.attachment?.type}")
                        }
                    }

                    if (post.users.isNotEmpty()) {
                        val usersToShow = min(post.users.size, 5) - 1
                        val subMap = post.users.toList().take(usersToShow)
                        subMap.forEach {
                            mentionedList.addView(
                                createImageView(
                                    requireContext(),
                                    it.second.avatar
                                )
                            )
                        }
                        val lastUser = post.users.toList().drop(usersToShow).take(1)
                        mentionedList.addView(
                            createImageView(
                                requireContext(),
                                lastUser[0].second.avatar,
                                false
                            )
                        )
                        if (post.users.size > 5) {
                            mentionedList.addView(createSeeMoreUsersButton(requireContext()))
                        }
                    }

                    likeButton.setOnClickListener {
                        if (authViewModel.authenticated) {
                            viewModel.likeById(post)
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
                    if (post.coords != null) {
                        mapView.visibility = View.VISIBLE
                        startLocation(Point(post.coords!!.lat, post.coords!!.long))
                    } else {
                        mapView.visibility = View.GONE
                    }
                }
            }
        }
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
        val marker = createBitmapFromVector(R.drawable.location_icon)
        mapObjectCollection =
            binding.mapView.mapWindow.map.mapObjects
        placeMarkMapObject = mapObjectCollection.addPlacemark(
            location,
            ImageProvider.fromBitmap(marker)
        )
        placeMarkMapObject.opacity = 0.5f
    }

    private fun createBitmapFromVector(art: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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


    override fun onResume() {
        super.onResume()
        val mainActivity = activity as? MainActivity
        mainActivity?.let { activity ->
            with(activity.binding) {
                bottomNavigationView.visibility = View.GONE
                toolbar.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val mainActivity = activity as? MainActivity
        mainActivity?.let { activity ->
            with(activity.binding) {
                bottomNavigationView.visibility = View.VISIBLE
                toolbar.visibility = View.VISIBLE
            }
        }
    }

    companion object {

        const val START_AZIMUTH = 0.0f
        const val START_TILT = 0.0f
        const val START_ZOOM = 17.0f
    }
}