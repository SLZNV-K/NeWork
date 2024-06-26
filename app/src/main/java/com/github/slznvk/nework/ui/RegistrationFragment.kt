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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentRegistrationBinding
import com.github.slznvk.nework.model.PhotoModel
import com.github.slznvk.nework.utills.load
import com.github.slznvk.nework.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)

        with(binding) {

            signInButton.setOnClickListener {
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
            }

            val name = name.text
            val login = login.text
            val password = password.text
            val passwordConfirmation = passwordCheck.text
            var photo: PhotoModel? = null

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
                            avatar.load(uri.toString(), true)
                            photo = PhotoModel(uri, uri.toFile())
                        }
                    }
                }

            avatar.setOnClickListener {
                ImagePicker.with(this@RegistrationFragment)
                    .crop()
                    .compress(64)
                    .createIntent { launcher.launch(it) }
            }

            registerButton.setOnClickListener {
                if (password.equals(passwordConfirmation)) {
                    Toast.makeText(
                        context,
                        getString(R.string.passwords_don_t_match),
                        Toast.LENGTH_LONG
                    ).show()
                } else
                    if (login.isBlank() || name.isBlank() ||
                        password.isBlank() || passwordConfirmation.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            getString(R.string.fields_cannot_be_empty),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        authViewModel.registration(
                            login.toString(),
                            password.toString(),
                            name.toString(),
                            photo
                        )
                    }
            }
            authViewModel.authStateModel.observe(viewLifecycleOwner) { state ->
                if (state.error) {
                    Toast.makeText(
                        context,
                        getString(R.string.a_user_with_this_username_already_exists),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val previousFragment =
                        findNavController().previousBackStackEntry?.destination?.id
                    if (previousFragment == R.id.loginFragment) {
                        findNavController().navigate(R.id.postsFeedFragment)
                    } else findNavController().navigateUp()
                }
            }

            return root
        }
    }
}