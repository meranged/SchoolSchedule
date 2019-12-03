package com.meranged.schoolschedule.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Subject::class, Teacher::class, TimeSlot::class],
    version = 2,
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
                        .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Thread(Runnable { prepopulateDb(getInstance(context))}).start()
                        }
                    })
                        .build()
                    INSTANCE = instance


                }

                return instance
            }
        }

        private fun prepopulateDb(db: SchoolScheduleDatabase) {
            db.dao.checkAndFillTimeSlots()
        }
    }
}