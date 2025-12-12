package com.example.college

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.college.CollegeDatabase
import com.example.college.R
import com.example.college.databinding.FragmentAddStudentBinding
import kotlinx.coroutines.launch

class AddStudentFragment : Fragment() {

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!

    private var selectedDate = ""
    private val photoNames = listOf("photoone", "phototwo")
    private val selectedSpecialties = mutableMapOf<Int, Boolean>() // specialtyId → isBudget

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val role = prefs.getString("role", "") ?: ""

        if (role !in listOf("admission", "teacher")) {
            Toast.makeText(context, "Доступ запрещён", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupPhotoSpinner()
        setupDatePicker()
        loadSpecialties()
        setupListeners()
    }

    private fun setupPhotoSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, photoNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPhoto.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.editBirthDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                    binding.editBirthDate.setText(selectedDate)
                },
                year,
                month,
                day
            ).apply {
                datePicker.maxDate = System.currentTimeMillis() // Запрещаем будущие даты
            }.show()
        }
    }

    private fun loadSpecialties() {
        val db = CollegeDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.specialtyDao().getAllSpecialties().collect { specialties ->
                binding.specialtiesContainer.removeAllViews()
                selectedSpecialties.clear()

                specialties.forEach { specialty ->
                    val checkBox = CheckBox(requireContext()).apply {
                        id = View.generateViewId()
                        text = specialty.name
                        tag = specialty.id
                    }

                    val budgetRadio = RadioButton(requireContext()).apply {
                        text = "Бюджет"
                        isVisible = false
                    }

                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        budgetRadio.isVisible = isChecked
                        if (!isChecked) {
                            budgetRadio.isChecked = false
                        }
                        selectedSpecialties[specialty.id] = budgetRadio.isChecked
                    }

                    budgetRadio.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            selectedSpecialties.replaceAll { _, _ -> false }
                            selectedSpecialties[specialty.id] = true

                            binding.specialtiesContainer.children
                                .filterIsInstance<RadioButton>()
                                .forEach { rb -> if (rb != budgetRadio) rb.isChecked = false }
                        } else {
                            selectedSpecialties[specialty.id] = false
                        }
                    }

                    binding.specialtiesContainer.addView(checkBox)
                    binding.specialtiesContainer.addView(budgetRadio)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            val fullName = binding.editFullName.text.toString().trim()
            val group = binding.editGroup.text.toString().trim()
            val courseText = binding.editCourse.text.toString().trim()
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            if (fullName.isEmpty() || group.isEmpty() || courseText.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val course = courseText.toInt()
            if (course !in 1..4) {
                Toast.makeText(context, "Курс должен быть от 1 до 4", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDate.isEmpty()) {
                Toast.makeText(context, "Выберите дату рождения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val photoName = binding.spinnerPhoto.selectedItem.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                val db = CollegeDatabase.getDatabase(requireContext())
                val studentId = db.studentDao().insertStudent(
                    Student(
                        fullName = fullName,
                        groupName = group,
                        course = course,
                        birthDate = selectedDate,
                        photoName = photoName,
                        email = email,
                        password = password
                    )
                )

                selectedSpecialties.forEach { (specId, isBudget) ->
                    db.studentDao().insertStudentSpecialty(
                        StudentSpecialtyCrossRef(
                            studentId = studentId.toInt(),
                            specialtyId = specId,
                            isBudget = isBudget
                        )
                    )
                }

                Toast.makeText(context, "Студент добавлен", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}