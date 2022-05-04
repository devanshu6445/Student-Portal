package com.college.portal.studentportal.ui.notifications.editStudentDetails

import com.college.portal.studentportal.data.model.EditedProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditStudentDetailsRepository(private val listener:FirebaseProfileChangeCallback){

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mUser = FirebaseAuth.getInstance().currentUser
    fun updateProfile(profile:EditedProfile){
        mFirestore
            .collection("users")
            .document(mUser?.uid!!)
            .update(mapOf(
                "emailAddress" to profile.emailAddress,
                "presentLivingAddress" to profile.presentLivingAddress,
                "dateOfBirth" to profile.dateOfBirth,
                "phoneNumber" to profile.phoneNumber
            )).addOnCompleteListener {
                if(it.isSuccessful)
                    listener.onProfileChanged()
                else
                    listener.onProfileChangeFailure(it.exception!!)
            }
    }
}