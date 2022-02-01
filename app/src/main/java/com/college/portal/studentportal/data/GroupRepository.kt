package com.college.portal.studentportal.data

import android.util.Log
import com.college.portal.studentportal.callback.FirebaseGDFCompleteListener
import com.college.portal.studentportal.data.model.BasicGroupData
import com.college.portal.studentportal.data.model.LoggedInUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class GroupRepository {

    companion object{
        private const val TAG = "GroupRepository: "
    }
    private val firebaseDatabase = Firebase.database
    private val databaseReference = firebaseDatabase.reference
    private val cloudFirestoreReference = FirebaseFirestore.getInstance()

    fun retrieveGroupsFirestore(listener: FirebaseGDFCompleteListener) { retrieveGroupFirestore(listener)}

    private fun retrieveGroupFirestore(listener: FirebaseGDFCompleteListener){

        val groupSemester = groupSemester()
        cloudFirestoreReference
                .collection("Groups")
                .whereEqualTo("groupSem",groupSemester)
                .get()
                .addOnSuccessListener {
                    val groupList:MutableList<BasicGroupData> = arrayListOf()
                    try {
                        for(documentSnapshot in it){
                            val groupData:BasicGroupData = documentSnapshot.toObject(BasicGroupData::class.java)
                            groupList.add(groupData)
                        }
                        listener.onFetchComplete(groupList)
                    } catch (e:NullPointerException){
                        Log.e(TAG, "retrieveGroup: Null",e)
                    } catch (e:Exception){
                        Log.e(TAG, "retrieveGroup: unknown", e)
                    }
                }
    }

    fun retrieveGroupsRDatabase(listener: FirebaseGDFCompleteListener, loggedInUser: LoggedInUser){retrieveGroupRDatabase(listener,loggedInUser)}

    private fun retrieveGroupRDatabase(listener: FirebaseGDFCompleteListener,loggedInUser: LoggedInUser){
        databaseReference
                .child("groups")
                .child("sem${loggedInUser.semester}") //TODO: Need to change the this child with user semester dynamically
                .addValueEventListener(object: ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val groupList = mutableListOf<BasicGroupData>()
                        lateinit var groupName : String
                        lateinit var groupObject: BasicGroupData
                        for(dataSnapshot in snapshot.children){
                            for (dataSnapshotGroupDetails in dataSnapshot.children){
                                groupObject = dataSnapshotGroupDetails.getValue<BasicGroupData>()!!
                                //groupName = dataSnapshotGroupDetails.value.toString()
                                Log.d(TAG, "onDataChange: Value Retrieved  ${groupObject.groupName}")
                            }
                            groupList.add(groupObject)
                            //TODO: Change the database and correct the code for GroupData object
                        }
                        listener.onFetchComplete(groupList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }

                })
    }

    private fun groupSemester():String{
        lateinit var groupSemester:String
        //going to be removed in future(this is useless)
        //TODO: implementation not done (Fetch Group Semester)
        return groupSemester
    }
}