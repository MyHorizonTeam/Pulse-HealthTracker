package com.example.pulse_healthtracker

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Home_MJ : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView

    private val tasks = mutableListOf(
        Task(title = "Wake up early", description = "Start your day with energy", time = "06:00 AM", color = "#FF5252"),
        Task(title = "Drink 3L water", description = "Stay hydrated", time = "08:00 AM", color = "#448AFF"),
        Task(title = "Exercise 30 min", description = "Workout", time = "07:00 AM", color = "#4CAF50"),
        Task(title = "Eat healthy", description = "Balanced meal", time = "09:00 AM", color = "#FF9800"),
        Task(title = "Meditation for mind", description = "Relax", time = "10:00 PM", color = "#9C27B0"),
        Task(title = "Read a book", description = "Knowledge", time = "11:00 PM", color = "#00BCD4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_mj)

        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)

        adapter = TaskAdapter(tasks,
            onTaskChanged = { updateProgress() },
            onTaskDeleted = { position ->
                tasks.removeAt(position)
                adapter.notifyItemRemoved(position)
                updateProgress()
            }
        )

        val rv = findViewById<RecyclerView>(R.id.rvTasks)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val etAddTask = findViewById<EditText>(R.id.etAddTask)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddTask)
            .setOnClickListener {
                addCustomTask(etAddTask)
            }

        etAddTask.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCustomTask(etAddTask)
                true
            } else false
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fabAddTask
        ).setOnClickListener {
            etAddTask.requestFocus()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cardArticles).setOnClickListener {
            startActivity(android.content.Intent(this, ArticlesActivity::class.java))
        }

        updateProgress()
    }

    private fun addCustomTask(et: EditText) {
        val text = et.text.toString().trim()
        if (text.isNotEmpty()) {
            val newTask = Task(title = text, description = "Custom task", time = "Now", color = "#7E57C2")
            tasks.add(newTask)
            adapter.notifyItemInserted(tasks.size - 1)
            updateProgress()
            et.text.clear()
        }
    }

    private fun updateProgress() {
        val done = tasks.count { it.isCompleted }
        val total = tasks.size
        val pct = if (total > 0) (done * 100) / total else 0
        progressBar.progress = pct
        tvProgress.text = getString(R.string.tasks_done_format, done, total)
    }
}