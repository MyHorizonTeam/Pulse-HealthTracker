package com.example.pulse_healthtracker.ui.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R

class FindDoctorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_doctors, container, false)

        // Back Button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Book Now Button for Dr. W. J. Abisha
        view.findViewById<View>(R.id.btnBookNow1).setOnClickListener {
            findNavController().navigate(R.id.action_findDoctors_to_doctorDetails)
        }

        // Optional: Book Now Button for Dr. Gowreeshan (same navigation for now)
        view.findViewById<View>(R.id.btnBookNow2).setOnClickListener {
            findNavController().navigate(R.id.action_findDoctors_to_doctorDetails)
        }

        return view
    }
}
