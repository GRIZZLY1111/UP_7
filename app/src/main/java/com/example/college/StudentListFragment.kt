package com.example.college

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.databinding.FragmentStudentListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
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

        val recyclerView = binding.recyclerView
        val adapter = StudentAdapter(
            onEdit = { student ->
                Toast.makeText(context, "Редактирование ещё не реализовано", Toast.LENGTH_SHORT).show()
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

        viewLifecycleOwner.lifecycleScope.launch {
            CollegeDatabase.getDatabase(requireContext())
                .studentDao()
                .getAllStudentsWithSpecialties()
                .collect { list ->
                    adapter.submitList(list.map { it.student })
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}