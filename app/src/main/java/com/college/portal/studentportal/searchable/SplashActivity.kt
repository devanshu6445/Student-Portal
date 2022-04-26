package com.college.portal.studentportal.searchable

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.college.portal.studentportal.StudentMain
import com.college.portal.studentportal.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this@SplashActivity, StudentMain::class.java))
            finishAffinity()
        }else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }

}