package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentNewJobBinding
import com.github.slznvk.nework.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewJobFragment : Fragment(R.layout.fragment_new_job) {
    private val binding by viewBinding(FragmentNewJobBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: UserViewModel by activityViewModels()

        binding.date.setOnClickListener {
            val selectDateFragment = SelectDateFragment()
            selectDateFragment.show(childFragmentManager, selectDateFragment.tag)
        }
        binding.apply {
            saveJob.setOnClickListener {
                viewModel.changeContentJob(
                    name = name.text.toString(),
                    position = position.text.toString(),
                    link = link.text.toString()
                )
                viewModel.saveJob()
                findNavController().navigateUp()
            }
        }
    }
}