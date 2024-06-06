package com.github.slznvk.nework.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.github.slznvk.nework.databinding.FragmentSelectDateBinding
import java.util.Calendar

class SelectDateFragment : DialogFragment() {
    private lateinit var binding: FragmentSelectDateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectDateBinding.inflate(layoutInflater, container, false)

        with(binding) {
            dateStartPicker.setOnClickListener {
                showDatePickerDialog(it as EditText)
            }

            dateEndPicker.setOnClickListener {
                showDatePickerDialog(it as EditText)
            }

            return root
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