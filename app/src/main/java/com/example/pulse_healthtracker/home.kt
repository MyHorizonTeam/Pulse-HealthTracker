package com.example.pulse_healthtracker

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class home : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)

        setupRecyclerView()
        setupPredefinedTasks()
        setupInputs()
        updateProgress()
    }

    private fun setupRecyclerView() {
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        taskAdapter = TaskAdapter(taskList, 
            onTaskChanged = { updateProgress() },
            onTaskDeleted = { position ->
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                updateProgress()
                Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show()
            }
        )
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = taskAdapter
    }

    private fun setupPredefinedTasks() {
        taskList.add(Task(1, "Wake up early", "Start your day with energy", "06:00 AM", false, "#FF5252"))
        taskList.add(Task(2, "Drink 3L water", "Stay hydrated throughout the day", "08:00 AM", false, "#448AFF"))
        taskList.add(Task(3, "Exercise 30min", "Morning workout or jog", "07:00 AM", false, "#4CAF50"))
        taskList.add(Task(4, "Eat healthy", "Balanced breakfast and meals", "09:00 AM", false, "#FF9800"))
        taskList.add(Task(5, "Meditation for mind", "Calm your thoughts", "10:00 PM", false, "#9C27B0"))
        taskList.add(Task(6, "Read a book", "Finish at least 10 pages", "11:00 PM", false, "#00BCD4"))
        taskAdapter.notifyDataSetChanged()
    }

    private fun setupInputs() {
        findViewById<androidx.cardview.widget.CardView>(R.id.cardArticles).setOnClickListener {
            startActivity(Intent(this, ArticlesActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cardDeepTimer).setOnClickListener {
            openWebPage("https://www.deeptimer.io/?utm_source=chatgpt.com")
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.cardChatWithGPT).setOnClickListener {
            openWebPage("https://chatgpt.com/")
        }

        val etAddTask = findViewById<EditText>(R.id.etAddTask)
        val btnAddTask = findViewById<MaterialButton>(R.id.btnAddTask)

        btnAddTask.setOnClickListener {
            addCustomTask(etAddTask)
        }

        etAddTask.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCustomTask(etAddTask)
                true
            } else false
        }
    }

    private fun openWebPage(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(intent)
    }

    private fun addCustomTask(et: EditText) {
        val text = et.text.toString().trim()
        if (text.isNotEmpty()) {
            val newTask = Task(
                System.currentTimeMillis(),
                text,
                "Custom task",
                "Now",
                false,
                "#7E57C2"
            )
            taskList.add(newTask)
            taskAdapter.notifyItemInserted(taskList.size - 1)
            updateProgress()
            et.text.clear()
            findViewById<RecyclerView>(R.id.rvTasks).smoothScrollToPosition(taskList.size - 1)
        }
    }

    private fun updateProgress() {
        val done = taskList.count { it.isCompleted }
        val total = taskList.size
        val pct = if (total > 0) (done * 100) / total else 0
        progressBar.progress = pct
        tvProgress.text = "$done / $total tasks done"
    }
}