package com.example.pulse_healthtracker

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID


class ProfilePg : AppCompatActivity() {
    private lateinit var lgOut: Button
    private lateinit var userName: TextView
    private lateinit var profilePic: ImageView
    private lateinit var emailText: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    // Define navigation button bottom
    lateinit var mediSwitch: MaterialButton
    lateinit var actSwitch: MaterialButton
    lateinit var nutSwitch: MaterialButton
    lateinit var docSwitch: MaterialButton
    lateinit var mentalSwitch: MaterialButton
    lateinit var userSwitch: MaterialButton


    private var selectedImageUri: Uri? = null
    private val pickImageRequest = 1
    private val ucropRequest = UCrop.REQUEST_CROP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_pg)

        // Firebase init
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_pics")
        database = FirebaseDatabase.getInstance().reference

        // Views
        profilePic = findViewById(R.id.profile_pic)
        progressBar = findViewById(R.id.progressBarL)
        lgOut = findViewById(R.id.lgOut)
        userName = findViewById(R.id.userName)
        emailText = findViewById(R.id.email_1)

        // Load existing profile image
        loadProfilePicture()

        profilePic.setOnClickListener { showImagePickerDialog() }

        mediSwitch = findViewById(R.id.medi_switch)
        actSwitch = findViewById(R.id.act_switch)
        nutSwitch = findViewById(R.id.nut_switch)
        mentalSwitch = findViewById(R.id.mental_switch)
        userSwitch = findViewById(R.id.user_switch)
        docSwitch = findViewById(R.id.doc_switch)

        // Navigation button function
        val toggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.navgrp)

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.medi_switch -> {
                        // Navigate to Medication page
                        val intent = Intent(this, HomeMed::class.java)
                        startActivity(intent)
                    }
                    R.id.user_switch -> {
                        // Navigate to Profile page
                        val intent = Intent(this, ProfilePg::class.java)
                        startActivity(intent)
                    }
                    R.id.doc_switch->{
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.mental_switch->{
                        val intent = Intent(this, HomeMJActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.nut_switch->{
                        val intent = Intent(this, DashboardActivity_Nut::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
        val btnEdtpf = findViewById<Button>(R.id.edtpf)
        btnEdtpf.setOnClickListener {
            val intent= Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.goal)
        // Need to create page

        val btnSet = findViewById<Button>(R.id.settings)
        btnSet.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        val btnPri = findViewById<Button>(R.id.privacy)
        btnPri.setOnClickListener {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }


        lgOut.setOnClickListener {
            lgOut.isEnabled = false
            lgOut.text = getString(R.string.signing_out)
            signOut()
        }

        // Fill name/email if user present
        val user = auth.currentUser
        user?.let {
            userName.text = it.displayName ?: getString(R.string.user_Name)
            emailText.text = it.email ?: getString(R.string.pf_email)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        AlertDialog.Builder(this)
            .setTitle("Select Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, pickImageRequest)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open camera: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        @Suppress("DEPRECATION")
        startActivityForResult(intent, pickImageRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == pickImageRequest) && (resultCode == RESULT_OK)) {
            val uriFromGallery = data?.data
            if (uriFromGallery != null) {
                startCrop(uriFromGallery)
            } else {
                @Suppress("DEPRECATION")
                val bitmap = data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    val tempUri = saveBitmapToCache(bitmap)
                    if (tempUri != null) startCrop(tempUri) else
                        Toast.makeText(this, "Failed to save camera image", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == ucropRequest) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                val resultUri = UCrop.getOutput(data)
                if (resultUri != null) {
                    selectedImageUri = resultUri
                    uploadImageToFirebase()
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = data?.let { UCrop.getError(it) }
                Toast.makeText(this, "Crop failed: ${cropError?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${UUID.randomUUID()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setHideBottomControls(false)
            setFreeStyleCropEnabled(false)
        }
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(800, 800)
            .withOptions(options)
            .start(this)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        val file = File(cacheDir, "camera_${UUID.randomUUID()}.jpg")
        var fos: FileOutputStream? = null
        return try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            Uri.fromFile(file)
        } catch (_: IOException) {
            null
        } finally {
            try {
                fos?.close()
            } catch (_: IOException) {
            }
        }
    }

    private fun uploadImageToFirebase() {
        val uri = selectedImageUri ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "No authenticated user", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        profilePic.isEnabled = false

        val fileRef = storageRef.child("$userId/${UUID.randomUUID()}.jpg")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    saveImageUrlToDatabase(userId, imageUrl)
                }.addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    profilePic.isEnabled = true
                    Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                profilePic.isEnabled = true
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUrlToDatabase(userId: String, imageUrl: String) {
        database.child("users").child(userId).child("profilePicUrl")
            .setValue(imageUrl)
            .addOnSuccessListener {
                loadProfilePicture()
                progressBar.visibility = View.GONE
                profilePic.isEnabled = true
                Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                profilePic.isEnabled = true
                Toast.makeText(this, "Failed to save URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfilePicture() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("profilePicUrl")
            .get()
            .addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(profilePic)
                } else {
                    profilePic.setImageResource(R.drawable.default_avatar)
                }
            }
            .addOnFailureListener {
                profilePic.setImageResource(R.drawable.default_avatar)
            }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { task ->
                lgOut.isEnabled = true
                lgOut.text = getString(R.string.log_out_btn)
                if (task.isSuccessful) {
                    clearUserSession()
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Sign out failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun clearUserSession() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit { clear() }
    }
}