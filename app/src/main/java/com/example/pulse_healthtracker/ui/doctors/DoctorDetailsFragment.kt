package com.example.pulse_healthtracker.ui.doctors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R

class DoctorDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_details, container, false)

        // Back Button
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Book Now Button - Navigates to Choose Date
        view.findViewById<View>(R.id.btnBookNow).setOnClickListener {
            findNavController().navigate(R.id.chooseDateFragment)
        }

        // Nearby Pharmacy Button
        view.findViewById<View>(R.id.btnNearbyPharmacy).setOnClickListener {
            val url = "https://www.bing.com/maps/search?FORM=HDRSC6&style=r&q=pharmasy+srilanka&cp=6.903800%7E79.931328&lvl=10.1"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        return view
    }
}
