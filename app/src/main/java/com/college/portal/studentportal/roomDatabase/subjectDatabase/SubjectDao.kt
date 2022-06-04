package com.college.portal.studentportal.roomDatabase.subjectDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.college.portal.studentportal.data.model.Subject

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubjectInBulk(subjectList: List<Subject>)

    @Query("SELECT * FROM subjects WHERE subCourse == :course AND subSem == :semester")
    fun getSubjectsBySemesterAndCourse(course:String,semester:String):List<Subject>

    @Query("SELECT subCode FROM subjects WHERE subCourse == :course")
    fun getSubjectsBySemesterAndCourse(course: String): List<String>

    @Query("SELECT * FROM subjects")
    fun getSubjectList():List<Subject>
}