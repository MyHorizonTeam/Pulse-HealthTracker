package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        onSignInResult(res)
    }

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var googleLoginButton: MaterialButton
    private lateinit var googleRegisterButton: MaterialButton
    private lateinit var forgotPasswordText: TextView
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var logLayout: LinearLayout
    private lateinit var regLayout: LinearLayout
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var regName: EditText
    private lateinit var regEmail: EditText
    private lateinit var regPassword: EditText
    private lateinit var regConfirmPassword: EditText

    override fun onStart() {
        super.onStart()
        if (::auth.isInitialized && auth.currentUser != null) {
            startActivity(Intent(this, profile_pg::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginButton = findViewById(R.id.btnLog)
        registerButton = findViewById(R.id.btnReg)
        forgotPasswordText = findViewById(R.id.frgpwd)
        toggleGroup = findViewById(R.id.authToggleGroup)
        logLayout = findViewById(R.id.logLyt)
        regLayout = findViewById(R.id.regLyt)
        googleLoginButton = findViewById(R.id.btnGoogleSignIn)
        googleRegisterButton = findViewById(R.id.btnGoogleSignInR)
        loginEmail = findViewById(R.id.edtxtEmail)
        loginPassword = findViewById(R.id.edtxtPwd)
        regName = findViewById(R.id.edtxtName)
        regEmail = findViewById(R.id.edtxtEmailR)
        regPassword = findViewById(R.id.edtxtPwdR)
        regConfirmPassword = findViewById(R.id.edtcnpwd)

        toggleGroup.check(R.id.regswitch)
        logLayout.visibility = View.GONE
        regLayout.visibility = View.VISIBLE

        loginButton.setOnClickListener {
            performLogin()
        }

        registerButton.setOnClickListener {
            createAccount(
                regName.text.toString().trim(),
                regEmail.text.toString().trim(),
                regPassword.text.toString(),
                regConfirmPassword.text.toString()
            )
        }

        googleLoginButton.setOnClickListener {
            createSignInIntent()
        }

        googleRegisterButton.setOnClickListener {
            createSignInIntent()
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot password flow not added yet", Toast.LENGTH_SHORT).show()
        }

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.logswitch -> {
                        logLayout.visibility = View.VISIBLE
                        regLayout.visibility = View.GONE
                    }

                    R.id.regswitch -> {
                        logLayout.visibility = View.GONE
                        regLayout.visibility = View.VISIBLE
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

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            saveLoginState()
            Toast.makeText(this, getString(R.string.successfully_signed_in), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, profile_pg::class.java))
            finish()
        } else {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "FirebaseUI sign-in failed", result.idpResponse?.error)
        }
    }

    private fun performLogin() {
        val email = loginEmail.text.toString().trim()
        val password = loginPassword.text.toString()

        if (email.isEmpty()) {
            loginEmail.error = "Email is required"
            loginEmail.requestFocus()
            return
        }
        if (!isValidEmail(email)) {
            loginEmail.error = "Please enter a valid email address"
            loginEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            loginPassword.error = "Password is required"
            loginPassword.requestFocus()
            return
        }

        loginButton.isEnabled = false
        loginButton.setText(R.string.logging_in)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loginButton.isEnabled = true
                loginButton.setText(R.string.log_in)

                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    saveLoginState()
                    startActivity(Intent(this, profile_pg::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLoginState() {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_logged_in", true)
            putString("user_email", auth.currentUser?.email)
            putString("user_uid", auth.currentUser?.uid)
            apply()
        }
    }

    private fun getFriendlyErrorMessage(errorMessage: String?): String {
        return when {
            errorMessage?.contains("no user record", ignoreCase = true) == true ->
                "No account found with this email"
            errorMessage?.contains("password is invalid", ignoreCase = true) == true ->
                "Incorrect password. Please try again."
            errorMessage?.contains("network error", ignoreCase = true) == true ->
                "Network error. Check your connection."
            else -> "Authentication failed. Please try again."
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun createAccount(fullname: String, email: String, password: String, confirmPassword: String) {
        when {
            fullname.isBlank() -> {
                regName.error = "Full name is required"
                regName.requestFocus()
                return
            }

            email.isBlank() -> {
                regEmail.error = "Email is required"
                regEmail.requestFocus()
                return
            }

            !isValidEmail(email) -> {
                regEmail.error = "Please enter a valid email address"
                regEmail.requestFocus()
                return
            }

            password.isBlank() -> {
                regPassword.error = "Password is required"
                regPassword.requestFocus()
                return
            }

            password.length < 6 -> {
                regPassword.error = "Password must be at least 6 characters"
                regPassword.requestFocus()
                return
            }

            confirmPassword.isBlank() -> {
                regConfirmPassword.error = "Please confirm your password"
                regConfirmPassword.requestFocus()
                return
            }

            password != confirmPassword -> {
                regConfirmPassword.error = "Passwords do not match"
                regConfirmPassword.requestFocus()
                return
            }
        }

        registerButton.isEnabled = false
        registerButton.setText(R.string.creating_account)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                registerButton.isEnabled = true
                registerButton.setText(R.string.register)

                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullname)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }

                    saveLoginState()
                    sendEmailVerification()
                    startActivity(Intent(this, profile_pg::class.java))
                    finish()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser ?: return
        user.sendEmailVerification()
            .addOnCompleteListener(this) {
                Toast.makeText(this, getString(R.string.verification_email_sent), Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
