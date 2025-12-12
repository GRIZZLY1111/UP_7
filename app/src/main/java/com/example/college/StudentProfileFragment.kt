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
import com.example.college.CollegeDatabase
import com.example.college.databinding.FragmentStudentProfileBinding
import kotlinx.coroutines.launch

class StudentProfileFragment : Fragment() {

    private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val email = prefs.getString("email", "") ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            val db = CollegeDatabase.getDatabase(requireContext())
            val student = db.studentDao().getStudentByEmail(email)

            if (student != null) {
                binding.textViewFullName.text = student.fullName
                binding.textViewGroup.text = "Группа: ${student.groupName}"
                binding.textViewCourse.text = "Курс: ${student.course}"
                binding.textViewBirthDate.text = "Дата рождения: ${student.birthDate}"

                val resId = context?.resources?.getIdentifier(
                    student.photoName,
                    "drawable",
                    context?.packageName
                ) ?: 0
                binding.imageView.setImageResource(if (resId != 0) resId else R.drawable.photoone)
            } else {
                Toast.makeText(context, "Не удалось найти профиль", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}