package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*
import android.content.Intent
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import android.text.TextUtils
import org.w3c.dom.Text

class Login : AppCompatActivity() {

    // See: https://developer.android.com/training/basics/intents/result
    private lateinit var auth: FirebaseAuth
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        // Create and launch sign-in intent
        /*btnGoogleSignIn.setOnClickListener {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }*/

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Succesfully signed in", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }


    public override fun onStart() {
        super.onStart()
        // Check is the user logged in
        val currentUser = auth.currentUser
        if (currentUser!=null){
            reload()
            //startActivity(Intent(this, profile_pg::class.java))
            //finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        val lgBtn = findViewById<Button>(R.id.btnLog)
        val frgpwd = findViewById<TextView>(R.id.frgpwd)
        val togglegrp = findViewById<MaterialButtonToggleGroup>(R.id.authToggleGroup)
        val logLyt = findViewById<LinearLayout>(R.id.logLyt)
        val regLyt = findViewById<LinearLayout>(R.id.regLyt)
        val regBtn = findViewById<Button>(R.id.btnReg)
        var name_U = findViewById<EditText>(R.id.edtxtName)
        val emailNew = findViewById<EditText>(R.id.edtxtEmailR)
        var pwd_N = findViewById<EditText>(R.id.edtxtPwdR)
        var pwd_C = findViewById<EditText>(R.id.edtcnpwd)

        lgBtn.setOnClickListener{
            performLogin()
        }
        regBtn.setOnClickListener{
            createAccount(name_U,emailNew,pwd_N,pwd_C)
        }

        togglegrp.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.logswitch -> {
                        logLyt.visibility = View.VISIBLE
                        regLyt.visibility = View.GONE
                    }

                    R.id.regswitch -> {
                        logLyt.visibility = View.GONE
                        regLyt.visibility = View.VISIBLE
                    }
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // Sign in
        private fun performLogin(){
        var emailU = findViewById<EditText>(R.id.edtxtEmail)
        val emailS = emailU.text.toString().trim()
        val pwd_U = findViewById<EditText>(R.id.edtxtPwd)
        val pwd_S = pwd_U.text.toString().trim()
        if (emailS.isEmpty()){
            emailU.error = "Email is required"
            return
        }
        if (pwd_S.isEmpty()){
            pwd_U.error="Password is required"
            return
        }
        val btnLog = findViewById<Button>(R.id.btnLog)
        btnLog.isEnabled = false
        btnLog.text = "Loggin in..."
        auth.signInWithEmailAndPassword(emailS, pwd_S)
            .addOnCompleteListener(this) { task ->
                // Re enable btn
                btnLog.isEnabled = true
                btnLog.text = "Log in"
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    saveLoginState(true)
                    //val user = auth.currentUser
                    val intent = Intent(this, profile_pg::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // Show user-friendly error message
                    val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                    Toast.makeText(
                        this@Login,
                        errorMessage,
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }
    //Save Login state
    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_logged_in", isLoggedIn)
            if (isLoggedIn) {
                putString("user_email", auth.currentUser?.email)
                putString("user_uid", auth.currentUser?.uid)
            }
            apply()
        }
    }
    // Helper function for user-friendly errors
    private fun getFriendlyErrorMessage(errorMessage: String?): String {
        return when {
            errorMessage?.contains("no user record") == true ->
                "No account found with this email"
            errorMessage?.contains("password is invalid") == true ->
                "Incorrect password. Please try again."
            errorMessage?.contains("network error") == true ->
                "Network error. Check your connection."
            else -> "Authentication failed. Please try again."
        }
    }
    // Register new user
    private fun createAccount(fullname:String,email:String,password:String,confirmpassword:String) {
        // Validation
        when {
            TextUtils.isEmpty(fullName) -> {
                etFullName.error = "Full name is required"
                etFullName.requestFocus()
                return
            }
            TextUtils.isEmpty(email) -> {
                etEmail.error = "Email is required"
                etEmail.requestFocus()
                return
            }
            !isValidEmail(email) -> {
                etEmail.error = "Please enter a valid email address"
                etEmail.requestFocus()
                return
            }
            TextUtils.isEmpty(password) -> {
                etPassword.error = "Password is required"
                etPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return
            }
            TextUtils.isEmpty(confirmPassword) -> {
                etConfirmPassword.error = "Please confirm your password"
                etConfirmPassword.requestFocus()
                return
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Passwords do not match"
                etConfirmPassword.requestFocus()
                return
            }
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }

    }
    // Email verification
    private fun sendEmailVerification() {

        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }

    }
    private fun updateUI(user: FirebaseUser?) {
    }

    private fun reload() {
    }
    companion object {
        private const val TAG = "EmailPassword"
        private const val RC_SIGN_IN = 100
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }

    }



        }

