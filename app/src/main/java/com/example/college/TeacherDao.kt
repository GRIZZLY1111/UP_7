package com.example.college

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Transaction
    @Query("SELECT * FROM teachers WHERE role != 'admission'")
    fun getAllTeachersWithSpecialties(): Flow<List<TeacherWithSpecialties>>

    @Query("SELECT * FROM teachers WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): Teacher?
    @Query("SELECT * FROM teachers")
    suspend fun getAllTeachers(): List<Teacher>

    @Insert
    suspend fun insertTeacher(teacher: Teacher)

    @Insert
    suspend fun insertTeacherSpecialty(ref: TeacherSpecialtyCrossRef)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("DELETE FROM teacher_specialty WHERE teacherId = :teacherId")
    suspend fun deleteTeacherSpecialties(teacherId: Int)

    @Query("SELECT * FROM teachers WHERE email = :email AND password = :password AND role = :role")
    suspend fun loginByRole(email: String, password: String, role: String): Teacher?

}