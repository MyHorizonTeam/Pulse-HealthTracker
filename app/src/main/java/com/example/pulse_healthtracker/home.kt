package com.example.pulse_healthtracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class home : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        setupRecyclerView()
        setupPredefinedTasks()
        setupFab()
    }

    private fun setupRecyclerView() {
        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        taskAdapter = TaskAdapter(taskList, 
            onTaskChanged = { task ->
                val status = if (task.isCompleted) "Completed" else "Pending"
                Toast.makeText(this, "${task.title} is $status", Toast.LENGTH_SHORT).show()
            },
            onTaskDeleted = { position ->
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
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

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fab.setOnClickListener {
            val newTask = Task(
                System.currentTimeMillis(),
                "New Task",
                "Description for the task",
                "12:00 PM",
                false,
                "#7E57C2"
            )
            taskList.add(newTask)
            taskAdapter.notifyItemInserted(taskList.size - 1)
            findViewById<RecyclerView>(R.id.rvTasks).smoothScrollToPosition(taskList.size - 1)
        }
    }
}