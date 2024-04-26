package com.github.slznvk.nework

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.slznvk.nework.databinding.FragmentNewPostBinding

class NewPostFragment : Fragment() {

    private lateinit var binding: FragmentNewPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}