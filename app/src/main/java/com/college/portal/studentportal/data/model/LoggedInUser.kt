package com.college.portal.studentportal.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
class LoggedInUser(
    val userId: String,
    val displayName: String,
    val role: String,
    val lastLogin: String,
    val semester: String
)