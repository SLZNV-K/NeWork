package com.github.slznvk.nework.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentBottomSheetDialogBinding
import com.github.slznvk.nework.viewModel.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class BottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private lateinit var viewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            datePickerEditText.setOnClickListener {
                showDatePickerDialog()
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.onlineButton -> {
                        viewModel.changeTypeEvent("ONLINE")
                    }

                    R.id.offlineButton -> {
                        viewModel.changeTypeEvent("OFFLINE")
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.datePickerEditText.setText(selectedDate)
                viewModel.changeDateEvent(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }
}