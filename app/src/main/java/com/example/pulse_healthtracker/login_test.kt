package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
class login_test : AppCompatActivity() {
    private lateinit var tvLoginTab: MaterialTextView
    private lateinit var tvRegisterTab: MaterialTextView
    private lateinit var loginScrollView: NestedScrollView
    private lateinit var registerScrollView: NestedScrollView

    // Login views
    private lateinit var etLoginEmail: TextInputEditText
    private lateinit var etLoginPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvForgotPassword: MaterialTextView

    // Register views
    private lateinit var etRegisterName: TextInputEditText
    private lateinit var etRegisterEmail: TextInputEditText
    private lateinit var etRegisterPassword: TextInputEditText
    private lateinit var etRegisterConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnGoogleRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()

        // Default show login
        showLoginForm()
    }

    private fun initViews() {
        // Tabs
        tvLoginTab = findViewById(R.id.tvLoginTab)
        tvRegisterTab = findViewById(R.id.tvRegisterTab)
        loginScrollView = findViewById(R.id.loginScrollView)
        registerScrollView = findViewById(R.id.registerScrollView)

        // Login views
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Register views
        etRegisterName = findViewById(R.id.etRegisterName)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoogleRegister = findViewById(R.id.btnGoogleRegister)
    }

    private fun setupClickListeners() {
        tvLoginTab.setOnClickListener {
            showLoginForm()
        }

        tvRegisterTab.setOnClickListener {
            showRegisterForm()
        }

        btnLogin.setOnClickListener {
            performLogin()
        }

        btnRegister.setOnClickListener {
            performRegistration()
        }

        tvForgotPassword.setOnClickListener {
            handleForgotPassword()
        }

        findViewById<MaterialButton>(R.id.btnGoogleSignIn).setOnClickListener {
            handleGoogleSignIn()
        }

        btnGoogleRegister.setOnClickListener {
            handleGoogleSignIn()
        }
    }

    private fun showLoginForm() {
        // Show login, hide register
        loginScrollView.visibility = android.view.View.VISIBLE
        registerScrollView.visibility = android.view.View.GONE

        // Update tab styles
        tvLoginTab.setTextColor(getColor(R.color.black))
        tvLoginTab.setBackgroundResource(R.drawable.bottom_selected_indicator)
        tvRegisterTab.setTextColor(getColor(R.color.gray))
        tvRegisterTab.setBackgroundResource(0)

        // Update subtitle
        findViewById<TextView>(R.id.subtitleTextView)?.text = "Welcome back! Login to continue"
    }

    private fun showRegisterForm() {
        // Show register, hide login
        loginScrollView.visibility = android.view.View.GONE
        registerScrollView.visibility = android.view.View.VISIBLE

        // Update tab styles
        tvRegisterTab.setTextColor(getColor(R.color.black))
        tvRegisterTab.setBackgroundResource(R.drawable.bottom_selected_indicator)
        tvLoginTab.setTextColor(getColor(R.color.gray))
        tvLoginTab.setBackgroundResource(0)

        // Update subtitle
        findViewById<TextView>(R.id.subtitleTextView)?.text = "Create account to get started"
    }

    private fun performLogin() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                etLoginEmail.error = "Email is required"
                etLoginEmail.requestFocus()
            }
            password.isEmpty() -> {
                etLoginPassword.error = "Password is required"
                etLoginPassword.requestFocus()
            }
            else -> {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to main activity
            }
        }
    }

    private fun performRegistration() {
        val name = etRegisterName.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()
        val confirmPassword = etRegisterConfirmPassword.text.toString().trim()

        when {
            name.isEmpty() -> {
                etRegisterName.error = "Name is required"
                etRegisterName.requestFocus()
            }
            email.isEmpty() -> {
                etRegisterEmail.error = "Email is required"
                etRegisterEmail.requestFocus()
            }
            password.isEmpty() -> {
                etRegisterPassword.error = "Password is required"
                etRegisterPassword.requestFocus()
            }
            password.length < 6 -> {
                etRegisterPassword.error = "Password must be at least 6 characters"
                etRegisterPassword.requestFocus()
            }
            password != confirmPassword -> {
                etRegisterConfirmPassword.error = "Passwords do not match"
                etRegisterConfirmPassword.requestFocus()
            }
            else -> {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                // Navigate to main activity
            }
        }
    }

    private fun handleForgotPassword() {
        Toast.makeText(this, "Reset password link sent", Toast.LENGTH_SHORT).show()
    }

    private fun handleGoogleSignIn() {
        Toast.makeText(this, "Google Sign-In", Toast.LENGTH_SHORT).show()
    }
}