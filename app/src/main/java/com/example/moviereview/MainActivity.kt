package com.example.moviereview

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.moviereview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup Manual Clicks (Home & Favorites)
        binding.btnHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            }
        }

        binding.btnProfile.setOnClickListener {
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment)
            }
        }

        // Setup Global FAB Click
        binding.fabAdd.setOnClickListener {
            if (navController.currentDestination?.id != R.id.addEditMovieFragment) {
                val action = NavGraphDirections.actionGlobalToAddEditMovieFragment(movieId = -1)
                navController.navigate(action)
            }
        }

        // Update UI when page changes (Highlight the active icon)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Reset colors to Grey
            val greyColor = ContextCompat.getColor(this, R.color.text_secondary)
            val goldColor = ContextCompat.getColor(this, R.color.imdb_gold)

            setIconColor(binding.btnHome, greyColor)
            setIconColor(binding.btnProfile, greyColor) // Reset Profile Color

            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnHome, goldColor)
                }
                R.id.profileFragment -> {
                    // NEW: Highlight Profile Icon
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnProfile, goldColor)
                }
                else -> {
                    // Hide Bar on other screens (Add/Edit, Details, Favorites)
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabAdd.hide()
                }
            }
        }
    }

    // Helper to tint icons
    private fun setIconColor(imageView: ImageView, color: Int) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color))
    }
}