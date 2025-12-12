package com.example.college

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.college.CollegeDatabase
import com.example.college.Teacher
import com.example.college.Specialty
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var db: CollegeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = CollegeDatabase.getDatabase(this)

        lifecycleScope.launch {
            val teacherDao = db.teacherDao()
            val specialtyDao = db.specialtyDao()

            if (teacherDao.getAllTeachers().isEmpty()) {
                teacherDao.insertTeacher(
                    Teacher(
                        "Админ (Приёмная)",
                        "admin@college.ru",
                        "admin123",
                        "admission"
                    )
                )
            }

            if (specialtyDao.getAllSpecialties().firstOrNull()?.isEmpty() == true) {
                listOf(
                    Specialty(1, "Информационные системы"),
                    Specialty(2, "Экономика и бухгалтерия"),
                    Specialty(3, "Право и организация социального обеспечения")
                ).forEach { specialty ->
                    specialtyDao.insertSpecialty(specialty)
                }
            }
        }

        val email = findViewById<EditText>(R.id.editEmail)
        val password = findViewById<EditText>(R.id.editPassword)
        val loginBtn = findViewById<Button>(R.id.buttonLogin)

        loginBtn.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()

            val spinnerRole = findViewById<Spinner>(R.id.spinnerRole)
            val selectedRoleIndex = spinnerRole.selectedItemPosition
            val rolesArray = resources.getStringArray(R.array.roles)
            val role = when (selectedRoleIndex) {
                0 -> "admission"
                1 -> "teacher"
                2 -> "student"
                else -> ""
            }

            if (role.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Выберите роль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var user: Teacher? = null
                var student: Student? = null

                if (role in listOf("admission", "teacher")) {
                    user = db.teacherDao().loginByRole(e, p, role)
                } else if (role == "student") {
                    student = db.studentDao().loginStudent(e, p)
                }

                if (user != null || student != null) {
                    getSharedPreferences("session", Context.MODE_PRIVATE).edit()
                        .putString("email", e)
                        .putString("role", role)
                        .putBoolean("isLoggedIn", true)
                        .apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    val errorMessage = when (role) {
                        "admission" -> "Пользователь с ролью 'Приёмная комиссия' не существует"
                        "teacher" -> "Преподаватель с таким email/паролем не найден"
                        "student" -> "Студент с таким email/паролем не найден"
                        else -> "Неверный email, пароль или роль"
                    }
                    Toast.makeText(
                        this@LoginActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}