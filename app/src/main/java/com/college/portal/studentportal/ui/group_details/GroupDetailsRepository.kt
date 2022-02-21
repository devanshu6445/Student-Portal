package com.college.portal.studentportal.ui.group_details

import com.college.portal.studentportal.data.GroupRepository

class GroupDetailsRepository private constructor() {

    companion object{
        @Volatile
        private var INSTANCE: GroupDetailsRepository? = null

        fun getRepository(): GroupDetailsRepository{

            return INSTANCE?:synchronized(this){
                val instance = GroupDetailsRepository()
                INSTANCE = instance
                instance
            }
        }
    }



}