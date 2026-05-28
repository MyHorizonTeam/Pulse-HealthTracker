package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

class HomeActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var etSearch: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressCount: TextView
    private lateinit var btnAdd: LinearLayout

    // Date TextViews
    private lateinit var tvDate02: TextView
    private lateinit var tvDate03: TextView
    private lateinit var tvDate04: TextView
    private lateinit var tvDate05: TextView
    private lateinit var tvDate06: TextView

    // Medicine Cards
    private lateinit var card1: CardView
    private lateinit var card2: CardView
    private lateinit var card3: CardView

    // Toggles
    private lateinit var ivToggle1: ImageView
    private lateinit var ivToggle2: ImageView
    private lateinit var ivToggle3: ImageView

    // Status TextViews
    private lateinit var tvStatus1: TextView
    private lateinit var tvStatus2: TextView
    private lateinit var tvStatus3: TextView

    // Indicators
    private lateinit var indicator1: View
    private lateinit var indicator2: View
    private lateinit var indicator3: View

    // Selected date
    private var selectedDate = "04"

    // ─── Data Model ──────────────────────────────────────────────────────────
    data class Medicine(
        val name: String,
        val dosage: String,
        val time: String,
        val date: String,
        var isTaken: Boolean
    )

    private val allMedicines = mutableListOf<Medicine>()
    private val filteredMedicines = mutableListOf<Medicine>()

    // ─── onCreate ────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupMedicineData()
        setupDateSelection()
        setupSearch()
        setupToggleButtons()
        setupAddButton()
        updateProgressBar()
    }

    // ─── 1. Init Views ───────────────────────────────────────────────────────
    private fun initViews() {
        etSearch        = findViewById(R.id.etSearch)
        progressBar     = findViewById(R.id.progressBar)
        tvProgressCount = findViewById(R.id.tvProgressCount)
        btnAdd          = findViewById(R.id.btnAdd)

        tvDate02 = findViewById(R.id.tvDate02)
        tvDate03 = findViewById(R.id.tvDate03)
        tvDate04 = findViewById(R.id.tvDate04)
        tvDate05 = findViewById(R.id.tvDate05)
        tvDate06 = findViewById(R.id.tvDate06)

        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)

        ivToggle1 = findViewById(R.id.ivToggle1)
        ivToggle2 = findViewById(R.id.ivToggle2)
        ivToggle3 = findViewById(R.id.ivToggle3)

        tvStatus1 = findViewById(R.id.tvStatus1)
        tvStatus2 = findViewById(R.id.tvStatus2)
        tvStatus3 = findViewById(R.id.tvStatus3)

        indicator1 = findViewById(R.id.indicator1)
        indicator2 = findViewById(R.id.indicator2)
        indicator3 = findViewById(R.id.indicator3)
    }

    // ─── 2. Medicine Data ──────────────────────────────────────────────────
    private fun setupMedicineData() {
        allMedicines.addAll(listOf(
            Medicine("Paracetamol", "2 pills · After food", "09 AM", "04", true),
            Medicine("Vitamin A",   "2 pills · After food", "11 AM", "04", false),
            Medicine("Vitamin C",   "2 pills · After food", "08 AM", "04", true),
            Medicine("Aspirin",     "1 pill · Before food", "08 AM", "02", true),
            Medicine("Omega 3",     "1 pill · After food",  "09 AM", "03", false)
        ))
        filterByDate(selectedDate)
    }

    // ─── 3. Date Selection ───────────────────────────────────────────────────
    private fun setupDateSelection() {
        val dates = listOf(tvDate02, tvDate03, tvDate04, tvDate05, tvDate06)
        val vals  = listOf("02", "03", "04", "05", "06")

        dates.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                selectedDate = vals[index]
                highlightDate(dates, index)
                filterByDate(selectedDate)
                filterBySearch(etSearch.text.toString())
            }
        }

        // Default highlight — date 04
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

    private fun filterByDate(date: String) {
        filteredMedicines.clear()
        filteredMedicines.addAll(allMedicines.filter { it.date == date })
        renderMedicines(filteredMedicines)
    }

    // ─── 4. Search ───────────────────────────────────────────────────────────
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                filterBySearch(s.toString())
            }
        })
    }

    private fun filterBySearch(query: String) {
        val list = if (query.isEmpty()) filteredMedicines
        else filteredMedicines.filter {
            it.name.contains(query, ignoreCase = true)
        }
        renderMedicines(list)
    }

    // ─── 5. Render Medicines ───────────────────────────────────────────────
    private fun renderMedicines(list: List<Medicine>) {
        val cards      = listOf(card1, card2, card3)
        val toggles    = listOf(ivToggle1, ivToggle2, ivToggle3)
        val statuses   = listOf(tvStatus1, tvStatus2, tvStatus3)
        val indicators = listOf(indicator1, indicator2, indicator3)

        // Hide all cards first
        cards.forEach { it.visibility = View.GONE }

        list.take(3).forEachIndexed { i, med ->
            cards[i].visibility = View.VISIBLE

            // Status text + color
            if (med.isTaken) {
                statuses[i].text = "✓ Taken"
                statuses[i].setTextColor(0xFF4ECDC4.toInt())
            } else {
                statuses[i].text = "⏰ Pending"
                statuses[i].setTextColor(0xFFFFB347.toInt())
            }

            // Toggle icon
            toggles[i].setImageResource(
                if (med.isTaken) R.drawable.toggle_green
                else R.drawable.toggle_red
            )

            // Indicator color
            indicators[i].setBackgroundResource(
                if (med.isTaken) R.drawable.bg_indicator_green
                else R.drawable.bg_indicator_orange
            )
        }

        updateProgressBar()
    }

    // ─── 6. Toggle ON/OFF ────────────────────────────────────────────────────
    private fun setupToggleButtons() {
        listOf(ivToggle1, ivToggle2, ivToggle3).forEachIndexed { index, toggle ->
            toggle.setOnClickListener {
                if (index < filteredMedicines.size) {
                    filteredMedicines[index].isTaken = !filteredMedicines[index].isTaken
                    renderMedicines(filteredMedicines)
                }
            }
        }
    }

    // ─── 7. Progress Bar ─────────────────────────────────────────────────────
    private fun updateProgressBar() {
        val total = filteredMedicines.size
        val taken = filteredMedicines.count { it.isTaken }

        progressBar.max      = if (total == 0) 1 else total
        progressBar.progress = taken
        tvProgressCount.text = "$taken / $total taken"
    }

    // ─── 8. Add Button → New Page ────────────────────────────────────────────
    private fun setupAddButton() {
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddMedicineActivity::class.java)
            startActivity(intent)
        }
    }
}
