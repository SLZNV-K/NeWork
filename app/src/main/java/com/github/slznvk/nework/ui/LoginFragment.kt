package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentLoginBinding
import com.github.slznvk.nework.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        with(binding) {
            regiatrationButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }

            val log = login.text
            val pass = password.text

            signInButton.setOnClickListener {
                if (log.isBlank() || pass.isBlank()) {
                    Toast.makeText(
                        context,
                        getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    authViewModel.authentication(
                        log.toString(),
                        pass.toString()
                    )
                }
            }
            authViewModel.authStateModel.observe(viewLifecycleOwner) { state ->
                if (state.error) {
                    login.error = getString(R.string.invalid_login)
                    password.error = getString(R.string.invalid_password)
                } else {
                    val previousFragment =
                        findNavController().previousBackStackEntry?.destination?.id
                    if (previousFragment == R.id.registrationFragment) {
                        findNavController().navigate(R.id.postsFeedFragment)
                    } else findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }
}
