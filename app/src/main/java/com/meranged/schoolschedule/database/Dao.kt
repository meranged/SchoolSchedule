package com.meranged.schoolschedule.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SchoolScheduleDao {

    @Insert
    fun insert(subject: Subject)

    @Insert
    fun insert(teacher: Teacher)

    @Insert
    fun insert(timeslot: TimeSlot)

    @Transaction
    fun insertWeekTimeSlot(timeslot: TimeSlot){
        for (i in 1..6){
            timeslot.weekDay = i
            insert(timeslot)
        }
    }

    @Insert
    fun insert(lesson: Lesson)

    @Update
    fun update(subject: Subject)

    @Update
    fun update(teacher: Teacher)

    @Update
    fun update(timeslot: TimeSlot)

    @Transaction
    fun updatetWeekTimeSlot(timeslot: TimeSlot){
        for (i in 1..6){
            timeslot.weekDay = i
            update(timeslot)
        }
    }

    @Update
    fun update(lesson: Lesson)

    @Query("SELECT * FROM subject WHERE subjectId = :key")
    fun getSubject(key: Long): Subject?

    @Query("SELECT * FROM teacher WHERE teacherId = :key")
    fun getTeacher(key: Long): Teacher?

    @Query("SELECT * FROM time_slot WHERE timeslotId = :key")
    fun getTimeSlot(key: Long): Lesson?

    @Query("SELECT * FROM lesson WHERE lessonId = :key")
    fun getLesson(key: Long): Lesson?

    @Query("SELECT * FROM subject ORDER BY subjectId DESC")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM teacher ORDER BY teacherId DESC")
    fun getAllTeachers(): LiveData<List<Teacher>>

    @Query("SELECT * FROM time_slot ORDER BY timeslotId DESC")
    fun getAllTimeSlots(): LiveData<List<TimeSlot>>

    @Query("SELECT * FROM lesson ORDER BY lessonId DESC")
    fun getAllLessons(): LiveData<List<Lesson>>

    @Query("DELETE FROM teacher")
    fun deleteAllTeachers()

    @Query("DELETE FROM teacher WHERE teacherId = :key")
    fun deleteTeacher(key: Long)

    @Query("DELETE FROM subject")
    fun deleteAllSubjects()

    @Query("DELETE FROM subject WHERE subjectId = :key")
    fun deleteSubject(key: Long)

    @Query("DELETE FROM lesson")
    fun deleteAllLessons()

    @Query("DELETE FROM lesson WHERE lessonId = :key")
    fun deleteLesson(key: Long)

    @Query("DELETE FROM time_slot")
    fun deleteAllTimeSlots()

    @Query("DELETE FROM time_slot WHERE timeslotId = :key")
    fun deleteTimeSlot(key: Long)



}