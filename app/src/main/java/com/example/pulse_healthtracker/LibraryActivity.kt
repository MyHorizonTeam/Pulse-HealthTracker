package com.example.pulse_healthtracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LibraryActivity : AppCompatActivity() {

    private lateinit var noteAdapter: NoteAdapter
    private val notesList = mutableListOf<String>()
    private lateinit var layoutSavedMoods: LinearLayout
    private lateinit var tvNoMoods: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        findViewById<View>(R.id.btnBackLibrary).setOnClickListener {
            finish()
        }

        layoutSavedMoods = findViewById(R.id.layoutSavedMoods)
        tvNoMoods = findViewById(R.id.tvNoMoods)

        loadNotes()
        setupStickyNotes()
        loadMoods()
    }

    private fun setupStickyNotes() {
        val rv = findViewById<RecyclerView>(R.id.rvStickyNotes)
        noteAdapter = NoteAdapter(notesList, 
            onEdit = { position -> showNoteDialog(position) },
            onDelete = { position -> 
                notesList.removeAt(position)
                noteAdapter.notifyItemRemoved(position)
                saveNotes()
            }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = noteAdapter

        findViewById<View>(R.id.btnAddNote).setOnClickListener {
            showNoteDialog(-1)
        }
    }

    private fun loadMoods() {
        val prefs = getSharedPreferences("PulseHealth", Context.MODE_PRIVATE)
        val savedSet = prefs.getStringSet("saved_moods", emptySet()) ?: emptySet()
        
        if (savedSet.isNotEmpty()) {
            tvNoMoods.visibility = View.GONE
            layoutSavedMoods.removeAllViews()
            
            // Sort by timestamp (descending)
            val sortedMoods = savedSet.toList().sortedByDescending { it.split("|")[0].toLong() }
            
            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            
            sortedMoods.forEach { entry ->
                val parts = entry.split("|")
                if (parts.size == 2) {
                    val time = sdf.format(Date(parts[0].toLong()))
                    val mood = parts[1]
                    
                    val tv = TextView(this)
                    tv.text = String.format("[%s] - %s", time, mood)
                    tv.setPadding(0, 8, 0, 8)
                    tv.setTextColor(resources.getColor(R.color.text_primary))
                    tv.textSize = 15f
                    layoutSavedMoods.addView(tv)
                }
            }
        }
    }

    private fun showNoteDialog(position: Int) {
        val editText = EditText(this)
        if (position != -1) editText.setText(notesList[position])
        
        AlertDialog.Builder(this)
            .setTitle(if (position == -1) "Add Sticky Note" else "Edit Note")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    if (position == -1) {
                        notesList.add(text)
                        noteAdapter.notifyItemInserted(notesList.size - 1)
                    } else {
                        notesList[position] = text
                        noteAdapter.notifyItemChanged(position)
                    }
                    saveNotes()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveNotes() {
        val prefs = getSharedPreferences("PulseHealth", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("sticky_notes", notesList.toSet()).apply()
    }

    private fun loadNotes() {
        val prefs = getSharedPreferences("PulseHealth", Context.MODE_PRIVATE)
        val savedSet = prefs.getStringSet("sticky_notes", emptySet()) ?: emptySet()
        notesList.clear()
        notesList.addAll(savedSet)
    }
}

class NoteAdapter(
    private val notes: List<String>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNote: TextView = view.findViewById(R.id.tvTaskTitle)
        val ivEdit: ImageView = view.findViewById(R.id.ivEditTask)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        v.findViewById<View>(R.id.cbTask).visibility = View.GONE
        v.findViewById<View>(R.id.tvTaskTime).visibility = View.GONE
        v.findViewById<View>(R.id.tvTaskDesc).visibility = View.GONE
        v.findViewById<View>(R.id.viewColorTag).visibility = View.GONE
        return NoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.tvNote.text = notes[position]
        holder.ivEdit.setOnClickListener { onEdit(holder.bindingAdapterPosition) }
        holder.ivDelete.setOnClickListener { onDelete(holder.bindingAdapterPosition) }
    }

    override fun getItemCount() = notes.size
}