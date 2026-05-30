package com.example.pulse_healthtracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class HomeMed : AppCompatActivity() {

    private lateinit var tvMonth: TextView
    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressCount: TextView
    private lateinit var btnAdd: LinearLayout
    private lateinit var rvMedicines: RecyclerView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var dayNamesHeader: LinearLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var medicineListener: ListenerRegistration? = null

    private lateinit var medicineAdapter: MedicineAdapter
    private val allMedicines = mutableListOf<Medicine>()
    private val displayedMedicines = mutableListOf<Medicine>()

    private lateinit var calendarAdapter: CalendarAdapter
    private val calendarDates = mutableListOf<CalendarDate>()
    private var currentMonthCalendar = Calendar.getInstance()
    private var selectedFullDate = ""

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
        setupRecyclerViews()
        setupDateSelection()
        setupSearch()
        setupAddButton()
        fetchMedicines()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun initViews() {
        tvMonth = findViewById(R.id.tvMonth)
        etSearch = findViewById(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar)
        tvProgressCount = findViewById(R.id.tvProgressCount)
        btnAdd = findViewById(R.id.btnAdd)
        rvMedicines = findViewById(R.id.rvMedicines)
        rvCalendar = findViewById(R.id.rvCalendar)
        dayNamesHeader = findViewById(R.id.dayNamesHeader)
        
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<CardView>(R.id.cvProfile).setOnClickListener {
            startActivity(Intent(this, ProfilePg::class.java))
        }

        tvMonth.setOnClickListener { showDatePicker() }
        updateMonthDisplay()
    }

    private fun updateMonthDisplay() {
        val format = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonth.text = format.format(currentMonthCalendar.time)
    }

    private fun showDatePicker() {
        val year = currentMonthCalendar[Calendar.YEAR]
        val month = currentMonthCalendar[Calendar.MONTH]
        val day = currentMonthCalendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, _ ->
                currentMonthCalendar.set(selectedYear, selectedMonth, 1)
                updateMonthDisplay()
                generateDatesForMonth()
            },
            year,
            month,
            day,
        )
        datePickerDialog.show()
    }

    private fun setupRecyclerViews() {
        // Medicine Adapter
        medicineAdapter = MedicineAdapter(
            displayedMedicines,
            onToggleClick = { toggleMedicineStatus(it) },
            onItemClick = { openEditMedicine(it) },
        ) { deleteMedicine(it) }
        rvMedicines.layoutManager = LinearLayoutManager(this)
        rvMedicines.adapter = medicineAdapter

        // Calendar Adapter
        calendarAdapter = CalendarAdapter(calendarDates) { dateObj ->
            onDateSelected(dateObj)
        }
        rvCalendar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCalendar.adapter = calendarAdapter
        
        // Use SnapHelper to ensure dates always align with stationary Mon-Sun headers
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(rvCalendar)
    }

    private fun setupDateSelection() {
        generateDatesForMonth()
    }

    private fun generateDatesForMonth() {
        calendarDates.clear()
        val tempCal = currentMonthCalendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        
        val firstDayOfWeek = tempCal[Calendar.DAY_OF_WEEK]
        val offset = when(firstDayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        repeat(offset) {
            calendarDates.add(CalendarDate(null, isSelected = false, isToday = false))
        }

        val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val today = Calendar.getInstance()
        var defaultSelectionIndex = -1

        repeat(maxDays) {
            val date = tempCal.time
            val isToday = (tempCal[Calendar.YEAR] == today[Calendar.YEAR] &&
                tempCal[Calendar.MONTH] == today[Calendar.MONTH] &&
                tempCal[Calendar.DAY_OF_MONTH] == today[Calendar.DAY_OF_MONTH])

            if (isToday) {
                defaultSelectionIndex = calendarDates.size
            }
            
            calendarDates.add(CalendarDate(date, isSelected = isToday, isToday = isToday))
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        while (calendarDates.size % 7 != 0) {
            calendarDates.add(CalendarDate(null, isSelected = false, isToday = false))
        }

        calendarAdapter.updateData(calendarDates.toList())
        
        if (defaultSelectionIndex != -1) {
            onDateSelected(calendarDates[defaultSelectionIndex])
            rvCalendar.post {
                rvCalendar.scrollToPosition((defaultSelectionIndex / 7) * 7)
            }
        } else {
            val firstValid = calendarDates.indexOfFirst { it.date != null }
            if (firstValid != -1) onDateSelected(calendarDates[firstValid])
            rvCalendar.post {
                rvCalendar.scrollToPosition(0)
            }
        }
    }

    private fun onDateSelected(selectedDateObj: CalendarDate) {
        if (selectedDateObj.date == null) return
        
        calendarDates.forEach { it.isSelected = it == selectedDateObj }
        calendarAdapter.notifyDataSetChanged()
        
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedFullDate = format.format(selectedDateObj.date)
        
        // Highlight corresponding stationary header
        val indexInList = calendarDates.indexOf(selectedDateObj)
        val column = indexInList % 7
        highlightDayHeader(column)
        
        filterMedicines(etSearch.text.toString())
    }

    private fun highlightDayHeader(columnIndex: Int) {
        dayNamesHeader.children.forEachIndexed { index, view ->
            if (view is TextView) {
                if (index == columnIndex) {
                    view.setTextColor(0xFF0A0E63.toInt())
                    view.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    view.setTextColor(0xFF8899BB.toInt())
                    view.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
        }
    }

    private fun openEditMedicine(medicine: Medicine) {
        val intent = Intent(this, EditMedicineActivity::class.java)
        intent.putExtra("medicineId", medicine.medicineId)
        startActivity(intent)
    }

    private fun deleteMedicine(medicine: Medicine) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_medicine_title)
            .setMessage(getString(R.string.delete_medicine_msg, medicine.pillName))
            .setPositiveButton(R.string.delete) { _, _ ->
                firestore.collection("medications").document(medicine.medicineId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, getString(R.string.failed, e.message), Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                    filterMedicines(s.toString())
                }
            },
        )
    }

    private fun setupAddButton() {
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddMedicineActivity::class.java)
            intent.putExtra("selectedDate", selectedFullDate)
            startActivity(intent)
        }
    }

    private fun fetchMedicines() {
        val userId = auth.currentUser?.uid ?: return

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
                    allMedicines.sortByDescending { it.createdAt }
                    filterMedicines(etSearch.text.toString())
                }
            }
    }

    private fun filterMedicines(query: String) {
        val filteredList = allMedicines.filter { medicine ->
            val matchesSearch = medicine.pillName.contains(query, ignoreCase = true)
            val matchesDate = medicine.date == selectedFullDate
            matchesSearch && matchesDate
        }
        
        displayedMedicines.clear()
        displayedMedicines.addAll(filteredList)
        medicineAdapter.updateData(displayedMedicines.toList())
        updateProgressBar()
    }

    private fun toggleMedicineStatus(medicine: Medicine) {
        if (medicine.medicineId.isEmpty()) return
        val newStatus = !medicine.isTaken
        firestore.collection("medications").document(medicine.medicineId)
            .update("isTaken", newStatus)
            .addOnSuccessListener {
                if (newStatus) {
                    ReminderManager.setReminder(this, medicine.copy(isTaken = newStatus))
                    Toast.makeText(this, "Reminder set for ${medicine.pillName}", Toast.LENGTH_SHORT).show()
                } else {
                    ReminderManager.cancelReminder(this, medicine)
                    Toast.makeText(this, "Reminder cancelled", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateProgressBar() {
        val total = displayedMedicines.size
        val taken = displayedMedicines.count { it.isTaken }
        progressBar.max = if (total == 0) 1 else total
        progressBar.progress = taken
        tvProgressCount.text = getString(R.string.tasks_done_format, taken, total)
    }

    override fun onDestroy() {
        super.onDestroy()
        medicineListener?.remove()
    }
}
