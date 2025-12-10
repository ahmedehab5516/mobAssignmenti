package com.ahmed.assignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Initialize the UI
        bottomNav = findViewById(R.id.bottom_navigation)

        // 2. Fix Padding (Standard Edge-to-Edge fix)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ui)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Important: Bottom padding is 0 because the Nav Bar handles its own height
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // 3. Load the default screen (Home) so the screen isn't empty on launch
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // 4. THE NAVIGATION LOGIC
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // 5. The Helper Function to Swap Screens
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        // 'fragment_container' is the ID of the FrameLayout in activity_main.xml
        transaction.replace(R.id.bottom_navigation, fragment)
        transaction.commit()
    }
}