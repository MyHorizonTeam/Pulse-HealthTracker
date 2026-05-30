package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeMed : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressCount: TextView
    private lateinit var btnAdd: LinearLayout
    private lateinit var rvMedicines: RecyclerView

    private lateinit var tvDate02: TextView
    private lateinit var tvDate03: TextView
    private lateinit var tvDate04: TextView
    private lateinit var tvDate05: TextView
    private lateinit var tvDate06: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var medicineListener: ListenerRegistration? = null

    private lateinit var medicineAdapter: MedicineAdapter
    private val allMedicines = mutableListOf<Medicine>()
    private val displayedMedicines = mutableListOf<Medicine>()

    private var selectedDate = "04"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_med)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeFirebase()
        initViews()
        setupRecyclerView()
        setupDateSelection()
        setupSearch()
        setupAddButton()
        fetchMedicines()
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar)
        tvProgressCount = findViewById(R.id.tvProgressCount)
        btnAdd = findViewById(R.id.btnAdd)
        rvMedicines = findViewById(R.id.rvMedicines)

        tvDate02 = findViewById(R.id.tvDate02)
        tvDate03 = findViewById(R.id.tvDate03)
        tvDate04 = findViewById(R.id.tvDate04)
        tvDate05 = findViewById(R.id.tvDate05)
        tvDate06 = findViewById(R.id.tvDate06)
        
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<CardView>(R.id.cvProfile).setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        medicineAdapter = MedicineAdapter(
            displayedMedicines,
            onToggleClick = { toggleMedicineStatus(it) },
            onEditClick = { editMedicine(it) },
            onDeleteClick = { deleteMedicine(it) }
        )
        rvMedicines.layoutManager = LinearLayoutManager(this)
        rvMedicines.adapter = medicineAdapter
    }

    private fun editMedicine(medicine: Medicine) {
        val intent = Intent(this, AddMedicineActivity::class.java)
        intent.putExtra("medicineId", medicine.medicineId)
        startActivity(intent)
    }

    private fun deleteMedicine(medicine: Medicine) {
        AlertDialog.Builder(this)
            .setTitle("Delete Medicine")
            .setMessage("Are you sure you want to delete ${medicine.pillName}?")
            .setPositiveButton("Delete") { _, _ ->
                firestore.collection("medications").document(medicine.medicineId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupDateSelection() {
        val dates = listOf(tvDate02, tvDate03, tvDate04, tvDate05, tvDate06)
        val vals = listOf("02", "03", "04", "05", "06")

        dates.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectedDate = vals[index]
                highlightDate(dates, index)
            }
        }
        highlightDate(dates, 2)
    }

    private fun highlightDate(dates: List<TextView>, activeIndex: Int) {
        dates.forEachIndexed { index, tv ->
            if (index == activeIndex) {
                tv.setBackgroundResource(R.drawable.bg_selected_date)
                tv.setTextColor(0xFFFFFFFF.toInt())
            } else {
                tv.background = null
                tv.setTextColor(0xFF555577.toInt())
            }
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                filterMedicines(s.toString())
            }
        })
    }

    private fun setupAddButton() {
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddMedicineActivity::class.java))
        }
    }

    private fun fetchMedicines() {
        val userId = auth.currentUser?.uid ?: return

        // Fetching without ordering for maximum speed
        medicineListener = firestore.collection("medications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    allMedicines.clear()
                    for (doc in snapshots) {
                        val medicine = doc.toObject(Medicine::class.java)
                        medicine.medicineId = doc.id
                        allMedicines.add(medicine)
                    }
                    // Sort locally to avoid needing a Firestore Index (which is slow)
                    allMedicines.sortByDescending { it.createdAt }
                    filterMedicines(etSearch.text.toString())
                }
            }
    }

    private fun filterMedicines(query: String) {
        displayedMedicines.clear()
        if (query.isEmpty()) {
            displayedMedicines.addAll(allMedicines)
        } else {
            displayedMedicines.addAll(allMedicines.filter {
                it.pillName.contains(query, ignoreCase = true)
            })
        }
        medicineAdapter.updateData(displayedMedicines)
        updateProgressBar()
    }

    private fun toggleMedicineStatus(medicine: Medicine) {
        if (medicine.medicineId.isEmpty()) return
        val newStatus = !medicine.isTaken
        firestore.collection("medications").document(medicine.medicineId)
            .update("isTaken", newStatus)
    }

    private fun updateProgressBar() {
        val total = displayedMedicines.size
        val taken = displayedMedicines.count { it.isTaken }
        progressBar.max = if (total == 0) 1 else total
        progressBar.progress = taken
        tvProgressCount.text = "$taken / $total taken"
    }

    override fun onDestroy() {
        super.onDestroy()
        medicineListener?.remove()
    }
}
