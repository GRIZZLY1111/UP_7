package com.example.college

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "student_specialty",
    primaryKeys = ["studentId", "specialtyId"],
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Specialty::class,
            parentColumns = ["id"],
            childColumns = ["specialtyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("specialtyId") // ← добавлено
    ]
)
data class StudentSpecialtyCrossRef(
    val studentId: Int,
    val specialtyId: Int,
    val isBudget: Boolean
)