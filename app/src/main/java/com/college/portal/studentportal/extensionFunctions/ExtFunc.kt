package com.college.portal.studentportal.extensionFunctions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getName(context: Context): String? {
    val returnCursor = context.contentResolver.query(this, null, null, null, null)
    val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor?.moveToFirst()
    val fileName = nameIndex?.let { returnCursor.getString(it) }
    returnCursor?.close()
    return fileName
}

val validExtensions = listOf("pdf", "docs", "jpg", "jpeg", "mp4", "amv")

val moderatorMenuForParticipant = arrayOf("Show profile", "Ban student")
val teacherMenuForParticipant =
    arrayOf("Show profile", "Ban student", "Promote student", "Demote student")
val studentMenuForParticipant = arrayOf("Show profile")
val banTimes = arrayOf("1 hour","8 hours","1 day","10 day","Until you unban")
val internalBanValues = arrayOf<Long>(3600,28800,86400,864000,-1)