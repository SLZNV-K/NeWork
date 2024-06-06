package com.github.slznvk.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.slznvk.nework.databinding.FragmentNewJobBinding


class NewJobFragment : Fragment() {
    private lateinit var binding: FragmentNewJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewJobBinding.inflate(layoutInflater, container, false)

        binding.date.setOnClickListener {
            val selectDateFragment = SelectDateFragment()
            selectDateFragment.show(childFragmentManager, selectDateFragment.tag)
        }
        return binding.root
    }
}