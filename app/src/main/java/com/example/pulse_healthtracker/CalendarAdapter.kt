package com.example.pulse_healthtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarAdapter(
    private var dates: List<CalendarDate>,
    private val onDateSelected: (CalendarDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDateNumber: TextView = view.findViewById(R.id.tvDateNumber)
        val container: View = view.findViewById(R.id.dateItemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        
        // Dynamically set width to exactly 1/7th of the RecyclerView's width
        parent.post {
            val parentWidth = parent.width
            if (parentWidth > 0) {
                val itemWidth = parentWidth / 7
                view.layoutParams.width = itemWidth
                view.requestLayout()
            }
        }
        
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendarDate = dates[position]
        val date = calendarDate.date

        if (date == null) {
            holder.tvDateNumber.text = ""
            holder.tvDateNumber.background = null
            holder.container.isClickable = false
            return
        }

        holder.container.isClickable = true
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        holder.tvDateNumber.text = dateFormat.format(date)

        when {
            calendarDate.isSelected -> {
                holder.tvDateNumber.setBackgroundResource(R.drawable.bg_selected_date)
                holder.tvDateNumber.setTextColor(0xFFFFFFFF.toInt())
                holder.tvDateNumber.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            calendarDate.isToday -> {
                // Today highlighted with a border/background circle border
                holder.tvDateNumber.setBackgroundResource(R.drawable.circle_border)
                holder.tvDateNumber.setTextColor(0xFF0A0E63.toInt())
                holder.tvDateNumber.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            else -> {
                holder.tvDateNumber.background = null
                holder.tvDateNumber.setTextColor(0xFF555577.toInt())
                holder.tvDateNumber.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }

        holder.container.setOnClickListener {
            onDateSelected(calendarDate)
        }
    }

    override fun getItemCount() = dates.size

    fun updateData(newDates: List<CalendarDate>) {
        dates = newDates
        notifyDataSetChanged()
    }
}
