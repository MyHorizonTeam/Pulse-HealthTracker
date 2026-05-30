package com.example.pulse_healthtracker

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskChanged: (Task) -> Unit,
    private val onTaskDeleted: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbTask: CheckBox = view.findViewById(R.id.cbTask)
        val tvTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTaskTime)
        val tvDesc: TextView = view.findViewById(R.id.tvTaskDesc)
        val viewColor: View = view.findViewById(R.id.viewColorTag)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteTask)
        val ivEdit: ImageView = view.findViewById(R.id.ivEditTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvTitle.text = task.title
        holder.tvTime.text = task.time
        holder.tvDesc.text = task.description
        
        try {
            val colorInt = task.color.toColorInt()
            holder.viewColor.setBackgroundColor(colorInt)
            holder.cbTask.buttonTintList = android.content.res.ColorStateList.valueOf(colorInt)
        } catch (_: Exception) {
            holder.viewColor.setBackgroundColor(Color.RED)
        }
        
        holder.cbTask.setOnCheckedChangeListener(null)
        holder.cbTask.isChecked = task.isCompleted
        updateTextStyle(holder.tvTitle, task.isCompleted)

        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            updateTextStyle(holder.tvTitle, isChecked)
            onTaskChanged(task)
        }

        holder.ivDelete.setOnClickListener {
            onTaskDeleted(holder.bindingAdapterPosition)
        }

        holder.ivEdit.setOnClickListener {
            showRenameDialog(holder.itemView.context, task, holder.bindingAdapterPosition)
        }
    }

    private fun showRenameDialog(context: android.content.Context, task: Task, position: Int) {
        val editText = EditText(context)
        editText.setText(task.title)
        
        AlertDialog.Builder(context)
            .setTitle("Rename Task")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    task.title = newTitle
                    notifyItemChanged(position)
                    onTaskChanged(task)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTextStyle(textView: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.alpha = 0.5f
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.alpha = 1.0f
        }
    }

    override fun getItemCount() = tasks.size
}