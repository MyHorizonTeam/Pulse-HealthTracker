package com.example.pulse_healthtracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderManager {

    fun setReminder(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicineId", medicine.medicineId)
            putExtra("pillName", medicine.pillName)
            putExtra("dose", medicine.dose.toString())
            putExtra("time", medicine.time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicine.medicineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val calendar = Calendar.getInstance()
        val timeParts = medicine.time.split(" ", ":")
        if (timeParts.size >= 3) {
            var hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val amPm = timeParts[2]

            if ((amPm.equals("PM", ignoreCase = true)) && (hour < 12)) hour += 12
            if ((amPm.equals("AM", ignoreCase = true)) && (hour == 12)) hour = 0

            calendar.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            // If time has already passed today, set for tomorrow
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent,
                )
            } catch (_: SecurityException) {
                // For apps targeting Android 12+, SCHEDULE_EXACT_ALARM might need to be checked
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent,
                )
            }
        }
    }

    fun cancelReminder(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicine.medicineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)
    }
}
