package com.college.portal.studentportal

class CurrentUserException(): Exception() {

    var details:String = "Current user not found"
    get() = field
    private set(value) {}

    constructor(message: String): this(){
        details = message
    }
}