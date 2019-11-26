package com.meranged.schoolschedule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Subject::class, Teacher::class, TimeSlot::class, Lesson::class],
    version = 1,
    exportSchema = false
)
abstract class SchoolScheduleDatabase : RoomDatabase() {

    abstract val dao: SchoolScheduleDao

    companion object {

        @Volatile
        private var INSTANCE: SchoolScheduleDatabase? = null

        fun getInstance(context: Context): SchoolScheduleDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SchoolScheduleDatabase::class.java,
                        "school_schedule_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}