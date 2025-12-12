package com.example.college

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        Student::class,
        Teacher::class,
        Specialty::class,
        StudentSpecialtyCrossRef::class,
        TeacherSpecialtyCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CollegeDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun specialtyDao(): SpecialtyDao

    companion object {
        @Volatile
        private var INSTANCE: CollegeDatabase? = null

        fun getDatabase(context: Context): CollegeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CollegeDatabase::class.java,
                    "college_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}