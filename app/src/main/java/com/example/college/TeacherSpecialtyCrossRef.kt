package com.example.college

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "teacher_specialty",
    primaryKeys = ["teacherId", "specialtyId"],
    foreignKeys = [
        ForeignKey(
            entity = Teacher::class,
            parentColumns = ["id"],
            childColumns = ["teacherId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Specialty::class,
            parentColumns = ["id"],
            childColumns = ["specialtyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("specialtyId")]
)
data class TeacherSpecialtyCrossRef(
    val teacherId: Int,
    val specialtyId: Int,
    val hours: Int
)