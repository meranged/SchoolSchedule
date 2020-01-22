package com.meranged.schoolschedule.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.meranged.schoolschedule.App
import com.meranged.schoolschedule.R
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
    fun getSubject(key: Long): LiveData<Subject>

    @Query("SELECT * FROM teacher WHERE teacherId = :key")
    fun getTeacher(key: Long): LiveData<Teacher>

    @Query("SELECT * FROM subject ORDER BY subjectId ASC")
    fun getAllSubjects(): LiveData<List<Subject>>

    @Query("SELECT * FROM teacher ORDER BY teacherId ASC")
    fun getAllTeachers(): LiveData<List<Teacher>>

    @Query("SELECT * FROM teacher ORDER BY teacherId DESC")
    fun getAllTeachersAsList(): List<Teacher>

    @Query("SELECT * FROM time_slot ORDER BY timeslotId DESC")
    fun getAllTimeSlots(): LiveData<List<TimeSlot>>

    @Query("SELECT COUNT(*) FROM time_slot")
    fun getCountOfTimeSlots(): Int

    @Query("SELECT COUNT(*) FROM teacher")
    fun getCountOfTeachers(): Int

    @Query("SELECT COUNT(*) FROM subject")
    fun getCountOfSubjects(): Int

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
    fun fillTimeSlotsInitialData(){

        val startTimeOfLessonHours = 8
        var startTimeOfLessonMins = 0
        val lengthOfLesson = 40
        val lengthOfPause = 10

        val timeSlot = TimeSlot(0
            ,0
            ,0
            ,0
            ,0
            ,0
            ,0)

        // fill 6 timeslots for 6 weekdays
        for (weekday in 1..6){

            var timeInMins: Int

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
    fun fillTeachersInitialData(){

        val teacher = Teacher()
        val res = App.context!!.resources

        teacher.firstName = res.getString(R.string.teacher1_initial_first_name)
        teacher.secondName = res.getString(R.string.teacher1_initial_second_name)
        teacher.thirdName = res.getString(R.string.teacher1_initial_family)

        insert(teacher)

        teacher.firstName = res.getString(R.string.teacher2_initial_first_name)
        teacher.secondName = res.getString(R.string.teacher2_initial_second_name)
        teacher.thirdName = res.getString(R.string.teacher2_initial_family)

        insert(teacher)

        teacher.firstName = res.getString(R.string.teacher3_initial_first_name)
        teacher.secondName = res.getString(R.string.teacher3_initial_second_name)
        teacher.thirdName = res.getString(R.string.teacher3_initial_family)

        insert(teacher)
    }

    @Transaction
    fun fillSubjectsInitialData(){
        val subject = Subject()
        val res = App.context!!.resources

        subject.name = res.getString(R.string.subject1_name)
        subject.roomNumber = res.getString(R.string.subject_place1)
        insert(subject)

        subject.name = res.getString(R.string.subject2_name)
        subject.roomNumber = res.getString(R.string.subject_place1)
        insert(subject)

        subject.name = res.getString(R.string.subject3_name)
        subject.roomNumber = res.getString(R.string.subject_place1)
        insert(subject)

        subject.name = res.getString(R.string.subject4_name)
        subject.roomNumber = res.getString(R.string.subject_place2)
        insert(subject)

    }


    @Transaction
    fun checkAndFillTimeSlots(){
        val count = getCountOfTimeSlots()
        Log.i("dbdebug","count of timeslots = $count")
        if (count < 36){
            fillTimeSlotsInitialData()
        }
    }

    @Transaction
    fun checkAndFillTeachersList(){
        val count = getCountOfTeachers()
        Log.i("dbdebug","count of teachers = $count")
        if (count < 1){
            fillTeachersInitialData()
        }
    }

    @Transaction
    fun checkAndFillSubjectsList(){
        val count = getCountOfSubjects()
        Log.i("dbdebug","count of subjects = $count")
        if (count < 1){
            fillSubjectsInitialData()
        }
    }

    @Transaction
    @Query("SELECT * FROM Subject")
    fun getSubjectsWithTeacher(): LiveData<List<SubjectWithTeacher>>

    @Transaction
    @Query("SELECT * FROM Teacher")
    fun getTeacherWithSubjects(): LiveData<List<TeacherWithSubjects>>

}