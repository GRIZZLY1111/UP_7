package com.example.college

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Transaction
    @Query("SELECT * FROM students")
    fun getAllStudentsWithSpecialties(): Flow<List<StudentWithSpecialties>>

    @Transaction
    @Query("SELECT * FROM students WHERE fullName LIKE '%' || :query || '%' OR groupName LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<StudentWithSpecialties>>

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Insert
    suspend fun insertStudentSpecialty(ref: StudentSpecialtyCrossRef)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM student_specialty WHERE studentId = :studentId")
    suspend fun deleteStudentSpecialties(studentId: Int)

    @Query("SELECT * FROM students WHERE email = :email")
    suspend fun getStudentByEmail(email: String): Student?

    @Query("SELECT * FROM students WHERE email = :email AND password = :password")
    suspend fun loginStudent(email: String, password: String): Student?
}