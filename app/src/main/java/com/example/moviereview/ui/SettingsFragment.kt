package com.example.moviereview.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moviereview.LocaleHelper
import com.example.moviereview.MainActivity
import com.example.moviereview.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnglish.setOnClickListener {
            setNewLocale("en")
        }

        binding.btnHebrew.setOnClickListener {
            setNewLocale("iw")
        }
    }

    private fun setNewLocale(language: String) {
        // Save new language
        LocaleHelper.setLocale(requireContext(), language)

        // Restart App to apply changes
        val intent = Intent(requireContext(), MainActivity::class.java)
        // Clear task to ensure a fresh start
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}