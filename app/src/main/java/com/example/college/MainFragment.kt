package com.example.college

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.college.CollegeDatabase
import com.example.college.Student
import com.example.college.Teacher
import com.example.college.StudentAdapter
import com.example.college.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerView
        val searchEdit = binding.editSearch

        val prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val role = prefs.getString("role", "") ?: ""
        binding.buttonAddTeacher.isVisible = role == "admission"
        binding.buttonStudents.isVisible = role in listOf("admission", "teacher")
        binding.buttonTeachers.isVisible = role == "admission"
        binding.buttonAddStudent.isVisible = role in listOf("admission", "teacher")
        binding.buttonAddTeacher.setOnClickListener {
            findNavController().navigate(R.id.addTeacherFragment)
        }

        val adapter = StudentAdapter(
            onEdit = { student ->
                Toast.makeText(context, "Редактирование — в разработке", Toast.LENGTH_SHORT).show()
            },
            onDelete = { student ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val db = CollegeDatabase.getDatabase(requireContext())
                    db.studentDao().deleteStudent(student)
                    Toast.makeText(context, "Студент удалён", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        if (role == "student") {
            findNavController().navigate(R.id.studentProfileFragment)
            return
        }

        val db = CollegeDatabase.getDatabase(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            db.studentDao().getAllStudentsWithSpecialties().collect { list ->
                val students = list.map { it.student }
                adapter.submitList(students)
            }
        }

        searchEdit.setOnKeyListener { _, keyCode, event ->
            if (event?.action == android.view.KeyEvent.ACTION_UP &&
                keyCode == android.view.KeyEvent.KEYCODE_ENTER
            ) {
                val query = searchEdit.text.toString().trim()
                viewLifecycleOwner.lifecycleScope.launch {
                    db.studentDao().searchStudents(query).collect { list ->
                        val students = list.map { it.student }
                        adapter.submitList(students)
                    }
                }
                return@setOnKeyListener true
            }
            false
        }
        binding.buttonStudents.setOnClickListener {
            findNavController().navigate(R.id.studentListFragment)
        }

        binding.buttonTeachers.setOnClickListener {
            findNavController().navigate(R.id.teacherListFragment)
        }

        binding.buttonAddStudent.setOnClickListener {
            findNavController().navigate(R.id.addStudentFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

