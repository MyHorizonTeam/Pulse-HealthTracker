package com.example.pulse_healthtracker.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R

class ChooseDateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_date, container, false)

        // Back Button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Navigate to Register Appointment on date selection
        val onDateSelected = View.OnClickListener {
            findNavController().navigate(R.id.registerAppointmentFragment)
        }

        view.findViewById<View>(R.id.btnToday).setOnClickListener(onDateSelected)
        view.findViewById<View>(R.id.btnTomorrow).setOnClickListener(onDateSelected)
        view.findViewById<View>(R.id.btnNext).setOnClickListener(onDateSelected)

        // Contact Clinic Button - Also navigates to Register Appointment
        view.findViewById<View>(R.id.btnContactClinic).setOnClickListener {
            findNavController().navigate(R.id.registerAppointmentFragment)
        }

        return view
    }
}
