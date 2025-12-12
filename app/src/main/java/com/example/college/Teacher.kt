package com.example.college

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
) {
    constructor(fullName: String, email: String, password: String, role: String) : this(0, fullName, email, password, role)
}