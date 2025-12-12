package com.example.college

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specialties")
data class Specialty(
    @PrimaryKey val id: Int,
    val name: String
)