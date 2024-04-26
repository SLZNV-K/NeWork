package com.github.slznvk.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.slznvk.nework.databinding.FragmentPostsFeedBinding

class PostsFeedFragment : Fragment() {

    private lateinit var binding: FragmentPostsFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsFeedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}