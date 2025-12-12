package com.example.college

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val groupName: String,
    val course: Int,
    val birthDate: String,
    val photoName: String,
    val email: String? = null,
    val password: String? = null
) {
    constructor(fullName: String, groupName: String, course: Int, birthDate: String, photoName: String, email: String?, password: String?) : this(0, fullName, groupName, course, birthDate, photoName, email, password)
}