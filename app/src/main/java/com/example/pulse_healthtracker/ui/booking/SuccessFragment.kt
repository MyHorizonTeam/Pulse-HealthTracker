package com.example.pulse_healthtracker.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pulse_healthtracker.R
import com.google.android.material.button.MaterialButton

class SuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_success, container, false)

        // Done Button - Go back to Dashboard or Home
        view.findViewById<MaterialButton>(R.id.btnDone).setOnClickListener {
            // Go back to first screen (Dashboard)
            requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        // Optional: Edit Appointment
        view.findViewById<View>(R.id.tvEditAppointment).setOnClickListener {
            // You can handle edit logic here later
            requireActivity().onBackPressed()
        }

        return view
    }
}