package com.github.slznvk.nework.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.slznvk.domain.dto.AttachmentType
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentPostDetailsBinding
import com.github.slznvk.nework.ui.PostsFeedFragment.Companion.POST_ID
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
class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding
    private val viewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var map: Map
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placeMarkMapObject: PlacemarkMapObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        map = binding.mapView.mapWindow.map

        val id = arguments?.getInt(POST_ID)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                repeat(3) {
                    id?.let { viewModel.getPostById(it) }
                    delay(16)
                }
            }

            viewModel.pickedPost.observe(viewLifecycleOwner) { post ->
                with(binding) {
                    author.text = post.author
                    (post.authorJob
                        ?: getString(R.string.looking_for_a_job)).also { authorJob.text = it }
                    published.text = post.published.formatDateTime()
                    avatar.load(post.authorAvatar, true)
                    content.text = post.content
                    likeButton.isChecked = post.likedByMe
                    likeButton.text = post.likeOwnerIds.first().toString()
                    usersButton.text = post.mentionIds.size.toString()

                    toolbar.setNavigationOnClickListener {
                        findNavController().navigateUp()
                    }

                    imageAttachment.visibility = View.GONE
                    videoAttachment.visibility = View.GONE
                    stubView.visibility = View.GONE

                    if (post.attachment != null) {
                        when (post.attachment?.type) {
                            AttachmentType.IMAGE -> {
                                imageAttachment.visibility = View.VISIBLE
                                imageAttachment.load(post.attachment!!.url)
                            }

                            AttachmentType.VIDEO -> {
                                videoAttachment.visibility = View.VISIBLE
                                videoAttachment.apply {
                                    setMediaController(MediaController(context))
                                    setVideoURI(Uri.parse(post.attachment?.url))
                                    setOnPreparedListener {
                                        start()
                                    }
                                    setOnCompletionListener {
                                        stopPlayback()
                                    }
                                }
                            }

                            AttachmentType.AUDIO -> {
                                stubView.inflate()
                            }

                            null -> error("Unknown attachment type: ${post.attachment?.type}")
                        }
                    }

                    if (post.users?.users?.isNotEmpty() == true) {
                        val usersToShow = min(post.users!!.users.size, 5)
                        user1.load(post.users!!.users[0].avatar, true)
                        for (i in 1 until usersToShow) {
                            val user = post.users!!.users[i]
                            usersList.addView(
                                createImageView(
                                    requireContext(),
                                    user.avatar
                                )
                            )
                        }
                        usersList.addView(createSeeMoreUsersButton(requireContext()))
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

        return binding.root
    }

    private fun createImageView(context: Context, imageUrl: String): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                40.dpToPixelsInt(context),
                40.dpToPixelsInt(context)
            ).apply {
                marginStart = (-8).dpToPixelsInt(context)
            }
            setPadding(
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context),
                2.dpToPixelsInt(context)
            )
            contentDescription = context.getString(R.string.description_user_s_avatar)
            load(imageUrl, true)
        }
    }

    private fun createSeeMoreUsersButton(context: Context): ImageButton {
        return ImageButton(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                40.dpToPixelsInt(context),
                40.dpToPixelsInt(context)
            ).apply {
                marginStart = (-8).dpToPixelsInt(context)
            }
            setPadding(
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context),
                16.dpToPixelsInt(context)
            )
            setBackgroundResource(R.drawable.circle_button_background)
            setImageResource(R.drawable.add_icon)
        }
    }

    private fun Int.dpToPixelsInt(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

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