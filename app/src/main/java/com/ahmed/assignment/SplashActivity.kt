// SplashActivity.kt
package com.ahmed.assignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // No layout needed – we redirect instantly

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is already logged in → go to Main
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Not logged in → go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Close Splash so user can't go back
        finish()
    }
}