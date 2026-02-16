package com.example.moviereview.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.moviereview.R
import com.example.moviereview.databinding.FragmentProfileBinding
import com.example.moviereview.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    // Key for saving the name in SharedPreferences
    companion object {
        private const val PREF_USER_NAME = "pref_user_name"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load saved name (or default to "User")
        loadUserName()

        // Observe Data for stats (same as before)
        viewModel.allMovies.observe(viewLifecycleOwner) { movies ->
            binding.tvStatTotalCount.text = movies.size.toString()
            val favCount = movies.count { it.isFavorite }
            binding.tvStatFavCount.text = favCount.toString()
            if (movies.isNotEmpty()) {
                val avg = movies.map { it.score }.average()
                binding.tvStatAvgScore.text =
                    String.format(Locale.getDefault(), "%.1f", avg)
            } else {
                binding.tvStatAvgScore.text = "0.0"
            }
        }

        // Handle Favorites click
        binding.cardFavorites.setOnClickListener {
            val action = ProfileFragmentDirections.Companion.actionProfileFragmentToFavoritesFragment()
            findNavController().navigate(action)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        // Handle Click on Name to Edit
        binding.tvUserName.setOnClickListener {
            showEditNameDialog()
        }
    }

    private fun loadUserName() {
        // Get SharedPreferences (a simple way to save small data)
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        // Read the name, default to "User" if nothing saved yet
        val savedName = sharedPref.getString(PREF_USER_NAME, "User")
        binding.tvUserName.text = savedName
    }

    private fun showEditNameDialog() {
        // Inflate the Custom Layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)

        // Build the Dialog
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = builder.create()

        // Make background transparent so rounded corners show correctly
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Setup View Logic
        val etName = dialogView.findViewById<EditText>(R.id.et_dialog_name)
        val btnCancel = dialogView.findViewById<View>(R.id.btn_cancel)
        val btnSave = dialogView.findViewById<View>(R.id.btn_save)

        // Pre-fill current name
        etName.setText(binding.tvUserName.text)
        // Move cursor to end of text
        etName.setSelection(etName.text.length)

        // Cancel Button
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Save Button
        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()
            if (newName.isNotEmpty()) {
                binding.tvUserName.text = newName
                saveUserName(newName)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveUserName(newName: String) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        sharedPref.edit {
            putString(PREF_USER_NAME, newName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}