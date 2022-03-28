package com.college.portal.studentportal.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.college.portal.studentportal.R
import com.college.portal.studentportal.StudentMain
import com.college.portal.studentportal.databinding.ActivityLoginBinding
import com.college.portal.studentportal.roomDatabase.user.CurrentUserDatabase
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var loginViewModel: LoginViewModel? = null
    private var binding: ActivityLoginBinding? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            layoutInflater
        )


        setContentView(binding!!.root)
        supportActionBar?.hide()
        val database = CurrentUserDatabase.getDatabase(applicationContext)
        val preferences = getSharedPreferences("userData", MODE_PRIVATE)
        loginViewModel = ViewModelProvider(this,
            LoginViewModelFactory(
                preferences, database
            )
        ).get(LoginViewModel::class.java)
        val usernameEditText: EditText? = binding!!.userEmail
        val passwordEditText: EditText? = binding!!.userPassword
        val loginButton = binding!!.login
        loginViewModel!!.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }

            loginButton.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText!!.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordEditText!!.error = getString(loginFormState.passwordError!!)
            }
        })
        loginViewModel!!.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) {
                return@Observer
            }
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                startActivity(Intent(this@LoginActivity, StudentMain::class.java))
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
        })
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel!!.loginDataChanged(
                    usernameEditText!!.text.toString(),
                    passwordEditText!!.text.toString()
                )
            }
        }
        usernameEditText!!.addTextChangedListener(afterTextChangedListener)
        passwordEditText!!.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel!!.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }
        loginButton.setOnClickListener {
            loginViewModel!!.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView?) {
        val welcome = getString(R.string.welcome) + model!!.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        Toast.makeText(applicationContext, errorString!!, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null)
            startActivity(Intent(this@LoginActivity,StudentMain::class.java))
    }
}