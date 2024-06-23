package com.github.slznvk.nework.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentMapBinding
import com.github.slznvk.nework.ui.NewEventFragment.Companion.FRAGMENT_EVENT
import com.github.slznvk.nework.ui.NewPostFragment.Companion.FRAGMENT_POST
import com.github.slznvk.nework.ui.UsersFragment.Companion.FRAGMENT_TYPE
import com.github.slznvk.nework.viewModel.EventViewModel
import com.github.slznvk.nework.viewModel.PostViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {
    private val binding by viewBinding(FragmentMapBinding::bind)
    private lateinit var map: Map
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placeMarkMapObject: PlacemarkMapObject
    private val postViewModel: PostViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    private val mapObjectTapListener = MapObjectTapListener { _, point ->
        arguments?.putDouble(LATITUDE, point.latitude)
        arguments?.putDouble(LONGITUDE, point.longitude)
        Toast.makeText(
            requireContext(),
            "LATITUDE: ${point.latitude}, LONGITUDE: ${point.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    private val geoObjectTapListener =
        GeoObjectTapListener { geoObjectTapEvent ->
            val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            binding.mapView.mapWindow.map.selectGeoObject(
                selectionMetadata,
            )
            val point = geoObjectTapEvent.geoObject.geometry[0].point
            setMarkerInLocation(point)
            false
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = binding.mapView.mapWindow.map
        val mapKit = MapKitFactory.getInstance()
        val userLocationLayer = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        startLocation(userLocationLayer.cameraPosition()?.target)

        map.addTapListener(geoObjectTapListener)

        val type = arguments?.getString(FRAGMENT_TYPE)

        binding.savePointButton.setOnClickListener {
            val long = arguments?.getDouble(LONGITUDE)
            val lat = arguments?.getDouble(LATITUDE)
            if (long != null && lat != null) {
                when (type) {
                    FRAGMENT_POST -> postViewModel.addPoint(lat, long)
                    FRAGMENT_EVENT -> eventViewModel.addPoint(lat, long)
                    else -> {}
                }
            }

            findNavController().navigateUp()
        }
    }

    private fun startLocation(location: Point?) {
        val startLocation = location ?: Point(55.7520233, 37.6174994)
        map.move(
            CameraPosition(
                startLocation,
                START_ZOOM,
                START_AZIMUTH,
                START_TILT
            )
        )
    }

    private fun setMarkerInLocation(location: Point?) {
        val marker = createBitmapFromVector(R.drawable.location_icon)
        if (location != null) {
            mapObjectCollection =
                binding.mapView.mapWindow.map.mapObjects
            placeMarkMapObject = mapObjectCollection.addPlacemark(
                location,
                ImageProvider.fromBitmap(marker)
            )
            placeMarkMapObject.opacity = 0.5f
            placeMarkMapObject.addTapListener(mapObjectTapListener)
        }
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
        arguments?.clear()
    }


    companion object {

        const val START_AZIMUTH = 0.0f
        const val START_TILT = 0.0f
        const val START_ZOOM = 17.0f

        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
    }
}