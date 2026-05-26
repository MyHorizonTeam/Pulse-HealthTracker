package com.example.pulse_healthtracker.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R
import com.google.android.material.button.MaterialButton

class RegisterAppointmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_appointment, container, false)

        // Back Button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Confirm Button
        view.findViewById<MaterialButton>(R.id.btnConfirm).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_success)
        }

        return view
    }
}