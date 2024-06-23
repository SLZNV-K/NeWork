package com.github.slznvk.nework.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentNewEventBinding
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.ui.UsersFragment.Companion.CHOOSER_SCREEN
import com.github.slznvk.nework.ui.UsersFragment.Companion.FRAGMENT_TYPE
import com.github.slznvk.nework.viewModel.EventViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewEventFragment : Fragment(R.layout.fragment_new_event) {
    private val binding by viewBinding(FragmentNewEventBinding::bind)
    private val viewModel: EventViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        with(binding) {
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
                findNavController().navigate(
                    R.id.action_newEventFragment_to_usersFragment,
                    Bundle().apply {
                        putBoolean(CHOOSER_SCREEN, true)
                        putString(FRAGMENT_TYPE, FRAGMENT_EVENT)
                    }
                )
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
                        getString(R.string.text_must_no_be_blank),
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

    companion object{
        const val FRAGMENT_EVENT = "FRAGMENT_EVENT"
    }
}