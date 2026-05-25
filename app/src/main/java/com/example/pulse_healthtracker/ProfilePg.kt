package com.example.pulse_healthtracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import android.widget.*
import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.app.Activity.RESULT_OK
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.installations
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID
import android.provider.MediaStore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.SetOptions

class ProfilePg : AppCompatActivity() {
    private lateinit var lgOut: Button
    public lateinit var userName: TextView
    public lateinit var profile_pic : ImageView
    private lateinit var email_1 : TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var database: DatabaseReference

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    // Main function start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_pg)

        // Initialize firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("profile_pic")
        database = FirebaseDatabase.getInstance().reference

        // Initialize view
        profile_pic = findViewById(R.id.profile_pic)
        progressBar = findViewById(R.id.progressBarL)

        loadProfilePicture()

        profile_pic.setOnClickListener {
            showImagePickerDialog()
        }


        lgOut = findViewById(R.id.lgOut)
        lgOut.isEnabled = false
        lgOut.text = "Signing out..."
        lgOut.setOnClickListener {
            signOut()
        }
        val user = Firebase.auth.currentUser
        if (user !=null){
            userName = findViewById(R.id.userName)
            profile_pic = findViewById(R.id.profile_pic)
            email_1 = findViewById(R.id.email_1)

            userName.text = user.displayName
            email_1.text = user.email
            if (user.photoUrl != null) {
                // Load the profile picture using an image loading library like Glide or Picasso
                // For example, using Glide:
                // Glide.with(this).load(user.photoUrl).into(profile_pic)
            }
            else {

            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showImagePickerDialog(){
        val options = arrayOf("Take Photo","Choose from Gallery","Cancel")
        AlertDialog.Builder(this).setTitle("Select Profile Picture")
            .setItems(options){_,which ->
                when(which){
                    0 -> openCamera()
                    1 -> openGallery()

                }
            }
            .show()
    }
    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode,resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            uploadImageToFirebase()
        }
    }
    private fun uploadImageToFirebase(){
        if(selectedImageUri == null) return

        //Show progress
        progressBar.visibility = android.view.View.VISIBLE
        profile_pic.isEnabled = false

        val userId = auth.currentUser?.uid ?: return
        val fileRef = storageRef.child("$userId/${UUID.randomUUID()}.jpg")

        fileRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveImageUrlToDatabase(userId, imageUrl)
                }
            }

            .addOnFailureListener{ exception ->
                progressBar.visibility = android.view.View.GONE
                profile_pic.isEnabled = true
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveImageUrlToDatabase(userId: String, imageUrl: String) {
        // Save to Firestore
        database.child("users").child(userId).child("profilePicUrl").setValue(imageUrl).addOnSuccessListener {
            loadProfilePicture()
            progressBar.visibility = android.view.View.GONE
            profile_pic.isEnabled = true
            Toast.makeText(this, "Profile picture updated!",Toast.LENGTH_SHORT).show()
        }

            .addOnFailureListener { e ->
                progressBar.visibility = android.view.View.GONE
                profile_pic.isEnabled = true
                Toast.makeText(this, "Failed to save URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfilePicture() {
        val userId = auth.currentUser?.uid ?: return
        /*
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val imageUrl = document.getString("profilePicUrl")
                if (!imageUrl.isNullOrEmpty()) {
                    // Load image using Glide
                    Glide.with(this)
                        .load(imageUrl)
                        .circleCrop()  // Makes it circular
                        .placeholder(R.drawable.default_avatar)  // While loading
                        .error(R.drawable.default_avatar)  // If error
                        .into(profile_pic)
                } else {
                    // Set default avatar
                    profile_pic.setImageResource(R.drawable.default_avatar)
                }
            }
            .addOnFailureListener {
                profile_pic.setImageResource(R.drawable.default_avatar)
            } */
        database.child("users").child(userId).child("profilePicUrl")
            .get()
            .addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(imageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.default_avatar)
                        .into(profile_pic)
                }
            }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener { task ->
                lgOut = findViewById(R.id.lgOut)
                lgOut.isEnabled = true
                lgOut.text = "Log out"
                if (task.isSuccessful) {
                    // Clear any saved user data
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
        // Clear SharedPreferences if you're storing user data
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}