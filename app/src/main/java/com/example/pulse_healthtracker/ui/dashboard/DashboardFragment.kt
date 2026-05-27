package com.example.pulse_healthtracker.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R
import com.example.pulse_healthtracker.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Navigation to Find Doctors
        binding.cardBookAppointment.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_findDoctors)
        }

        binding.btnSearchDoctors.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_findDoctors)
        }

        // Open Bing Maps for Locate Pharmacy
        binding.cardLocatePharmacy.setOnClickListener {
            val url = "https://www.bing.com/maps/search?FORM=HDRSC6&style=r&q=pharmasy+srilanka&cp=6.903800%7E79.931328&lvl=10.1"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
