package com.example.moviereview

import android.content.Context
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
import com.example.moviereview.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

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

        binding.btnHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.popBackStack(R.id.homeFragment, false)
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

        binding.btnDiscover.setOnClickListener {
            if (navController.currentDestination?.id != R.id.discoverFragment) {
                navController.navigate(R.id.discoverFragment)
            }
        }

        binding.btnFavorites.setOnClickListener {
            if (navController.currentDestination?.id != R.id.favoritesFragment) {
                navController.navigate(R.id.favoritesFragment)
            }
        }

        // Update UI when page changes (Highlight the active icon)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Reset colors to Grey
            val greyColor = ContextCompat.getColor(this, R.color.text_secondary)
            val goldColor = ContextCompat.getColor(this, R.color.imdb_gold)

            setIconColor(binding.btnHome, greyColor)
            setIconColor(binding.btnProfile, greyColor)
            setIconColor(binding.btnFavorites, greyColor)
            setIconColor(binding.btnDiscover, greyColor)

            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnHome, goldColor)
                }

                R.id.profileFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnProfile, goldColor)
                }
                R.id.discoverFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnDiscover, goldColor)
                }
                R.id.favoritesFragment -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fabAdd.show()
                    setIconColor(binding.btnFavorites, goldColor)
                }

                else -> {
                    // Hide Bar on other screens (Add/Edit, Details, Favorites)
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fabAdd.hide()
                }
            }
        }
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.bottomAppBar) { view, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())

            // Adjust the bottom margin of the bar to sit above the nav buttons
            val params = view.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            params.bottomMargin = systemBars.bottom
            view.layoutParams = params

            insets
        }
    }

    // Helper to tint icons
    private fun setIconColor(imageView: ImageView, color: Int) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color))
    }

}