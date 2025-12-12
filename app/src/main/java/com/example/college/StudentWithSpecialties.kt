package com.example.college

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.college.Student
import com.example.college.Specialty
import com.example.college.StudentSpecialtyCrossRef

data class StudentWithSpecialties(
    @Embedded val student: Student,
    @Relation(
        entity = Specialty::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            StudentSpecialtyCrossRef::class,
            parentColumn = "studentId",
            entityColumn = "specialtyId"
        )
    )
    val specialties: List<Specialty>
)
