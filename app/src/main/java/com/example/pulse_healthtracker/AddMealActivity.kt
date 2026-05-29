package com.example.pulse_healthtracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class AddMealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_meal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnSaveMeal = findViewById<Button>(R.id.btnSaveMeal)

        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)

        // DATE PICKER
        etDate.setOnTouchListener { _, event ->

            val DRAWABLE_END = 2

            if (event.action == MotionEvent.ACTION_UP) {

                if (event.rawX >= (
                            etDate.right -
                                    etDate.compoundDrawables[DRAWABLE_END].bounds.width()
                            )
                ) {

                    val calendar = Calendar.getInstance()

                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        this,
                        { _, selectedYear, selectedMonth, selectedDay ->

                            val date =
                                "${selectedMonth + 1}/$selectedDay/$selectedYear"

                            etDate.setText(date)

                        },
                        year,
                        month,
                        day
                    )

                    datePickerDialog.show()

                    return@setOnTouchListener true
                }
            }

            false
        }

        // TIME PICKER
        etTime.setOnTouchListener { _, event ->

            val DRAWABLE_END = 2

            if (event.action == MotionEvent.ACTION_UP) {

                if (event.rawX >= (
                            etTime.right -
                                    etTime.compoundDrawables[DRAWABLE_END].bounds.width()
                            )
                ) {

                    val calendar = Calendar.getInstance()

                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        this,
                        { _, selectedHour, selectedMinute ->

                            val amPm =
                                if (selectedHour >= 12) "PM" else "AM"

                            val hourFormatted =
                                if (selectedHour > 12)
                                    selectedHour - 12
                                else if (selectedHour == 0)
                                    12
                                else
                                    selectedHour

                            val minuteFormatted =
                                String.format("%02d", selectedMinute)

                            val time =
                                "$hourFormatted:$minuteFormatted $amPm"

                            etTime.setText(time)

                        },
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()

                    return@setOnTouchListener true
                }
            }

            false
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnSaveMeal.setOnClickListener {

            Toast.makeText(
                this,
                "Saving Meal & Redirecting...",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        val btnAddFoodItem = findViewById<Button>(R.id.btnAddFoodItem)
        val foodContainer = findViewById<LinearLayout>(R.id.foodContainer)

        btnAddFoodItem.setOnClickListener {

            val foodItemView = layoutInflater.inflate(R.layout.item_food, null)

            foodContainer.addView(foodItemView)
        }
    }
}