package com.college.portal.studentportal

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.college.portal.studentportal.roomDatabase.subjectDatabase.SubjectDatabase

class WorkerTest(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val subjectDatabase = SubjectDatabase.getDatabase(context)

        return Result.success()
    }
}

