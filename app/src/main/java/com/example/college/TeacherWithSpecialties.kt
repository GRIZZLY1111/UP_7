package com.example.college

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.college.Teacher
import com.example.college.Specialty
import com.example.college.TeacherSpecialtyCrossRef

data class TeacherWithSpecialties(
    @Embedded val teacher: Teacher,
    @Relation(
        entity = Specialty::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TeacherSpecialtyCrossRef::class,
            parentColumn = "teacherId",
            entityColumn = "specialtyId"
        )
    )
    val specialties: List<Specialty>
)
