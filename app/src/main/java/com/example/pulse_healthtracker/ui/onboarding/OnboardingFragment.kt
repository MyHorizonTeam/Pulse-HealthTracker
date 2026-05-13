package com.example.pulse_healthtracker.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pulse_healthtracker.R
import com.example.pulse_healthtracker.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        // NEXT Button Click
        binding.btnNext.setOnClickListener {
            goToDashboard()
        }

        // Skip Button Click
        binding.tvSkip.setOnClickListener {
            goToDashboard()
        }

        return binding.root
    }

    private fun goToDashboard() {
        findNavController().navigate(R.id.action_onboarding_to_dashboard)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
