package com.github.slznvk.nework.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.slznvk.nework.databinding.FragmentNewPostBinding
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    private lateinit var binding: FragmentNewPostBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)


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

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.attachmentFrame.visibility = View.GONE
                return@observe
            }
            binding.attachmentFrame.visibility = View.VISIBLE
            binding.attachment.setImageURI(it.uri)
        }

        binding.removeButton.setOnClickListener {
            viewModel.clear()
        }

        binding.takePhotoButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .createIntent { launcher.launch(it) }
        }

        binding.saveButton.setOnClickListener {
            //TODO()
        }


        return binding.root
    }
}