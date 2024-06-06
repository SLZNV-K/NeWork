package com.github.slznvk.nework.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentNewEventBinding
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.viewModel.EventViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private lateinit var binding: FragmentNewEventBinding
    private lateinit var viewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewEventBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                        return@registerForActivityResult
                    }

                    Activity.RESULT_OK -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.savePhoto(PhotoModel(uri, uri.toFile()))
                    }
                }
            }


        binding.apply {

            viewModel.photo.observe(viewLifecycleOwner) {
                if (it == null) {
                    attachmentFrame.visibility = View.GONE
                    return@observe
                }
                attachmentFrame.visibility = View.VISIBLE
                imageAttachment.setImageURI(it.uri)
            }

            removeButton.setOnClickListener {
                viewModel.clear()
            }

            takePhotoButton.setOnClickListener {
                ImagePicker.with(this@NewEventFragment)
                    .crop()
                    .cameraOnly()
                    .compress(2048)
                    .createIntent { launcher.launch(it) }
            }

            takeAttachmentButton.setOnClickListener {
                ImagePicker.with(this@NewEventFragment)
                    .crop()
                    .galleryOnly()
                    .compress(2048)
                    .createIntent { launcher.launch(it) }
            }

            chooseUsersButton.setOnClickListener {
//                TODO: переход на пользователей
            }

            chooseLocationButton.setOnClickListener {
                findNavController().navigate(R.id.action_newEventFragment_to_mapFragment)
            }

            saveButton.setOnClickListener {
                if (!newContent.text.isNullOrBlank()) {
                    viewModel.changeContent(newContent.text.toString())
                    viewModel.saveEvent()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Text must no be blank",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            addDateButton.setOnClickListener {
                val bottomSheetDialogFragment = BottomSheetDialog()
                bottomSheetDialogFragment.show(childFragmentManager, bottomSheetDialogFragment.tag)
            }
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
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