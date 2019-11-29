package com.meranged.schoolschedule.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.meranged.schoolschedule.getHoursFromMins
import com.meranged.schoolschedule.getMinsFromMins

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

    @Update
    fun update(subject: Subject)

    @Update
    fun update(teacher: Teacher)

    @Update
    fun update(timeslot: TimeSlot)

    @Transaction
    fun updateWeekTimeSlot(timeslot: TimeSlot){

        val lessons = getTimeSlotsByNumber(timeslot.number)

        for (lesson in lessons){
            lesson.startTimeHours = timeslot.startTimeHours
            lesson.startTimeMinutes = timeslot.startTimeMinutes
            lesson.finishTimeHours = timeslot.finishTimeHours
            lesson.finishTimeMinutes = timeslot.finishTimeMinutes
            update(lesson)
        }
    }

    @Query("SELECT * FROM time_slot WHERE number = :key")
    fun getTimeSlotsByNumber(key: Int): List<TimeSlot>

    @Query("SELECT * FROM time_slot WHERE week_day = 1")
    fun getEtalonTimeSlots(): LiveData<List<TimeSlot>>

    @Query("SELECT * FROM subject WHERE subjectId = :key")
    fun getSubject(key: Long): Subject?

    @Query("SELECT * FROM teacher WHERE teacherId = :key")
    fun getTeacher(key: Long): Teacher?

    @Query("SELECT * FROM subject ORDER BY subjectId DESC")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM teacher ORDER BY teacherId DESC")
    fun getAllTeachers(): LiveData<List<Teacher>>

    @Query("SELECT * FROM time_slot ORDER BY timeslotId DESC")
    fun getAllTimeSlots(): LiveData<List<TimeSlot>>

    @Query("SELECT COUNT(*) FROM time_slot")
    fun getCountOfTimeSlots(): Int

    @Query("DELETE FROM teacher")
    fun deleteAllTeachers()

    @Query("DELETE FROM teacher WHERE teacherId = :key")
    fun deleteTeacher(key: Long)

    @Transaction
    fun deleteTeacherWithSubjectsUpd(key: Long){
        updateSubjectSetTeacherNull(key)
        deleteTeacher(key)
    }

    @Query("UPDATE subject SET teacher_id = null WHERE teacher_id = :key")
    fun updateSubjectSetTeacherNull(key: Long)

    @Query("DELETE FROM subject")
    fun deleteAllSubjects()

    @Query("DELETE FROM subject WHERE subjectId = :key")
    fun deleteSubject(key: Long)


    @Query("DELETE FROM time_slot")
    fun deleteAllTimeSlots()

    @Query("DELETE FROM time_slot WHERE timeslotId = :key")
    fun deleteTimeSlot(key: Long)

    @Transaction
    fun fillDatabaseInitialData(){

        var startTimeOfLessonHours = 8
        var startTimeOfLessonMins = 0
        var lengthOfLesson = 40
        var lengthOfPause = 10



        var timeSlot: TimeSlot = TimeSlot(0
            ,0
            ,0
            ,0
            ,0
            ,0
            ,0)

        // fill 6 timeslots for 6 weekdays
        for (weekday in 1..6){

            var timeInMins: Int = startTimeOfLessonHours*60 + startTimeOfLessonMins

            for (timeSlotNumber in 1..6){
                timeSlot.weekDay = weekday
                timeSlot.number = timeSlotNumber

                timeInMins = startTimeOfLessonHours*60 + (lengthOfLesson + lengthOfPause)*(timeSlotNumber-1)

                timeSlot.startTimeHours = getHoursFromMins(timeInMins)
                timeSlot.startTimeMinutes = getMinsFromMins(timeInMins)

                timeSlot.finishTimeHours = getHoursFromMins(timeInMins + lengthOfLesson)
                timeSlot.finishTimeMinutes = getMinsFromMins(timeInMins + lengthOfLesson)

                Log.i("dbdebug", timeSlot.toString())

                insert(timeSlot)
            }
        }
    }

    @Transaction
    fun checkAndFillTimeSlots(){
        val count = getCountOfTimeSlots()
        Log.i("dbdebug","count = $count")
        if (count < 36){
            fillDatabaseInitialData()
        }
    }
}