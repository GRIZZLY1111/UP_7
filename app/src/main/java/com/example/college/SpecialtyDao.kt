package com.example.college

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialtyDao {
    @Query("SELECT * FROM specialties")
    fun getAllSpecialties(): Flow<List<Specialty>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpecialty(specialty: Specialty)
}