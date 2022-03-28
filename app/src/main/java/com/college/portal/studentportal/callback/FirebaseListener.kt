package com.college.portal.studentportal.callback

import android.content.Context
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.college.portal.studentportal.R
import com.college.portal.studentportal.roomDatabase.groupEverything.Participant

interface FirebasePFCompleteListener{

    fun onComplete(participantList: MutableList<Participant>)
    fun onFailure()
}

