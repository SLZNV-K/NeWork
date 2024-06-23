package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentNewJobBinding


class NewJobFragment : Fragment(R.layout.fragment_new_job) {
    private val binding by viewBinding(FragmentNewJobBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.date.setOnClickListener {
            val selectDateFragment = SelectDateFragment()
            selectDateFragment.show(childFragmentManager, selectDateFragment.tag)
        }
    }
}