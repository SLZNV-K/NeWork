package com.github.slznvk.nework.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.slznvk.nework.R
import com.github.slznvk.nework.databinding.FragmentSelectDateBinding
import com.github.slznvk.nework.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class SelectDateFragment : DialogFragment(R.layout.fragment_select_date) {

    private val binding by viewBinding(FragmentSelectDateBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: UserViewModel by activityViewModels()

        with(binding) {
            dateStartPicker.setOnClickListener {
                showDatePickerDialog(it as EditText)
            }

            dateEndPicker.setOnClickListener {
                showDatePickerDialog(it as EditText)
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

            saveButton.setOnClickListener {
                viewModel.changeDateJob(
                    dateStartPicker.text.toString(),
                    dateEndPicker.text.toString()
                )
                dismiss()
            }
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                editText.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }
}