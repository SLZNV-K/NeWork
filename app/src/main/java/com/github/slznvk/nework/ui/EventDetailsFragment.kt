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
import com.github.slznvk.nework.databinding.FragmentEventDetailsBinding
import com.github.slznvk.nework.observer.MediaLifecycleObserver
import com.github.slznvk.nework.ui.EventsFeedFragment.Companion.EVENT_ID
import com.github.slznvk.nework.utills.ViewExtension.createImageView
import com.github.slznvk.nework.utills.ViewExtension.createSeeMoreUsersButton
import com.github.slznvk.nework.utills.formatDateTime
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.github.slznvk.nework.viewModel.EventViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.min


@AndroidEntryPoint
class EventDetailsFragment : Fragment(R.layout.fragment_event_details) {
    private val binding by viewBinding(FragmentEventDetailsBinding::bind)
    private lateinit var map: Map
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placeMarkMapObject: PlacemarkMapObject
    private val viewModel: EventViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaObserver = MediaLifecycleObserver()
        lifecycle.addObserver(mediaObserver)
        map = binding.mapView.mapWindow.map

        val id = arguments?.getLong(EVENT_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                id?.let { viewModel.getEventById(it) }
            }

            viewModel.pickedEvent.observe(viewLifecycleOwner) { event ->
                with(binding) {
                    author.text = event.author
                    (event.authorJob
                        ?: getString(R.string.looking_for_a_job)).also { authorJob.text = it }
                    published.text = event.published.formatDateTime()
                    avatar.load(event.authorAvatar, true)
                    content.text = event.content
                    content.isVisible = event.content != ""
                    likeButton.isChecked = event.likedByMe
                    likeButton.text = event.likeOwnerIds.size.toString()
                    usersButton.text = event.participantsIds.size.toString()
                    eventType.text = event.type.value

                    imageAttachment.visibility = View.GONE
                    videoAttachment.visibility = View.GONE
                    audioAttachment.visibility = View.GONE

                    if (event.attachment != null) {
                        when (event.attachment?.type) {
                            AttachmentType.IMAGE -> imageAttachment.apply {
                                visibility = View.VISIBLE
                                load(event.attachment?.url)
                            }

                            AttachmentType.VIDEO -> videoAttachment.apply {
                                visibility = View.VISIBLE
                                setMediaController(MediaController(context))
                                setVideoURI(Uri.parse(event.attachment?.url))
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
                                    mediaObserver.playSong(
                                        event.attachment!!.url,
                                        event.songPlaying
                                    )
                                    event.songPlaying = !event.songPlaying
                                    playPauseButton.setImageResource(
                                        if (event.songPlaying) {
                                            R.drawable.pause_icon
                                        } else {
                                            R.drawable.play_icon
                                        }
                                    )
                                }
                            }

                            else -> error("Unknown attachment type: ${event.attachment?.type}")
                        }
                    }
                    if (event.users.isNotEmpty()) {
                        val usersToShow = min(event.users.size, 5) - 1
                        val subMap = event.users.toList().take(usersToShow)
                        subMap.forEach {
                            mentionedList.addView(
                                createImageView(
                                    requireContext(),
                                    it.second.avatar
                                )
                            )
                        }
                        val lastUser = event.users.toList().drop(usersToShow).take(1)
                        mentionedList.addView(
                            createImageView(
                                requireContext(),
                                lastUser[0].second.avatar,
                                false
                            )
                        )
                        if (event.users.size > 5) {
                            mentionedList.addView(createSeeMoreUsersButton(requireContext()))
                        }
                    }

                    likeButton.setOnClickListener {
                        if (authViewModel.authenticated) {
                            viewModel.likeEventById(event)
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
                    if (event.coords != null) {
                        mapView.visibility = View.VISIBLE
                        startLocation(Point(event.coords!!.lat, event.coords!!.long))
                    } else {
                        mapView.visibility = View.GONE
                    }

                    toolbar.setNavigationOnClickListener {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun startLocation(location: Point) {
        map.move(
            CameraPosition(
                location,
                PostDetailsFragment.START_ZOOM,
                PostDetailsFragment.START_AZIMUTH,
                PostDetailsFragment.START_TILT
            )
        )
        setMarkerInLocation(location)
    }

    private fun setMarkerInLocation(location: Point) {
        val marker = createBitmapFromVector(R.drawable.location_icon)
        mapObjectCollection =
            binding.mapView.mapWindow.map.mapObjects
        placeMarkMapObject = mapObjectCollection.addPlacemark().apply {
            geometry = location
            setIcon(ImageProvider.fromBitmap(marker))
        }
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
}