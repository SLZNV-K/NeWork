package com.github.slznvk.nework.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.domain.dto.EventType
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentBottomSheetDialogBinding
import com.github.slznvk.nework.viewModel.EventViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar

class BottomSheetDialog : BottomSheetDialogFragment(R.layout.fragment_bottom_sheet_dialog) {
    private val binding by viewBinding(FragmentBottomSheetDialogBinding::bind)
    private val viewModel: EventViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            datePickerEditText.setOnClickListener {
                showDatePickerDialog()
            }
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.onlineButton -> {
                        viewModel.changeTypeEvent(EventType.ONLINE)
                    }

                    R.id.offlineButton -> {
                        viewModel.changeTypeEvent(EventType.OFFLINE)
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